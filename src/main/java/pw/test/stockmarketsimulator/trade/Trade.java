package pw.test.stockmarketsimulator.trade;

import lombok.Data;
import pw.test.stockmarketsimulator.order.Symbol;
import pw.test.stockmarketsimulator.utils.TradeIdGenerator;

@Data
public class Trade {

    private final int id = TradeIdGenerator.generateId();

    private final Symbol symbol;
    private final int quantity;
    private final int price;
    private final int buyOrderId;
    private final int sellOrderId;
}
