package com.example.client;

import com.example.grpc.models.Withdrawl;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class WithdrawStreamingResponse implements StreamObserver<Withdrawl> {

  private CountDownLatch latch;

  public WithdrawStreamingResponse(CountDownLatch latch) {
    this.latch = latch;
  }

  @Override
  public void onNext(Withdrawl withdrawl) {
    System.out.println("Received async : " + withdrawl.getValue());
  }

  @Override
  public void onError(Throwable throwable) {
    System.out.println(throwable.getMessage());
    latch.countDown();
  }

  @Override
  public void onCompleted() {
    System.out.println("Server is done!");
    latch.countDown();
  }
}
