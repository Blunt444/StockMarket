package stockmarket;

public class Holding {
    public int quantity;
    public double amount;
    public double pricePerShare;

    Holding(int quantity, double amount, double pricePerShare) {
        this.quantity = quantity;
        this.amount = amount;
        this.pricePerShare = pricePerShare;
    }

    Holding() {

    }
}
