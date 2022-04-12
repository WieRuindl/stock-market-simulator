package pw.test.stockmarketsimulator.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class TradeIdGenerator {

    private final static AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    public static int generateId() {
        return ID_GENERATOR.getAndIncrement();
    }

    // for tests
    public static void refresh() {
        ID_GENERATOR.set(0);
    }
}
