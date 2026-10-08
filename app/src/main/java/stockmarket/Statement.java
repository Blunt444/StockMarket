package stockmarket;

public class Statement {
    public int quantity;
    public double transaction;
    public String stockName;

    Statement(int quantity, double transaction, String stockName) {
        this.quantity = quantity;
        this.transaction = transaction;
        this.stockName = stockName;
    }
}
