package com.example.server.ssl;

import com.example.grpc.models.*;
import com.example.server.AccountDatabase;
import com.example.server.CashStreamingRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

  @Override
  public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
    int accountNumber = request.getAccountNumber();

    Balance balance =
        Balance.newBuilder().setAmount(AccountDatabase.getBalance(accountNumber)).build();
    responseObserver.onNext(balance);
    responseObserver.onCompleted();
  }

  @Override
  public void withdraw(WithdrawlRequest request, StreamObserver<Withdrawl> responseObserver) {
    int accountNumber = request.getAccountNumber();
    int amount = request.getAmount();
    int balance = AccountDatabase.getBalance(accountNumber);

    if (balance < amount) {
      Status status =
          Status.FAILED_PRECONDITION.withDescription(
              "Not enough money in account for requested withdrawl. Balance: "
                  + balance
                  + " Amount: "
                  + amount);
      responseObserver.onError(status.asRuntimeException());
      return;
    }

    for (int i = 0; i < (amount / 10); i++) {
      Withdrawl withdrawl = Withdrawl.newBuilder().setValue(10).build();
      responseObserver.onNext(withdrawl);
      AccountDatabase.deductBalance(accountNumber, 10);
    }

    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {
    return new CashStreamingRequest(responseObserver);
  }
}
