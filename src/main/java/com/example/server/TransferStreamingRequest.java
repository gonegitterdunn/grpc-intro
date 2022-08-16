package com.example.server;

import com.example.grpc.models.Account;
import com.example.grpc.models.TransferRequest;
import com.example.grpc.models.TransferResponse;
import com.example.grpc.models.TransferStatus;
import io.grpc.stub.StreamObserver;

public class TransferStreamingRequest implements StreamObserver<TransferRequest> {

  private StreamObserver<TransferResponse> transferResponseStreamObserver;

  public TransferStreamingRequest(StreamObserver<TransferResponse> transferResponseStreamObserver) {
    this.transferResponseStreamObserver = transferResponseStreamObserver;
  }

  @Override
  public void onNext(TransferRequest transferRequest) {
    int fromAccount = transferRequest.getFromAccount();
    int toAccount = transferRequest.getToAccount();
    int amount = transferRequest.getAmount();
    int balance = AccountDatabase.getBalance(fromAccount);
    TransferStatus status = TransferStatus.FAILED;

    if (balance >= amount && fromAccount != toAccount) {
      AccountDatabase.deductBalance(fromAccount, amount);
      AccountDatabase.addBalance(amount, toAccount);
      status = TransferStatus.SUCCESS;
    }

    Account fromAccountInfo =
        Account.newBuilder()
            .setAccountNumber(fromAccount)
            .setAmount(AccountDatabase.getBalance(fromAccount))
            .build();

    Account toAccountInfo =
        Account.newBuilder()
            .setAccountNumber(toAccount)
            .setAmount(AccountDatabase.getBalance(toAccount))
            .build();

    TransferResponse transferResponse =
        TransferResponse.newBuilder()
            .setStatus(status)
            .addAccounts(fromAccountInfo)
            .addAccounts(toAccountInfo)
            .build();

    transferResponseStreamObserver.onNext(transferResponse);
  }

  @Override
  public void onError(Throwable throwable) {}

  @Override
  public void onCompleted() {
    AccountDatabase.printAccountDetails();
    transferResponseStreamObserver.onCompleted();
  }
}
