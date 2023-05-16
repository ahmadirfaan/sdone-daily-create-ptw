package com.sdone.createdailyptw.grpc.client;

import com.sdone.createdailyptw.exception.GrpcClientException;
import com.sdone.createdailyptw.exception.PermissionDeniedException;
import grpc.health.v1.HealthGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.sumdev.projectone.database.ptw130.Ptw130.CreateDailyRequest;
import net.sumdev.projectone.database.ptw130.Ptw130DBServiceGrpc;
import org.springframework.stereotype.Component;

import static net.sumdev.projectone.database.ptw130.Ptw130.*;

@Component
@Slf4j(topic = "GRPC-SERVICE")
public class Ptw130Client {

    @GrpcClient("createDailyPtw-service")
    private HealthGrpc.HealthBlockingStub healthTokenValidator;

    @GrpcClient("createDailyPtw-service")
    private Ptw130DBServiceGrpc.Ptw130DBServiceBlockingStub ptw130DBServiceBlockingStub;

    public Response createDailyPtw(CreateDailyRequest createDailyRequest) {
        log.info("{} request : {} ", Ptw130DBServiceGrpc.SERVICE_NAME, createDailyRequest);
        try {
        return ptw130DBServiceBlockingStub.createDaily(createDailyRequest);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode().equals(Status.Code.PERMISSION_DENIED)) {
                //means not authorized from token validator service
                throw new PermissionDeniedException("permission denied");
            }
            throw new GrpcClientException(Ptw130DBServiceGrpc.SERVICE_NAME + " " +
                    e.getStatus().getCode() + " " + e.getStatus().getDescription());
        } catch (Throwable e) {
            throw new GrpcClientException("token validator error");
        }
    }
}
