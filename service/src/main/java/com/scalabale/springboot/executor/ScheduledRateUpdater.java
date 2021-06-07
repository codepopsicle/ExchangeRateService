package com.scalabale.springboot.executor;

import com.scalabale.springboot.ExchangeRates;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScheduledRateUpdater implements Runnable{

    @Autowired
    private Environment environment;

    private static final String ECB_RATE_URL_PROPERTY = "ecb.rate_url";

    private static final String ECB_RATE_URL_TOPNODE = "rates";

    private static final String API_KEY_PROPERTY = "ecb.api_key";

    private void downloadRates(){
        try {

            final URL url = new URL(createUrlString());
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            final BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            final StringBuilder outputBuilder = new StringBuilder(1024);
            String output;
            while ((output = br.readLine()) != null) {
                outputBuilder.append(output);
            }
            conn.disconnect();

            /* Converting to JSON object */
            final JSONObject rates = new JSONObject(outputBuilder.toString()).getJSONObject(ECB_RATE_URL_TOPNODE);
            createExchangeRates(rates);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createExchangeRates(JSONObject exchangeRates){
        final Map<String, Pair<BigDecimal, Integer>> updatedRateMap = new ConcurrentHashMap<>();
        exchangeRates.keys().forEachRemaining(symbol -> {
                    final BigDecimal rate = new BigDecimal(exchangeRates.get(symbol).toString());
                    updatedRateMap.put(symbol, new MutablePair<>(rate, 0));
                    if(ExchangeRates.getRateMap().isEmpty()){
                        final Pair<BigDecimal, Integer> attributePair = new ImmutablePair<>(rate, 0);
                        updatedRateMap.put(symbol, attributePair);
                    }
                    else{
                        final Map<String, Pair<BigDecimal, Integer>> oldRateMap = ExchangeRates.getRateMap();
                        int accessCount = oldRateMap.get(symbol).getRight();
                        final Pair<BigDecimal, Integer> attributePair = new ImmutablePair<>(rate, accessCount);
                        updatedRateMap.put(symbol, attributePair);
                    }
                ExchangeRates.setRateMap(updatedRateMap);

                });
    }

    private String createUrlString() {
        final StringBuilder urlBuilder = new StringBuilder(1024);
        urlBuilder.append(environment.getProperty(ECB_RATE_URL_PROPERTY));
        urlBuilder.append("?apikey=");
        urlBuilder.append(environment.getProperty(API_KEY_PROPERTY));

        return urlBuilder.toString();
    }

    @Override
    public void run() {
        downloadRates();
    }
}
