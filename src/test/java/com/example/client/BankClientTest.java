package com.example.client;

import com.example.grpc.models.*;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.CountDownLatch;

// creates instance variables in setup instead of statics
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {
  private BankServiceGrpc.BankServiceBlockingStub blockingStub;
  private BankServiceGrpc.BankServiceStub nonblockingStub;

  @BeforeAll
  public void setup() throws SSLException {

    SslContext sslContext =
        GrpcSslContexts.forClient().trustManager(new File("/Users/demo_certs/ca.cert.pem")).build();

    ManagedChannel managedChannel =
        NettyChannelBuilder.forAddress("localhost", 6565).sslContext(sslContext).build();

    blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
    nonblockingStub = BankServiceGrpc.newStub(managedChannel);
  }

  @Test
  public void balanceTest() {
    BalanceCheckRequest balanceCheckRequest =
        BalanceCheckRequest.newBuilder().setAccountNumber(4).build();

    Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
    System.out.println(balance);
    Assertions.assertEquals(100, balance.getAmount());
  }

  @Test
  public void withdrawTest() {
    WithdrawlRequest withdrawlRequest =
        WithdrawlRequest.newBuilder().setAccountNumber(9).setAmount(10).build();

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
