package stockmarket;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
// import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

public class DataService {

    // private static final Path envPath = Path.of(System.getProperty("user.home"),
    // ".stockMarket");
    private static final String keyName = "FINNHUB_KEY";
    private static String apiKey;
    private static long lastCheck = 0;
    private static final long maxPingTime = 5 * 60 * 1000;
    private static long lastCached = 0;
    private static long maxTime = 2 * 60 * 1000;

    private static final Map<String, Data> data = new TreeMap<>();

    public Double getPrice(String name) {
        Data val = data.get(name.toLowerCase());

        if (val == null)
            return null;

        return val.price;
    }

    public static void callApiKey() {
        long now = System.currentTimeMillis();
        if (now - lastCached < maxTime)
            return;

        try {

            System.out.println("Caching data... Please wait few minutes");

            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS)
                    .connectTimeout(Duration.ofSeconds(5)).build();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://finnhub.io/api/v1/stock/symbol?exchange=US"))
                    .header("X-Finnhub-Token", apiKey)
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            int status = res.statusCode();

            if (status == 200) {
                lastCached = now;
                System.out.println(res.body());
                setData();
            }

            if (status == 401) {
                System.out.println("API key rejected by Finnhub.");
                System.exit(1);
            }

            if (status == 429) {
                System.out.println("Finnhub rate limit reached. Slow down for a minute.");
                lastCached = now;
            }

            if (status == 403) {
                System.out.println("Your Finnhub plan doesn't allow this request. Exiting.");
                System.exit(1);
            }

            if (status == 422) {
                System.out.println("Finnhub couldn't process the request (status 422). Exiting.");
                System.exit(1);
            }
            if (status >= 500) {
                System.out.println("Finnhub is having problems (status" + status + "). Exiting.");
                System.exit(1);
            }

        } catch (InterruptedException e) {
            System.out.print("No connection");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.print("API key rejected by FINNHUB.");
            System.exit(1);
        } catch (IOException e) {
            System.out.print("No connection.");
            System.exit(1);
        }
    }

    public static void setData() {

    }

    public static void authenticateKey() {

        long now = System.currentTimeMillis();
        if (now - lastCheck < maxPingTime)
            return;

        if (apiKey == null) {
            System.out.print("API key is null. Please enter a good api key next time. \n");
            System.exit(1);
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
                System.exit(1);
            }

            if (status == 429) {
                System.out.println("Finnhub rate limit reached. Slow down for a minute.");
                lastCheck = now;
            }

            if (status == 403) {
                System.out.println("Your Finnhub plan doesn't allow this request. Exiting.");
                System.exit(1);
            }

            if (status == 422) {
                System.out.println("Finnhub couldn't process the request (status 422). Exiting.");
                System.exit(1);
            }
            if (status >= 500) {
                System.out.println("Finnhub is having problems (status" + status + "). Exiting.");
                System.exit(1);
            }

        } catch (InterruptedException e) {
            System.out.println("No connection");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println("API key rejected by FINNHUB.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("No connection.");
            System.exit(1);
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