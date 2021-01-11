package com.newland;

import com.newland.controller.CoverHttpServer;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class OptimizeMain {

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(12011), 0);
        httpServer.createContext("/comparison", new CoverHttpServer());
        httpServer.start();
    }

}
