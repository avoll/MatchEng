package com.avv.orderbook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimi.dsi.fastutil.doubles.Double2IntRBTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * This class is deprecated, but is kept temporarily to show the origins of common logic
 * for buy and sell side sorted maps of order book
 *
 * There are many improvements possible, please consider this a working prototype.
 *
 * @author Alexander Voll
 */


@Deprecated
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OrderBookPrimitive {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OrderBookPrimitive() {}

    @Autowired
    private ObjectMapper mapper;

    @JsonProperty("buys")
    protected final Double2IntRBTreeMap buys = new Double2IntRBTreeMap(Comparator.reverseOrder());
    @JsonProperty("sells")
    protected final Double2IntRBTreeMap sells = new Double2IntRBTreeMap(Comparator.naturalOrder());

    public void buy(int qty, double price) {
        logger.info("OrderBook"+this.getClass().toString()+" BUY: " + qty + "@"+ price);
        double smallestPrice;
        if (sells.isEmpty() || ((smallestPrice = sells.firstDoubleKey())> price)) {
            buys.put(price, qty);
        } else {
            while(smallestPrice <= price) {
                int smallestSellQty = sells.get(smallestPrice);
                if (smallestSellQty <= qty) {
                    qty -= smallestSellQty;
                    sells.remove(smallestPrice);
                    if (!sells.isEmpty()) {
                        smallestPrice = sells.firstDoubleKey();
                    } else {
                        buys.put(price, qty);
                        break;
                    }
                } else {
                    sells.addTo(smallestPrice, -qty);
                    break;
                }
            }
        }
    }

    public void sell(int qty, double price) {
        logger.info("OrderBook"+this.getClass().toString()+" SELL: " + qty + "@"+ price);
        double highestPrice;
        if (buys.isEmpty() || ((highestPrice = buys.firstDoubleKey()) < price)) {
            sells.put(price, qty);
        } else {
            while(highestPrice >= price) {
                int highestBuyQty = buys.get(highestPrice);
                if (highestBuyQty <= qty) {
                    qty -= highestBuyQty;
                    buys.remove(highestPrice);
                    if (!buys.isEmpty()) {
                        highestPrice = buys.firstDoubleKey();
                    } else {
                        sells.put(price, qty);
                        break;
                    }
                } else {
                    buys.addTo(highestPrice, -qty);
                    break;
                }
            }
        }
    }

    public String toJson() throws JsonProcessingException {
        String buysJson = mapper.writeValueAsString(buys.values());
        String sellsJson = mapper.writeValueAsString(sells.values());
        String json = "{\n   \"buys\": " + buysJson + ",\n  \"sells\": " + sellsJson + "\n}";
        //json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        return json;
    }
}