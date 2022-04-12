package pw.test.stockmarketsimulator.book;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pw.test.stockmarketsimulator.order.Order;
import pw.test.stockmarketsimulator.order.Order.Type;
import pw.test.stockmarketsimulator.order.Symbol;
import pw.test.stockmarketsimulator.trade.Trade;
import pw.test.stockmarketsimulator.utils.OrderIdGenerator;
import pw.test.stockmarketsimulator.utils.TradeIdGenerator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookTest {

    private Book book;

    @Before
    public void setUp() {
        book = new Book(Symbol.AAA);
    }

    @After
    public void afterTest() {
        OrderIdGenerator.refresh();
        TradeIdGenerator.refresh();
    }

    @Test
    public void test_removeNotExistingOrder() {
        Optional<Order> andRemoveOrder = book.findAndRemoveOrder(0);
        assertTrue(andRemoveOrder.isEmpty());
    }

    @Test
    public void test_addBuyOrdersAndCheck() throws InterruptedException {
        Order order0 = new Order(Type.BUY, Symbol.AAA, 100, 10);
        Thread.sleep(10);
        Order order1 = new Order(Type.BUY, Symbol.AAA, 100, 10);
        Thread.sleep(10);
        Order order2 = new Order(Type.BUY, Symbol.AAA, 99, 10);
        Thread.sleep(10);
        Order order3 = new Order(Type.BUY, Symbol.AAA, 101, 10);
        book.addOrder(order0);
        book.addOrder(order1);
        book.addOrder(order2);
        book.addOrder(order3);

        Map<Type, List<Order>> orders = book.getOrders();

        assertTrue(orders.get(Type.SELL).isEmpty());
        assertEquals(4, orders.get(Type.BUY).size());
        assertEquals(order3, orders.get(Type.BUY).get(0));
        assertEquals(order0, orders.get(Type.BUY).get(1));
        assertEquals(order1, orders.get(Type.BUY).get(2));
        assertEquals(order2, orders.get(Type.BUY).get(3));
    }

    @Test
    public void test_addSellOrdersAndCheck() throws InterruptedException {
        Order order0 = new Order(Type.SELL, Symbol.AAA, 100, 10);
        Thread.sleep(10);
        Order order1 = new Order(Type.SELL, Symbol.AAA, 100, 10);
        Thread.sleep(10);
        Order order2 = new Order(Type.SELL, Symbol.AAA, 99, 10);
        Thread.sleep(10);
        Order order3 = new Order(Type.SELL, Symbol.AAA, 101, 10);
        book.addOrder(order0);
        book.addOrder(order1);
        book.addOrder(order2);
        book.addOrder(order3);

        Map<Type, List<Order>> orders = book.getOrders();

        assertTrue(orders.get(Type.BUY).isEmpty());
        assertEquals(4, orders.get(Type.SELL).size());
        assertEquals(order2, orders.get(Type.SELL).get(0));
        assertEquals(order0, orders.get(Type.SELL).get(1));
        assertEquals(order1, orders.get(Type.SELL).get(2));
        assertEquals(order3, orders.get(Type.SELL).get(3));
    }

    @Test
    public void test_NoMatchingBecauseOfThePrice() throws InterruptedException {
        Order buyOrder0 = new Order(Type.BUY, Symbol.AAA, 99, 10);
        Thread.sleep(10);
        Order sellOrder1 = new Order(Type.SELL, Symbol.AAA, 100, 10);

        book.addOrder(buyOrder0);
        book.addOrder(sellOrder1);
        List<Trade> trades = book.matchOrders();

        assertTrue(trades.isEmpty());
    }

    @Test
    public void test_match1() throws InterruptedException {
        Order buyOrder0 = new Order(Type.BUY, Symbol.AAA, 99, 10);
        Thread.sleep(10);
        Order buyOrder1 = new Order(Type.BUY, Symbol.AAA, 100, 10);
        Thread.sleep(10);
        Order sellOrder2 = new Order(Type.SELL, Symbol.AAA, 100, 10);

        book.addOrder(buyOrder0);
        book.addOrder(buyOrder1);
        book.addOrder(sellOrder2);
        List<Trade> trades = book.matchOrders();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Symbol.AAA, trade.getSymbol());
        assertEquals(0, trade.getId());
        assertEquals(1, trade.getBuyOrderId());
        assertEquals(2, trade.getSellOrderId());
        assertEquals(100, trade.getPrice());
        assertEquals(10, trade.getQuantity());

        assertTrue(book.getOrders().get(Type.SELL).isEmpty());
        assertEquals(1, book.getOrders().get(Type.BUY).size());
        assertEquals(buyOrder0, book.getOrders().get(Type.BUY).get(0));
    }

    @Test
    public void test_match2() throws InterruptedException {
        Order buyOrder0 = new Order(Type.BUY, Symbol.AAA, 99, 10);
        Thread.sleep(10);
        Order buyOrder1 = new Order(Type.BUY, Symbol.AAA, 100, 10);
        Thread.sleep(10);
        Order sellOrder2 = new Order(Type.SELL, Symbol.AAA, 90, 10);

        book.addOrder(buyOrder0);
        book.addOrder(buyOrder1);
        book.addOrder(sellOrder2);
        List<Trade> trades = book.matchOrders();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Symbol.AAA, trade.getSymbol());
        assertEquals(0, trade.getId());
        assertEquals(1, trade.getBuyOrderId());
        assertEquals(2, trade.getSellOrderId());
        assertEquals(90, trade.getPrice());
        assertEquals(10, trade.getQuantity());

        assertTrue(book.getOrders().get(Type.SELL).isEmpty());
        assertEquals(1, book.getOrders().get(Type.BUY).size());
        assertEquals(buyOrder0, book.getOrders().get(Type.BUY).get(0));
    }

    @Test
    public void test_match3() throws InterruptedException {
        Order buyOrder0 = new Order(Type.BUY, Symbol.AAA, 100, 10);
        Thread.sleep(10);
        Order sellOrder1 = new Order(Type.SELL, Symbol.AAA, 100, 10);
        Thread.sleep(10);
        Order sellOrder2 = new Order(Type.SELL, Symbol.AAA, 99, 10);

        book.addOrder(buyOrder0);
        book.addOrder(sellOrder1);
        book.addOrder(sellOrder2);
        List<Trade> trades = book.matchOrders();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Symbol.AAA, trade.getSymbol());
        assertEquals(0, trade.getId());
        assertEquals(0, trade.getBuyOrderId());
        assertEquals(2, trade.getSellOrderId());
        assertEquals(99, trade.getPrice());
        assertEquals(10, trade.getQuantity());

        assertTrue(book.getOrders().get(Type.BUY).isEmpty());
        assertEquals(1, book.getOrders().get(Type.SELL).size());
        assertEquals(sellOrder1, book.getOrders().get(Type.SELL).get(0));
    }

    @Test
    public void test_crossMatching() throws InterruptedException {
        Order buyOrder0 = new Order(Type.BUY, Symbol.AAA, 99, 8);
        Thread.sleep(10);
        Order buyOrder1 = new Order(Type.BUY, Symbol.AAA, 100, 7);
        Thread.sleep(10);
        Order sellOrder2 = new Order(Type.SELL, Symbol.AAA, 95, 9);
        Thread.sleep(10);
        Order sellOrder3 = new Order(Type.SELL, Symbol.AAA, 90, 5);

        book.addOrder(buyOrder0);
        book.addOrder(buyOrder1);
        book.addOrder(sellOrder2);
        book.addOrder(sellOrder3);
        List<Trade> trades = book.matchOrders();

        assertEquals(3, trades.size());
        Trade trade0 = trades.get(0);
        assertEquals(Symbol.AAA, trade0.getSymbol());
        assertEquals(0, trade0.getId());
        assertEquals(1, trade0.getBuyOrderId());
        assertEquals(3, trade0.getSellOrderId());
        assertEquals(90, trade0.getPrice());
        assertEquals(5, trade0.getQuantity());

        Trade trade1 = trades.get(1);
        assertEquals(Symbol.AAA, trade1.getSymbol());
        assertEquals(1, trade1.getId());
        assertEquals(1, trade1.getBuyOrderId());
        assertEquals(2, trade1.getSellOrderId());
        assertEquals(95, trade1.getPrice());
        assertEquals(2, trade1.getQuantity());

        Trade trade2 = trades.get(2);
        assertEquals(Symbol.AAA, trade2.getSymbol());
        assertEquals(2, trade2.getId());
        assertEquals(0, trade2.getBuyOrderId());
        assertEquals(2, trade2.getSellOrderId());
        assertEquals(95, trade2.getPrice());
        assertEquals(7, trade2.getQuantity());

        assertTrue(book.getOrders().get(Type.SELL).isEmpty());
        assertEquals(1, book.getOrders().get(Type.BUY).size());
        assertEquals(buyOrder0.getId(), book.getOrders().get(Type.BUY).get(0).getId());
        assertEquals(1, book.getOrders().get(Type.BUY).get(0).getQuantity());
    }

    @Test
    public void test_matchThenMatchAgain() throws InterruptedException {
        Order buyOrder0 = new Order(Type.BUY, Symbol.AAA, 120, 10);
        Thread.sleep(10);
        Order sellOrder1 = new Order(Type.SELL, Symbol.AAA, 110, 5);
        Thread.sleep(10);
        Order sellOrder2 = new Order(Type.SELL, Symbol.AAA, 100, 5);

        book.addOrder(buyOrder0);
        book.addOrder(sellOrder1);
        List<Trade> trades0 = book.matchOrders();

        assertEquals(1, trades0.size());
        Trade trade0 = trades0.get(0);
        assertEquals(Symbol.AAA, trade0.getSymbol());
        assertEquals(0, trade0.getId());
        assertEquals(0, trade0.getBuyOrderId());
        assertEquals(1, trade0.getSellOrderId());
        assertEquals(110, trade0.getPrice());
        assertEquals(5, trade0.getQuantity());
        Map<Type, List<Order>> orders = book.getOrders();
        assertTrue(orders.get(Type.SELL).isEmpty());
        assertEquals(1, orders.get(Type.BUY).size());
        assertEquals(buyOrder0.getId(), orders.get(Type.BUY).get(0).getId());
        assertEquals(5, orders.get(Type.BUY).get(0).getQuantity());

        book.addOrder(sellOrder2);
        List<Trade> trades1 = book.matchOrders();

        Trade trade1 = trades1.get(0);
        assertEquals(Symbol.AAA, trade1.getSymbol());
        assertEquals(1, trade1.getId());
        assertEquals(0, trade1.getBuyOrderId());
        assertEquals(2, trade1.getSellOrderId());
        assertEquals(100, trade1.getPrice());
        assertEquals(5, trade1.getQuantity());
        assertTrue(book.getOrders().get(Type.SELL).isEmpty());
        assertTrue(book.getOrders().get(Type.BUY).isEmpty());
    }

    @Test
    public void test_matchThenCancelTrade() throws InterruptedException {
        Order buyOrder0 = new Order(Type.BUY, Symbol.AAA, 120, 10);
        Thread.sleep(10);
        Order sellOrder1 = new Order(Type.SELL, Symbol.AAA, 110, 5);
        Thread.sleep(10);
        Order sellOrder2 = new Order(Type.SELL, Symbol.AAA, 100, 5);

        book.addOrder(buyOrder0);
        book.addOrder(sellOrder1);
        List<Trade> trades0 = book.matchOrders();

        assertEquals(1, trades0.size());
        Trade trade0 = trades0.get(0);
        assertEquals(Symbol.AAA, trade0.getSymbol());
        assertEquals(0, trade0.getId());
        assertEquals(0, trade0.getBuyOrderId());
        assertEquals(1, trade0.getSellOrderId());
        assertEquals(110, trade0.getPrice());
        assertEquals(5, trade0.getQuantity());
        Map<Type, List<Order>> orders = book.getOrders();
        assertTrue(orders.get(Type.SELL).isEmpty());
        assertEquals(1, orders.get(Type.BUY).size());
        assertEquals(buyOrder0.getId(), orders.get(Type.BUY).get(0).getId());
        assertEquals(5, orders.get(Type.BUY).get(0).getQuantity());

        book.addOrder(sellOrder2);
        book.findAndRemoveOrder(0);
        List<Trade> trades1 = book.matchOrders();

        assertTrue(trades1.isEmpty());
        assertEquals(1, book.getOrders().get(Type.SELL).size());
        assertEquals(sellOrder2, book.getOrders().get(Type.SELL).get(0));
        assertTrue(book.getOrders().get(Type.BUY).isEmpty());
    }
}