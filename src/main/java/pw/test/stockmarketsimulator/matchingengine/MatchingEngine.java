package pw.test.stockmarketsimulator.matchingengine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.test.stockmarketsimulator.book.Book;
import pw.test.stockmarketsimulator.order.Order;
import pw.test.stockmarketsimulator.order.Symbol;
import pw.test.stockmarketsimulator.trade.Trade;
import pw.test.stockmarketsimulator.trade.TradeLedger;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MatchingEngine {

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
    private final Map<Symbol, Book> books = Arrays.stream(Symbol.values())
            .map(Book::new)
            .collect(Collectors.toMap(Book::getSymbol, b -> b));
    private final TradeLedger tradeLedger;

    @Autowired
    public MatchingEngine(TradeLedger tradeLedger) {
        this.tradeLedger = tradeLedger;

        Runnable task = () -> {
            List<Trade> trades = books.values().stream()
                    .map(Book::matchOrders)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            this.tradeLedger.addTrades(trades);
        };
        EXECUTOR_SERVICE.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    public String addOrder(Order order) {
        Book book = books.get(order.getSymbol());
        book.addOrder(order);

        String message = String.format("Order %s is created", order);
        log.info(message);
        return message;
    }

    public String cancelOrder(int id) {
        String message = String.format("No existing order found for id=%s", id);
        for (Book book : books.values()) {
            Optional<Order> order = book.findAndRemoveOrder(id);
            if (order.isPresent()) {
                message = String.format("Order %s is deleted", order.get());
                break;
            }
        }

        log.info(message);
        return message;
    }

    public String getOrders(String symbol) {
        Optional<Book> book = books.values().stream()
                .filter(b -> b.getSymbol().toString().equals(symbol))
                .findFirst();

        String message = (book.isPresent() ? book.get().toString() : "No book found for symbol ") + symbol;
        log.info(message);
        return message;
    }

}
