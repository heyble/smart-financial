package com.smart.financial.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PreferredStockList {

    private static List<String> preferred = new ArrayList<>(1000);

    private PreferredStockList(){

    }

    public static void add(String symbol){
        preferred.add(symbol);
    }

    public static void addAll(Collection<String> c){
        preferred.addAll(c);
    }

    public static List<String> getPreferred() {
        return preferred;
    }
}
