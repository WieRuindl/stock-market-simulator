package pw.test.stockmarketsimulator.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.test.stockmarketsimulator.clientsnotifier.ClientsNotifier;

import java.util.LinkedList;
import java.util.List;

@Component
@AllArgsConstructor
public class TradeLedger {

    @Autowired
    private final ClientsNotifier clientsNotifier;

    @Getter
    private final List<Trade> trades = new LinkedList<>();

    public void addTrades(List<Trade> trades) {
        this.trades.addAll(trades);
        trades.forEach(t -> clientsNotifier.notifyClients(t.toString()));
    }
}
