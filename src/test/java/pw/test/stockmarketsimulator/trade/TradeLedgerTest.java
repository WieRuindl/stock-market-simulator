package pw.test.stockmarketsimulator.trade;

import org.junit.Before;
import org.junit.Test;
import pw.test.stockmarketsimulator.clientsnotifier.ClientsNotifier;
import pw.test.stockmarketsimulator.order.Symbol;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TradeLedgerTest {

    private ClientsNotifier clientsNotifierMock;
    private TradeLedger tradeLedger;

    @Before
    public void setUp() {
        clientsNotifierMock = mock(ClientsNotifier.class);
        tradeLedger = new TradeLedger(clientsNotifierMock);
    }

    @Test
    public void addEmptyListOfTradesTest() {
        doNothing().when(clientsNotifierMock).notifyClients(anyString());

        tradeLedger.addTrades(Collections.emptyList());

        verify(clientsNotifierMock, times(0)).notifyClients(anyString());
        assertEquals(0, tradeLedger.getTrades().size());
    }

    @Test
    public void addTradesTest() {
        doNothing().when(clientsNotifierMock).notifyClients(anyString());
        Trade trade = new Trade(Symbol.AAA, 10, 100, 0, 1);

        tradeLedger.addTrades(List.of(trade));

        verify(clientsNotifierMock, times(1)).notifyClients(anyString());
        verify(clientsNotifierMock, times(1)).notifyClients(trade.toString());
        assertEquals(1, tradeLedger.getTrades().size());
        assertEquals(trade, tradeLedger.getTrades().get(0));
    }
}