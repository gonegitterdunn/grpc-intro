package com.example.server.ssl;

import com.example.server.BankService;
import com.example.server.TransferService;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

public class GrpcServer {

  public static void main(String[] args) throws IOException, InterruptedException {

    SslContext sslContext =
        GrpcSslContexts.configure(
                SslContextBuilder.forServer(
                    new File("/Users/demo_certs/localhost.crt"),
                    new File("/Users/demo_certs/localhost.pem")))
            .build();

    Server server =
        NettyServerBuilder.forPort(6565)
            .executor(Executors.newFixedThreadPool(20))
            .sslContext(sslContext)
            .addService(new BankService())
            .addService(new TransferService())
            .build();

    server.start();
    server.awaitTermination();
  }
}
