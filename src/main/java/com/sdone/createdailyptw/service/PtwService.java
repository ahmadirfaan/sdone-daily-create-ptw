package com.sdone.createdailyptw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdone.createdailyptw.entity.ApprovalData;
import com.sdone.createdailyptw.entity.PtwData;
import com.sdone.createdailyptw.entity.WizardEnum;
import com.sdone.createdailyptw.entity.WizardStatusEnum;
import com.sdone.createdailyptw.entity.wizard6.WizardEnam;
import com.sdone.createdailyptw.entity.wizard7.WizardTujuh;
import com.sdone.createdailyptw.exception.BadRequestException;
import com.sdone.createdailyptw.grpc.client.Ptw130Client;
import com.sdone.createdailyptw.grpc.client.TokenValidatorServiceClient;
import com.sdone.createdailyptw.model.CreateDailyPtw;
import com.sdone.createdailyptw.model.FieldConstant;
import com.sdone.createdailyptw.repository.ApprovalDataRepository;
import com.sdone.createdailyptw.repository.PtwDataRepository;
import net.sumdev.projectone.database.ptw130.Enum130;
import net.sumdev.projectone.database.ptw130.Ptw130Wizard6;
import net.sumdev.projectone.database.ptw130.Ptw130Wizard7;
import net.sumdev.projectone.database.user.UserOuterClass.UserWithRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.sumdev.projectone.database.ptw130.Ptw130.CreateDailyRequest;
import static net.sumdev.projectone.database.ptw130.Ptw130.DataDaily;
import static net.sumdev.projectone.database.user.UserOuterClass.Role;
import static net.sumdev.projectone.security.TokenValidator.ValidateResponse.Status;

@Service
public class PtwService {

    @Value("${com.sdone.createdailyPtw.isTestMode}")
    private boolean testMode;

    @Autowired
    private PtwDataRepository ptwDataRepository;

    @Autowired
    private TokenValidatorServiceClient tokenValidatorServiceClient;

    @Autowired
    private Ptw130Client ptw130Client;

    @Autowired
    private ApprovalDataRepository approvalDataRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    public Map<String, Object> createDailyPtw(CreateDailyPtw request) {

        var result = new HashMap<String, Object>();

        var validateToken = tokenValidatorServiceClient.validateToken(request.getToken());
        if (validateToken.getStatus() != Status.VALID) {
            returnNotAuthorized(result);
            return result;
        }

        var isPermissionValid = checkPermissionRoles(validateToken.getUserWithRoles());
        if (!isPermissionValid) {
            returnNotAuthorized(result);
            return result;
        }

        if (testMode) {

            var approvalData = approvalDataRepository.findByUuid(request.getUuid());
            checkApprovalData(approvalData);
            var isError = true;
            List<WizardEnum> wizardEnumList = List.of(WizardEnum.WIZARD_6, WizardEnum.WIZARD_7);
            if (request.getWizardStatus() == WizardStatusEnum.DRAFT && wizardEnumList.contains(request.getWizardNo())) {
                isError = false;
            }

            if (request.getWizardStatus() == WizardStatusEnum.SUBMIT && request.getWizardNo() == WizardEnum.WIZARD_8) {
                isError = false;
                wizardEnumList.forEach(wizardEnum -> {
                    var wizard = ptwDataRepository.findByUuidAndAndWizard(request.getUuid(), wizardEnum);
                    if (wizard.isEmpty()) {
                        throw new BadRequestException(wizardEnum.name() + " is not exist with status DRAFT");
                    }

                    PtwData ptwData = wizard.get();
                    if (ptwData.getStatus() != WizardStatusEnum.DRAFT) {
                        throw new BadRequestException(wizardEnum.name() + " is not exist with status DRAFT");
                    }

                    ptwData.setStatus(WizardStatusEnum.SUBMIT);
                    ptwDataRepository.save(ptwData);
                });
            }

            if (isError) {
                throw new BadRequestException("Not valid wizardNo and wizardStatus");
            }

            populateDbH2(request);

            result.put(FieldConstant.HTTP_STATUS, HttpStatus.OK.value());
            result.put(FieldConstant.RESULT, "success put to DB");
            result.put("uuid", request.getUuid());
        } else {
            try {
                var createDailyRequest = createRequest(request, validateToken.getUserWithRoles());
                if (createDailyRequest == null) {
                    throw new BadRequestException("Invalid WizardNo");
                }
                var dailyPtw = ptw130Client.createDailyPtw(createDailyRequest);
                result.put(FieldConstant.HTTP_STATUS, HttpStatus.OK.value());
                result.put(FieldConstant.RESULT, dailyPtw.getResult());
                result.put("uuid", dailyPtw.getUuid());
            } catch (JsonProcessingException e) {
                throw new BadRequestException("Invalid Data Ptw : " + e.getMessage());
            }
        }
        return result;
    }

