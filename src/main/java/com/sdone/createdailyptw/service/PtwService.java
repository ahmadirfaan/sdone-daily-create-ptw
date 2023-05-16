package com.sdone.createdailyptw.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdone.createdailyptw.entity.ApprovalData;
import com.sdone.createdailyptw.entity.WizardStatusEnum;
import com.sdone.createdailyptw.exception.BadRequestException;
import com.sdone.createdailyptw.grpc.client.TokenValidatorServiceClient;
import com.sdone.createdailyptw.model.CreateDailyPtw;
import com.sdone.createdailyptw.model.FieldConstant;
import com.sdone.createdailyptw.repository.ApprovalDataRepository;
import com.sdone.createdailyptw.repository.PtwDataRepository;
import net.sumdev.projectone.database.user.UserOuterClass.UserWithRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        }
        return result;
    }


    private static void returnNotAuthorized(HashMap<String, Object> result) {
        result.put(FieldConstant.HTTP_STATUS, HttpStatus.UNAUTHORIZED.value());
        result.put(FieldConstant.RESULT, "error");
    }


    private boolean checkPermissionRoles(UserWithRoles userWithRoles) {
        for (int i = 0; i < userWithRoles.getRolesList().size(); i++) {
            Role roles = userWithRoles.getRoles(i);
            for (String permission : roles.getPermissionList()) {
                boolean createPtw = permission.equals("listPtw");
                if (createPtw) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkApprovalData(List<ApprovalData> approvalData) {
        var approvalDataVp = approvalData.stream().filter(data -> data.getStatus().equalsIgnoreCase(WizardStatusEnum.APPROVE.toString())
                && data.getRole().equalsIgnoreCase("VP")).findAny();
        var approvalDataJm =approvalData.stream().filter(data -> data.getStatus().equalsIgnoreCase(WizardStatusEnum.APPROVE.toString())
                && data.getRole().equalsIgnoreCase("JM")).findAny();
        var approvalDataShe = approvalData.stream().filter(data -> data.getStatus().equalsIgnoreCase(WizardStatusEnum.APPROVE.toString())
                && data.getRole().equalsIgnoreCase("SHE")).findAny();

        if(approvalDataVp.isEmpty() || approvalDataJm.isEmpty() || approvalDataShe.isEmpty()) {

            throw new BadRequestException("Approval is not exist ");
        }
    }
}

