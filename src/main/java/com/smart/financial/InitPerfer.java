package com.smart.financial;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.TypeReference;
import com.smart.financial.analyzer.PreferredStockList;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class InitPerfer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        final InputStream inputStream = InitPerfer.class.getResourceAsStream("/perfer-fushiluosu.json");
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final JSONReader jsonReader = new JSONReader(bufferedReader);
        final List<String> perferedList = jsonReader.readObject(new TypeReference<List<String>>() {
        });
        PreferredStockList.addAll(perferedList);
    }
}
