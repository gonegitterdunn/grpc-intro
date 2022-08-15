package com.example.server;

import com.example.grpc.models.Balance;
import com.example.grpc.models.DepositRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CashStreamingRequest implements StreamObserver<DepositRequest> {

  private StreamObserver<Balance> balanceStreamObserver;
  private int accountBalance;

  public CashStreamingRequest(StreamObserver<Balance> balanceStreamObserver) {
    this.balanceStreamObserver = balanceStreamObserver;
  }

  @Override
  public void onNext(DepositRequest depositRequest) {
    int accountNumber = depositRequest.getAccountNumber();
    int amount = depositRequest.getAmount();
    accountBalance = AccountDatabase.addBalance(accountNumber, amount);
  }

  @Override
  public void onError(Throwable throwable) {
    Status status = Status.FAILED_PRECONDITION.withDescription("Error while depositing");
    balanceStreamObserver.onError(status.asRuntimeException());
    return;
  }

  @Override
  public void onCompleted() {
    Balance balance = Balance.newBuilder().setAmount(accountBalance).build();
    balanceStreamObserver.onNext(balance);
    balanceStreamObserver.onCompleted();
  }
}
