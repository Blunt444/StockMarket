package stockmarket;

public class Data {
    public String name;
    public double price;
    public long lastChecked;
    public double high;
    public double low;
    public double change;
    public double dp;
    public double open;
    public double pc;

    Data(String name, double price, double high, double low, double change, double dp, double open, double pc,
            long lastChecked) {
        this.name = name;
        this.price = price;
        this.lastChecked = lastChecked;
        this.high = high;
        this.low = low;
        this.change = change;
        this.dp = dp;
        this.open = open;
        this.pc = pc;

    }
}