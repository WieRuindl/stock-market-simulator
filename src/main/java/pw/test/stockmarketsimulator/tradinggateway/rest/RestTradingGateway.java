package pw.test.stockmarketsimulator.tradinggateway.rest;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pw.test.stockmarketsimulator.matchingengine.MatchingEngine;
import pw.test.stockmarketsimulator.order.Order;
import pw.test.stockmarketsimulator.tradinggateway.TradingGateway;

@RestController
@AllArgsConstructor
public class RestTradingGateway implements TradingGateway {

    @Autowired
    private final MatchingEngine matchingEngine;

    @RequestMapping(value = "/create-order", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String createOrder(@RequestBody Order order) {
        return matchingEngine.addOrder(order);
    }

    @RequestMapping(value = "/cancel-order", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String cancelOrder(@RequestBody int id) {
        return matchingEngine.cancelOrder(id);
    }

    @RequestMapping(value = "/get-orders", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String getOrders(@RequestBody String symbol) {
        return matchingEngine.getOrders(symbol);
    }

}
