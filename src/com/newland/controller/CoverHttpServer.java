package com.newland.controller;

import com.google.gson.Gson;
import com.newland.model.ServiceObject;
import com.newland.service.impl.ObjectService;
import com.newland.utils.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class CoverHttpServer  implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            ObjectService objectService = new ObjectService();

            ServiceObject ServiceObject = HttpUtils.getRequestBody(httpExchange, ServiceObject.class);
            Map<String, Object> temp = new HashMap<>();
            try {
                temp.put("resultData", objectService.selectHis(ServiceObject));
                temp.put("resultCode", 1);
            } catch (Exception e) {
                e.printStackTrace();
                temp.put("resultCode", 0);
            }
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.getResponseHeaders().add("Content-Type","application/json;charset=UTF-8");
            OutputStream responseBody = httpExchange.getResponseBody();

            responseBody.write(new Gson().toJson(temp).getBytes());
            responseBody.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
