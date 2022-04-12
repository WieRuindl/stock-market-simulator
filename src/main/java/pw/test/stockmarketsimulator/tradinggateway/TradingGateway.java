package pw.test.stockmarketsimulator.tradinggateway;

import pw.test.stockmarketsimulator.order.Order;

public interface TradingGateway {
    String createOrder(Order order);

    String cancelOrder(int id);

    String getOrders(String symbol);
}
