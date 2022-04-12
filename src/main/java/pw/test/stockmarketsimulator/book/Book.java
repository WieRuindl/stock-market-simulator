package pw.test.stockmarketsimulator.book;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pw.test.stockmarketsimulator.order.Order;
import pw.test.stockmarketsimulator.order.Order.Type;
import pw.test.stockmarketsimulator.order.Symbol;
import pw.test.stockmarketsimulator.trade.Trade;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Data
public class Book {

    private static final Comparator<Order> ORDER_COMPARATOR = (o1, o2) -> {
        int compare = Integer.compare(o1.getPrice(), o2.getPrice()) * (o1.getType() == Type.SELL ? 1 : -1);
        return compare != 0 ? compare : Long.compare(o1.getTimestamp(), o2.getTimestamp());
    };
    final Map<Type, List<Order>> orders = new HashMap<>() {{
        put(Type.BUY, new LinkedList<>());
        put(Type.SELL, new LinkedList<>());
    }};
    private final Symbol symbol;

    public void addOrder(Order order) {
        List<Order> orders = this.orders.get(order.getType());
        orders.add(order);
        orders.sort(ORDER_COMPARATOR);
    }

    public Optional<Order> findAndRemoveOrder(int id) {
        Optional<Order> buyOrder = findAndRemoveOrder(id, Type.BUY);
        if (buyOrder.isPresent()) return buyOrder;
        return findAndRemoveOrder(id, Type.SELL);
    }

    private Optional<Order> findAndRemoveOrder(int id, Type type) {
        Optional<Order> orderToRemove = orders.get(type).stream()
                .filter(o -> o.getId() == id)
                .findAny();
        if (orderToRemove.isEmpty()) {
            return Optional.empty();
        }
        Order order = orderToRemove.get();
        orders.get(type).remove(order);
        return Optional.of(order);
    }

    public List<Trade> matchOrders() {
        List<Trade> trades = new LinkedList<>();

        boolean matchingHappened;

        do {
            matchingHappened = false;
            for (Order buyOrder : orders.get(Type.BUY)) {
                for (Order sellOrder : orders.get(Type.SELL)) {
                    if (buyOrder.getPrice() >= sellOrder.getPrice()
                            && buyOrder.getQuantity() > 0
                            && sellOrder.getQuantity() > 0
                    ) {
                        matchingHappened = true;
                        int quantityToTrade = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());

                        String message = String.format("BUY order %s is matched to SELL order %s for quantity %s and price %s",
                                buyOrder, sellOrder, quantityToTrade, sellOrder.getPrice());
                        log.info(message);

                        buyOrder.reduceQuantity(quantityToTrade);
                        sellOrder.reduceQuantity(quantityToTrade);
                        Trade trade = new Trade(symbol, quantityToTrade, sellOrder.getPrice(), buyOrder.getId(), sellOrder.getId());
                        trades.add(trade);
                    }
                }
            }

            removeFilledOrders(Type.BUY);
            removeFilledOrders(Type.SELL);
        } while (matchingHappened);

        return trades;
    }

    private void removeFilledOrders(Type type) {
        List<Order> filledOrders = orders.get(type).stream()
                .filter(Order::isFilled)
                .collect(Collectors.toList());
        orders.get(type).removeAll(filledOrders);
    }

}