    private void populateDbH2(CreateDailyPtw request) {
        PtwData ptwData = new PtwData();
        ptwData.setTimestamp(Instant.now().getEpochSecond());
        ptwData.setWizard(request.getWizardNo());
        ptwData.setStatus(request.getWizardStatus());
        ptwData.setUuid(request.getUuid());
        ptwData.setData(request.getDataDaily().toString());
        ptwDataRepository.save(ptwData);
    }

    private CreateDailyRequest createRequest(CreateDailyPtw request, UserWithRoles userWithRoles) throws JsonProcessingException {
        long epochSecond = Instant.now().getEpochSecond();
        String uuid = request.getUuid();
        String username = userWithRoles.getUsername();
        var jsonNode = request.getDataDaily();
        switch (request.getWizardNo()) {
            case WIZARD_6:
                WizardEnam wizardEnam = objectMapper.readValue(jsonNode.toString(), WizardEnam.class);
                return CreateDailyRequest
                        .newBuilder()
                        .setTimestamp(epochSecond)
                        .setUuid(uuid)
                        .setUsername(username)
                        .setWizardNo(Enum130.WizardNo.WIZARD_6)
                        .setDataDaily(DataDaily.newBuilder()
                                .setWizardEnam(Ptw130Wizard6.DataPtwWizardEnam.newBuilder()
                                        .setTimestamp(epochSecond)
                                        .setUsername(username)
                                        .setUuid(uuid)
                                        .setImplementasi(Ptw130Wizard6.Implementasi.newBuilder()
                                                .setTanggalPtw(wizardEnam.getImplementasi().getTanggalPtw())
                                                .setTitikAksesLokasi(wizardEnam.getImplementasi().getTitikAksesLokasi())
                                                .setJumlahPekerja(wizardEnam.getImplementasi().getJumlahPekerja())
                                                .addAllFotoPekerjaan(wizardEnam.getImplementasi().getFotoPekerjaan())
                                                .build())
                                        .build())
                                .build())
                        .build();
            case WIZARD_7:
                var wizardTujuh = objectMapper.readValue(jsonNode.toString(), WizardTujuh.class);
                return CreateDailyRequest
                        .newBuilder()
                        .setTimestamp(epochSecond)
                        .setUuid(uuid)
                        .setUsername(username)
                        .setWizardNo(Enum130.WizardNo.WIZARD_7)
                        .setDataDaily(DataDaily.newBuilder()
                                .setWizardTujuh(Ptw130Wizard7.DataPtwWizardTujuh.newBuilder()
                                        .setTimestamp(epochSecond)
                                        .setUsername(username)
                                        .setUuid(uuid)
                                        .setSafety(Ptw130Wizard7.WorksiteSafety.newBuilder()
                                                .setSortCircuit(wizardTujuh.getSafety().getSortCircuit())
                                                .setMarkerBoards(wizardTujuh.getSafety().getMarkerBoards())
                                                .setEquipmentIsolation(wizardTujuh.getSafety().getEquipmentIsolation())
                                                .setPengaturanKomunikasi(wizardTujuh.getSafety().getPengaturanKomunikasi())
                                                .setTitikKeluarDarurat(wizardTujuh.getSafety().getTitikKeluarDarurat())
                                                .build())
                                        .build())
                                .build())
                        .build();
            default:
                return null;
        }
    }


    private static void returnNotAuthorized(HashMap<String, Object> result) {
        result.put(FieldConstant.HTTP_STATUS, HttpStatus.UNAUTHORIZED.value());
        result.put(FieldConstant.RESULT, "error");
    }


    private boolean checkPermissionRoles(UserWithRoles userWithRoles) {
        var isAllowedRole = false;
        var isAllowedPermission = false;
        for (int i = 0; i < userWithRoles.getRolesList().size(); i++) {
            Role roles = userWithRoles.getRoles(i);
            if (!isAllowedRole) {
                isAllowedRole = roles.getGroup().equalsIgnoreCase("MAINTENANCE");
            }
            for (String permission : roles.getPermissionList()) {
                if (!isAllowedPermission) {
                    isAllowedPermission = permission.equals("createPtw");
                }
            }

            if (isAllowedRole && isAllowedPermission) {
                break;
            }
        }
        return isAllowedRole && isAllowedPermission;
    }

    private void checkApprovalData(List<ApprovalData> approvalData) {
        var approvalDataVp = approvalData.stream().filter(data -> data.getStatus().equalsIgnoreCase(WizardStatusEnum.APPROVE.toString())
                && data.getRole().equalsIgnoreCase("VP")).findAny();
        var approvalDataJm = approvalData.stream().filter(data -> data.getStatus().equalsIgnoreCase(WizardStatusEnum.APPROVE.toString())
                && data.getRole().equalsIgnoreCase("JM")).findAny();
        var approvalDataShe = approvalData.stream().filter(data -> data.getStatus().equalsIgnoreCase(WizardStatusEnum.APPROVE.toString())
                && data.getRole().equalsIgnoreCase("SHE")).findAny();

        if (approvalDataVp.isEmpty() || approvalDataJm.isEmpty() || approvalDataShe.isEmpty()) {

            throw new BadRequestException("Approval is not exist ");
        }
    }
}

