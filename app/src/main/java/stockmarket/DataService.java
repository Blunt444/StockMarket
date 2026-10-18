package stockmarket;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
// import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.ArrayList;

public class DataService {

    private static String apiKey;
    private static long lastCheck = 0;
    private static final long maxPingTime = 5 * 60 * 1000;
    private static final long maxPriceTime = 3 * 60 * 1000;

    private static final Map<String, Data> data = new TreeMap<>();

    public Double getPrice(String name) {
        Data val = data.get(name);

        if (val == null)
            return null;

        long now = System.currentTimeMillis();

        if (val.price == 0 || now - val.lastChecked > maxPriceTime) {
            fetchPrice(name, val);
            val.lastChecked = now;
        }

        return val.price;
    }

    private static double getValue(JsonObject o, String key, String stockName) {
        try {
            return o.get(key).getAsDouble();
        } catch (RuntimeException e) {
            throw new IllegalStateException("No price data available for " + stockName.toUpperCase(), e);
        }
    }

    private static void fetchPrice(String stockName, Data d) {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(5)).build();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://finnhub.io/api/v1/quote?symbol=" + stockName.toUpperCase()))
                .header("X-Finnhub-Token", apiKey)
                .timeout(Duration.ofSeconds(10)).GET().build();

        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            String body = res.body();

            int status = res.statusCode();

            if (status == 200) {
                // System.out.println(res.body());
                JsonObject obj = JsonParser.parseString(body).getAsJsonObject();
                double price = getValue(obj, "c", stockName);

                if (price <= 0) {
                    throw new IllegalStateException("No price available for " + stockName.toUpperCase());
                }

                d.price = price;
                d.open = getValue(obj, "o", stockName);
                d.high = getValue(obj, "h", stockName);
                d.low = getValue(obj, "l", stockName);
                d.change = getValue(obj, "d", stockName);
                d.dp = getValue(obj, "dp", stockName);
                d.pc = getValue(obj, "pc", stockName);
            }

            if (status == 401) {
                System.out.println("API key rejected by Finnhub.");
                System.exit(0);
            }

            if (status == 429) {
                // System.out.println("Finnhub rate limit reached. Slow down for a minute.");
                throw new IllegalStateException("Rate limit reached. Can't process this at the moment");
            }

            if (status == 403) {
                System.out.println("Your Finnhub plan doesn't allow this request. Exiting.");
                System.exit(0);
            }

            if (status == 422) {
                System.out.println("Finnhub couldn't process the request (status 422). Exiting.");
                System.exit(0);
            }
            if (status >= 500) {
                System.out.println("Finnhub is having problems (status" + status + "). Exiting.");
                System.exit(0);
            }

        } catch (InterruptedException e) {
            System.out.println("No connection");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("No connection");
            System.exit(0);
        }

        throw new IllegalStateException("Unexpected response from Finnhub");
    }

    public static void callApiKey() {
        if (!data.isEmpty())
            return;

        try {
            System.out.println("Loading Symbols from the API... Please wait for a few minutes");

            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS)
                    .connectTimeout(Duration.ofSeconds(5)).build();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://finnhub.io/api/v1/stock/symbol?exchange=US"))
                    .header("X-Finnhub-Token", apiKey)
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            int status = res.statusCode();

            if (status == 200) {
                JsonArray arr = JsonParser.parseString(res.body()).getAsJsonArray();
                setData(arr);
            }

            if (status == 401) {
                System.out.println("API key rejected by Finnhub.");
                System.exit(0);
            }

            if (status == 429) {
                System.out.println("Finnhub rate limit reached. Slow down for a minute.");
            }

            if (status == 403) {
                System.out.println("Your Finnhub plan doesn't allow this request. Exiting.");
                System.exit(0);
            }

            if (status == 422) {
                System.out.println("Finnhub couldn't process the request (status 422). Exiting.");
                System.exit(0);
            }
            if (status >= 500) {
                System.out.println("Finnhub is having problems (status" + status + "). Exiting.");
                System.exit(0);
            }

        } catch (InterruptedException e) {
            System.out.print("No connection");
            System.exit(0);
        } catch (IllegalArgumentException e) {
            System.out.print("API key rejected by FINNHUB.");
            System.exit(0);
        } catch (IOException e) {
            System.out.print("No connection.");
            System.exit(0);
        }
    }

    public static void setData(JsonArray arr) {
        for (JsonElement e : arr) {
            JsonObject obj = e.getAsJsonObject();

            String symbol = obj.get("symbol").getAsString().toLowerCase();
            if (symbol.isBlank() || symbol.isEmpty())
                continue;
            // String

            Data temp = new Data(symbol, 0, 0, 0, 0, 0, 0, 0, System.currentTimeMillis());
            data.put(symbol, temp);
        }
    }

    public static void authenticateKey() {

        long now = System.currentTimeMillis();
        if (now - lastCheck < maxPingTime)
            return;

        if (apiKey == null) {
            System.out.print("API key is null. Please enter a good api key next time. \n");
            System.exit(0);
        }

        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create("https://finnhub.io/api/v1/quote?symbol=AAPL"))
                    .header("X-Finnhub-Token", apiKey)
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            int status = client.send(req, HttpResponse.BodyHandlers.discarding()).statusCode();

            if (status == 200) {
                lastCheck = now;
                System.out.println("API key is good.");
            }

            if (status == 401) {
                System.out.println("API key rejected by Finnhub.");
                System.exit(0);
            }

            if (status == 429) {
                System.out.println("Finnhub rate limit reached. Slow down for a minute.");
                lastCheck = now;
            }

            if (status == 403) {
                System.out.println("Your Finnhub plan doesn't allow this request. Exiting.");
                System.exit(0);
            }

            if (status == 422) {
                System.out.println("Finnhub couldn't process the request (status 422). Exiting.");
                System.exit(0);
            }
            if (status >= 500) {
                System.out.println("Finnhub is having problems (status" + status + "). Exiting.");
                System.exit(0);
            }

        } catch (InterruptedException e) {
            System.out.println("No connection");
            System.exit(0);
        } catch (IllegalArgumentException e) {
            System.out.println("API key rejected by FINNHUB.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("No connection.");
            System.exit(0);
        }
    }

    public static void init(String key) {
        apiKey = key;
    }

    public Data getStock(String stockName) {
        if (data.containsKey(stockName)) {
            return data.get(stockName);
        }
        return null;
    }

    public boolean isStock(String stockName) {
        return data.containsKey(stockName);
    }

    public List<Map.Entry<String, Data>> getAllStock() {
        List<Map.Entry<String, Data>> all = new ArrayList<>(data.entrySet());

        return all;
    }
}