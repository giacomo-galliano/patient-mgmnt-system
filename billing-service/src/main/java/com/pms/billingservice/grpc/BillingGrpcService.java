package com.pms.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest billingRequest, StreamObserver<BillingResponse> responseObserver) {
        log.info("createBillingAccount request received {}", billingRequest.toString());
        // implement business logic here - calculus or save to database

        BillingResponse billingResponse = BillingResponse.newBuilder()
                .setAccountId("1234")
                .setStatus("ACTIVE")
                .build();

        // send response to the client (could eventually send more than one!)
        responseObserver.onNext(billingResponse);
        // complete the request
        responseObserver.onCompleted();
    }
}
