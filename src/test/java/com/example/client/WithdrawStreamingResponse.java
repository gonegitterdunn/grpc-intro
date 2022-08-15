package com.example.client;

import com.example.grpc.models.Withdrawl;
import io.grpc.stub.StreamObserver;

public class WithdrawStreamingResponse implements StreamObserver<Withdrawl> {
  @Override
  public void onNext(Withdrawl withdrawl) {
    System.out.println("Received async : " + withdrawl.getValue());
  }

  @Override
  public void onError(Throwable throwable) {
    System.out.println(throwable.getMessage());
  }

  @Override
  public void onCompleted() {
    System.out.println("Server is done!");
  }
}
