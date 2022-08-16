package com.example.server;

import com.example.grpc.models.TransferRequest;
import com.example.grpc.models.TransferResponse;
import com.example.grpc.models.TransferServiceGrpc;
import io.grpc.stub.StreamObserver;

public class TransferService extends TransferServiceGrpc.TransferServiceImplBase {

  @Override
  public StreamObserver<TransferRequest> transfer(
      StreamObserver<TransferResponse> responseObserver) {
    return new TransferStreamingRequest(responseObserver);
  }
}
