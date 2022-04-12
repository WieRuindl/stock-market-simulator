package pw.test.stockmarketsimulator.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import pw.test.stockmarketsimulator.utils.OrderIdGenerator;

@Data
@AllArgsConstructor
public class Order {
    private final int id = OrderIdGenerator.generateId();
    private final long timestamp = System.currentTimeMillis();

    private final Type type;
    private final Symbol symbol;
    private final int price;
    private int quantity;

    public enum Type {
        BUY, SELL
    }

    public void reduceQuantity(int quantityToReduce) {
        quantity -= quantityToReduce;
    }
}
