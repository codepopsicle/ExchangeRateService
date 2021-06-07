package com.scalabale.springboot;

import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangeRates {

    private static Map<String, Pair<BigDecimal, Integer>> rateMap = new ConcurrentHashMap<>();

    public static void setRateMap(Map<String, Pair<BigDecimal, Integer>> map) {
        rateMap = map;
    }

    public static Map getRateMap(){
        return rateMap;
    }

    public static void updateRateMap(String currency, Pair<BigDecimal, Integer> attributePair){
        rateMap.put(currency, attributePair);
    }

    public static void printMap(){
        rateMap.forEach((k,v) -> System.out.println("Key: " + k + " Rate: " + v.getLeft() +
                " Count: " + v.getRight()));
    }
}
