package stockmarket;

import java.time.LocalDateTime;

public class Statement {
    public int quantity;
    public double transaction;
    public String stockName;
    public StatementType type;
    public double pricePerShare;
    public LocalDateTime time;

    Statement(int quantity, double transaction, String stockName, StatementType type, double pricePerShare) {
        this.quantity = quantity;
        this.transaction = transaction;
        this.stockName = stockName;
        this.type = type;
        this.pricePerShare = pricePerShare;
        this.time = LocalDateTime.now();
    }

    Statement(double transaction, StatementType type) {
        this.transaction = transaction;
        this.type = type;
        this.time = LocalDateTime.now();
    }
}