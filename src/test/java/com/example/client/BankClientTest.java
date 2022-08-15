package com.example.client;

import com.example.grpc.models.Balance;
import com.example.grpc.models.BalanceCheckRequest;
import com.example.grpc.models.BankServiceGrpc;
import com.example.grpc.models.WithdrawlRequest;
import com.example.server.BankService;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.*;

import java.io.IOException;
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
  public void withdrawAsyncTest() {
    WithdrawlRequest withdrawlRequest =
        WithdrawlRequest.newBuilder().setAccountNumber(5).setAmount(90).build();
    nonblockingStub.withdraw(withdrawlRequest, new WithdrawStreamingResponse());
    Uninterruptibles.sleepUninterruptibly(6, TimeUnit.SECONDS);
  }
}
