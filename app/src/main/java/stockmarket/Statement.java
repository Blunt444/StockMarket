package stockmarket;

public class Statement {
    public int quantity;
    public double transaction;
    public String stockName;
    public StatementType type;

    Statement(int quantity, double transaction, String stockName, StatementType type) {
        this.quantity = quantity;
        this.transaction = transaction;
        this.stockName = stockName;
        this.type = type;
    }

    Statement(double transaction, StatementType type) {
        this.transaction = transaction;
        this.type = type;
    }
}