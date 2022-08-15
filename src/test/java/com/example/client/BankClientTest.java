package com.example.client;

import com.example.grpc.models.*;
import com.example.server.BankService;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// creates instance variables in setup instead of statics
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {
  private BankServiceGrpc.BankServiceBlockingStub blockingStub;
  private BankServiceGrpc.BankServiceStub nonblockingStub;

  @BeforeAll
  public void setup() {
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();

    blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
    nonblockingStub = BankServiceGrpc.newStub(managedChannel);
  }

  @Test
  public void balanceTest() {
    BalanceCheckRequest balanceCheckRequest =
        BalanceCheckRequest.newBuilder().setAccountNumber(4).build();

    Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
    System.out.println(balance);
    Assertions.assertEquals(40, balance.getAmount());
  }

  @Test
  public void withdrawTest() {
    WithdrawlRequest withdrawlRequest =
        WithdrawlRequest.newBuilder().setAccountNumber(9).setAmount(80).build();

    this.blockingStub
        .withdraw(withdrawlRequest)
        .forEachRemaining(money -> Assertions.assertEquals(10, money.getValue()));
  }

  @Test
  public void withdrawAsyncTest() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    WithdrawlRequest withdrawlRequest =
        WithdrawlRequest.newBuilder().setAccountNumber(5).setAmount(90).build();
    nonblockingStub.withdraw(withdrawlRequest, new WithdrawStreamingResponse(countDownLatch));
    countDownLatch.await();
  }

  @Test
  public void cashStreamingRequest() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    StreamObserver<DepositRequest> streamObserver =
        this.nonblockingStub.cashDeposit(new BalanceStreamObserver(countDownLatch));
    for (int i = 0; i < 10; i++) {
      DepositRequest depositRequest =
          DepositRequest.newBuilder().setAccountNumber(8).setAmount(10).build();
      streamObserver.onNext(depositRequest);
    }
    streamObserver.onCompleted();
    countDownLatch.await();
  }
}
