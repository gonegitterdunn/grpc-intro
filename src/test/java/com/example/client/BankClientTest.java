package com.example.client;

import com.example.grpc.models.Balance;
import com.example.grpc.models.BalanceCheckRequest;
import com.example.grpc.models.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

// creates instance variables in setup instead of statics
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {
  private BankServiceGrpc.BankServiceBlockingStub blockingStub;

  @BeforeAll
  public void setup() {
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();

    blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
  }

  @Test
  public void balanceTest() {
    BalanceCheckRequest balanceCheckRequest =
        BalanceCheckRequest.newBuilder().setAccountNumber(4).build();

    Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
    System.out.println(balance);
  }
}
