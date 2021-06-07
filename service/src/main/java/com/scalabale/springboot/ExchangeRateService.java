package com.scalabale.springboot;

import com.scalabale.springboot.exception.RatesNotInitializedException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@Service
public class ExchangeRateService {

    private static final String ECB_REFERENCE_CURRENCY = "EUR";
    private static final String ECB_CURRENCY_GRAPH_PREFIX = "https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/eurofxref-graph-";
    private static final String ECB_CURRENCY_GRAPH_POSTFIX = ".en.html";

    public BigDecimal getReferenceRateForCurrency(String baseCurrency, String notionalCurrency) throws IllegalArgumentException,
            RatesNotInitializedException{
        final Map<String, Pair<BigDecimal, Integer>> rateMap = ExchangeRates.getRateMap();
        if(rateMap.isEmpty()){
            throw new RatesNotInitializedException("Rates aren't initialized");
        }
        if(!rateMap.containsKey(baseCurrency) || !rateMap.containsKey(notionalCurrency)){
            throw new IllegalArgumentException("Currencies not supported!");
        }
        if(!baseCurrency.equals(ECB_REFERENCE_CURRENCY)){
            throw new IllegalArgumentException("Base currency should be EUR");
        }
        if(notionalCurrency.equals(ECB_REFERENCE_CURRENCY)){
            throw new IllegalArgumentException("Only base currency should be EUR");
        }
        BigDecimal rate = rateMap.get(notionalCurrency).getLeft();
        int accessCount = rateMap.get(notionalCurrency).getRight();
        Pair<BigDecimal, Integer> updatedAttributePair = new ImmutablePair<>(rate, ++accessCount);
        ExchangeRates.updateRateMap(notionalCurrency, updatedAttributePair);

        return rate;
    }

    public BigDecimal getExchangeRateForCurrency(String baseCurrency, String notionalCurrency) throws IllegalArgumentException,
            RatesNotInitializedException{
        final Map<String, Pair<BigDecimal, Integer>> rateMap = ExchangeRates.getRateMap();
        if(rateMap.isEmpty()){
            throw new RatesNotInitializedException("Rates aren't initialized");
        }
        if(!rateMap.containsKey(baseCurrency) || !rateMap.containsKey(notionalCurrency)){
            throw new IllegalArgumentException("Currencies not supported!");
        }
        if(baseCurrency.equals(ECB_REFERENCE_CURRENCY) || notionalCurrency.equals(ECB_REFERENCE_CURRENCY)){
            throw new IllegalArgumentException("Neither base currency nor notional currency should be EUR!");
        }

        return getExchangeRate(baseCurrency, notionalCurrency, rateMap);
    }

    public Map getAvailableCurrencies() throws RatesNotInitializedException{
        if(ExchangeRates.getRateMap().isEmpty()){
            throw new RatesNotInitializedException("Rates aren't initialized");
        }
        return ExchangeRates.getRateMap();
    }

    public BigDecimal convertCurrencyAmount(String baseCurrency, String notionalCurrency, BigDecimal baseAmount) throws IllegalArgumentException,
            RatesNotInitializedException{
        final Map<String, Pair<BigDecimal, Integer>> rateMap = ExchangeRates.getRateMap();
        if(rateMap.isEmpty()){
            throw new RatesNotInitializedException("Rates aren't initialized");
        }
        if(!rateMap.containsKey(baseCurrency) || !rateMap.containsKey(notionalCurrency)){
            throw new IllegalArgumentException("Currencies not supported!");
        }

        return baseAmount.multiply(getExchangeRate(baseCurrency, notionalCurrency, rateMap));
    }

    public URL getGraphURL(String currency) throws MalformedURLException {
        URL graphUrl = new URL(ECB_CURRENCY_GRAPH_PREFIX + currency.toLowerCase() + ECB_CURRENCY_GRAPH_POSTFIX);
        return graphUrl;
    }

    private BigDecimal getExchangeRate(String baseCurrency, String notionalCurrency, Map<String, Pair<BigDecimal, Integer>> rateMap){

        BigDecimal baseRate = rateMap.get(baseCurrency).getLeft();
        int baseAccessCount = rateMap.get(baseCurrency).getRight();
        Pair<BigDecimal, Integer> updatedBaseAttributePair = new ImmutablePair<>(rateMap.get(baseCurrency).getLeft(), ++baseAccessCount);

        BigDecimal notionalRate = rateMap.get(notionalCurrency).getLeft();
        int notionalAccessCount = rateMap.get(notionalCurrency).getRight();
        Pair<BigDecimal, Integer> updatedNotionalAttributePair = new ImmutablePair<>(rateMap.get(notionalCurrency).getLeft(), ++notionalAccessCount);

        ExchangeRates.updateRateMap(baseCurrency, updatedBaseAttributePair);
        ExchangeRates.updateRateMap(notionalCurrency, updatedNotionalAttributePair);

        return notionalRate.divide(baseRate, 5, RoundingMode.CEILING);
    }

}
