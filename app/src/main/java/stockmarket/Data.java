package stockmarket;

public class Data {
    public String name;
    public double price;
    public long lastChecked;

    Data(String name, double price, long lastChecked) {
        this.name = name;
        this.price = price;
        this.lastChecked = lastChecked;
    }
}