package zad1; /**
 *
 *  @author Konan Aliaksandr  S28228
 *
 */


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



public class Service {
    private final String country;
    private static final Map<String, String> countryCurrencyMap = new HashMap<>();

    static { initializeMapWithValues();}

    public Service(String kraj) {
        this.country = kraj;
    }

    public static void initializeMapWithValues(){
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                String countryCode = locale.getCountry();
                if (!countryCode.isEmpty()) {
                    countryCurrencyMap.put(locale.getDisplayCountry(), currency.getCurrencyCode());
                }
            } catch (Exception ignored) {

            }
        }
    }

    public String getWeather(String miasto)  {
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + miasto + "&appid=d1b7d82726488479dde2f2cfbb767722");
            InputStream is = url.openStream();
            int c;
            StringBuilder sb = new StringBuilder();
            while ((c = is.read()) != -1)
                sb.append((char)c);
            return sb.toString();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    Double getRateFor(String kod_waluty) {
        try {
            URL url = new URL("https://v6.exchangerate-api.com/v6/a4eafb0f5ccc2bb5b45782c5/pair/" + countryCurrencyMap.get(country) + "/" + kod_waluty);
            InputStream is = url.openStream();
            int c;
            StringBuilder sb = new StringBuilder();
            while ((c = is.read()) != -1)
                sb.append((char)c);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject;
            try {
                jsonObject = (JSONObject) parser.parse(String.valueOf(sb));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return ((Number) jsonObject.get("conversion_rate")).doubleValue();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    Double getNBPRate() {
        Double a = this.getNBPRate("a");
        if (a != null) {
            return 1/a;
        }
        Double b = this.getNBPRate("b");
        if (b != null) {
            return 1/b;
        }
        return 1/this.getNBPRate("c");
    }


    public Double getNBPRate(String table) {
        String countryCurrencyCode = countryCurrencyMap.get(country);
        if (countryCurrencyCode.equals("PLN")) {
            return 1d;
        }
        try {
            JSONArray ratesArray = getJsonArray(table);
            for (Object obj : ratesArray) {
                    JSONObject rateObj = (JSONObject) obj;
                    String currencyCode = (String) rateObj.get("code");
                    double mid = (double) rateObj.get("mid");
                    if (currencyCode.equals(countryCurrencyCode)) {
                        return mid;
                    }
                }
            return null;
        } catch (IOException | ParseException e){
            throw new RuntimeException(e);
        }
    }

    private static JSONArray getJsonArray(String table) throws IOException, ParseException {
        URL url = new URL("http://api.nbp.pl/api/exchangerates/tables/" + table + "/");
        InputStream is = url.openStream();
        int c;
        StringBuilder sb = new StringBuilder();
        while ((c = is.read()) != -1)
            sb.append((char)c);
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(String.valueOf(sb));
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        return (JSONArray) jsonObject.get("rates");
    }
}



