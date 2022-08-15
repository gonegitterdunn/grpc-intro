package com.example.client;

import com.example.grpc.models.Balance;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class BalanceStreamObserver implements StreamObserver<Balance> {

  private CountDownLatch countDownLatch;

  public BalanceStreamObserver(CountDownLatch countDownLatch) {
    this.countDownLatch = countDownLatch;
  }

  @Override
  public void onNext(Balance balance) {
    System.out.println("Final balance: " + balance.getAmount());
    countDownLatch.countDown();
  }

  @Override
  public void onError(Throwable throwable) {}

  @Override
  public void onCompleted() {
    System.out.println("Server is done");
    countDownLatch.countDown();
  }
}
