package com.user.reward.api.thirdparty;

import com.user.reward.constant.Parameters;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Chaklader on 2019-04-11.
 */
@Configuration
@EnableScheduling
public class CurrencyUtilities {


    private Map<String, Double> currencyMap = null;
    private static final int HOUR = 60 * 60 * 1000;


    /**
     * get the curerny map with the currency name as the key and
     * their exchange rate against the EUR as the value
     *
     * @return
     */
    public Map<String, Double> getCurrencyMap() {
        return currencyMap;
    }

    /**
     * @param urlToRead
     * @return
     * @throws Exception
     */
    private String getCurrencyExchangeJsonData(String urlToRead) throws Exception {

        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;

        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        conn.disconnect();
        return result.toString();
    }


    /**
     * @return
     * @throws Exception
     */
    private Map<String, Double> getConvertionRates() throws Exception {


        String s = "https://openexchangerates.org/api/latest.json?app_id=" + Parameters.API_KEY;

        String response = getCurrencyExchangeJsonData(s);
        final JSONObject obj = new JSONObject(response.trim());

        /**
         * find the conversion rates for all the currencies against the USD from the
         * "openexchangerates.org" API
         */
        JSONObject rates = obj.getJSONObject("rates");

        Iterator<String> keys = rates.keys();
        Map<String, Double> map = new HashMap<>();
        double USD_TO_EUR = Double.parseDouble(rates.get("EUR").toString());

        map.put("USD", (1.0 / USD_TO_EUR));

        keys.forEachRemaining(k -> {
            double v = Double.parseDouble(rates.get(k).toString());
            map.put(k, v / USD_TO_EUR);
        });

//        for (Map.Entry<String, Double> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }

        return map;
    }


    /*
     * schedule the the currency update each hour
     * */
    @Scheduled(fixedRate = HOUR)
    public void updateCurrencyExchangeRate() throws Exception {
        currencyMap = getConvertionRates();
    }
}
