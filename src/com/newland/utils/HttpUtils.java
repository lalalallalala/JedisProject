package com.newland.utils;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpUtils {

    public static <T> T getRequestBody(HttpExchange httpExchange,Class<T> clazz) throws IOException {
        InputStream requestBody = httpExchange.getRequestBody();
        InputStreamReader inputStreamReader = new InputStreamReader(requestBody);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuffer = new StringBuilder();
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            stringBuffer.append(str.trim());
        }
        return new Gson().fromJson(stringBuffer.toString(),clazz);
    }

}
