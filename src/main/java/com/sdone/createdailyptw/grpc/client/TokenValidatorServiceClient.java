package com.sdone.createdailyptw.grpc.client;

import com.sdone.createdailyptw.exception.GrpcClientException;
import com.sdone.createdailyptw.exception.PermissionDeniedException;
import grpc.health.v1.HealthGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.sumdev.projectone.database.user.UserOuterClass;
import net.sumdev.projectone.security.TokenValidatorServiceGrpc;
import org.springframework.stereotype.Component;

import static grpc.health.v1.HealthProtocol.HealthCheckRequest;
import static grpc.health.v1.HealthProtocol.HealthCheckResponse;
import static net.sumdev.projectone.security.TokenOuterClass.Token;
import static net.sumdev.projectone.security.TokenValidator.ValidateRequest;
import static net.sumdev.projectone.security.TokenValidator.ValidateResponse;

@Component
@Slf4j(topic = "GRPC-SERVICE")
public class TokenValidatorServiceClient {

    @GrpcClient("tokenvalidator-service")
    private HealthGrpc.HealthBlockingStub healthTokenValidator;

    @GrpcClient("tokenvalidator-service")
    private TokenValidatorServiceGrpc.TokenValidatorServiceBlockingStub tokenvalidatorService;

    public boolean checkStatus() {
        try {
            HealthCheckResponse check = healthTokenValidator.check(HealthCheckRequest.newBuilder().setService("").build());
            return check.getStatus().equals(HealthCheckResponse.ServingStatus.SERVING);
        } catch (Throwable t) {
            return false;
        }
    }

    public ValidateResponse validateToken(String token) {
        var validateRequest = ValidateRequest.newBuilder()
                .setToken(Token.newBuilder().setToken(token).build())
                .build();
        log.info("{} request : {} ", TokenValidatorServiceGrpc.SERVICE_NAME, validateRequest);
        try {
//            return ValidateResponse
//                    .newBuilder()
//                    .setStatus(ValidateResponse.Status.VALID)
//                    .setUserWithRoles(UserOuterClass.UserWithRoles.newBuilder()
//                            .setUsername("ahmadirfaan")
//                            .addRoles(UserOuterClass.Role.newBuilder()
//                                    .addPermission("listPtw").build())
//                            .build())
//                    .build();
            return tokenvalidatorService.validate(validateRequest);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode().equals(Status.Code.PERMISSION_DENIED)) {
                //means not authorized from token validator service
                throw new PermissionDeniedException("permission denied");
            }
            throw new GrpcClientException(TokenValidatorServiceGrpc.SERVICE_NAME + " " +
                    e.getStatus().getCode() + " " + e.getStatus().getDescription());
        } catch (Throwable e) {
            throw new GrpcClientException("token validator error");
        }
    }
}
