package com.example.server;

import com.example.grpc.models.Balance;
import com.example.grpc.models.BalanceCheckRequest;
import com.example.grpc.models.BankServiceGrpc;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {
  public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
    int accountNumber = request.getAccountNumber();

    Balance balance =
        Balance.newBuilder().setAmount(AccountDatabase.getBalance(accountNumber)).build();
    responseObserver.onNext(balance);
    responseObserver.onCompleted();
  }
}
