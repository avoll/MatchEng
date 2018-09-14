package com.avv.orderbook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimi.dsi.fastutil.doubles.Double2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleComparators;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Order Book implementation that knows how to handle addition of buys and sells, and present itself in JSON
 *
 * There are many improvements possible, please consider this a working prototype.
 *
 * @author Alexander Voll
 */


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OrderBook {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OrderBook() {}

    @Autowired
    private ObjectMapper mapper;

    protected final Double2ObjectRBTreeMap<PriceQtyPair> buys = new Double2ObjectRBTreeMap<>(Comparator.reverseOrder());
    protected final Double2ObjectRBTreeMap<PriceQtyPair> sells = new Double2ObjectRBTreeMap<>(Comparator.naturalOrder());

    @JsonProperty("buys")
    protected ObjectCollection<PriceQtyPair> getBuys() {return buys.values();}
    @JsonProperty("sells")
    protected ObjectCollection<PriceQtyPair> getSells() {return sells.values();}

    /**
     * Adds a buy order, a bid, to the order book
     *
     * Delegates common functionality to {@link OrderBook#put(CmdType, Double2ObjectRBTreeMap, Double2ObjectRBTreeMap, DoubleComparator, PriceQtyPair)} method.
     *
     * @param priceQty a value object that contains a price and a quantity
     */
    public void buy(PriceQtyPair priceQty) { put(CmdType.BUY, buys, sells, DoubleComparators.NATURAL_COMPARATOR, priceQty); }

    /**
     * Adds a sell order, an ask, to the order book.
     *
     * Delegates common functionality to {@link OrderBook#put(CmdType, Double2ObjectRBTreeMap, Double2ObjectRBTreeMap, DoubleComparator, PriceQtyPair)} method.
     *
     * @param priceQty a value object that contains a price and a quantity
     */
    public void sell(PriceQtyPair priceQty) { put(CmdType.SELL, sells, buys, DoubleComparators.OPPOSITE_COMPARATOR, priceQty); }

    /**
     * Implements order book matching logic for buy and sell sides.
     *
     * The implementation is common, appropriate differences are achieved via parameters.
     * See {@link OrderBook#buy(PriceQtyPair)} and {@link OrderBook#sell(PriceQtyPair)} implementations for the specifics
     *
     * @param trxType
     * @param trxMapSrc
     * @param trxMapMatch
     * @param comp
     * @param priceQty
     */

    protected void put(CmdType trxType,
                     Double2ObjectRBTreeMap<PriceQtyPair> trxMapSrc,
                     Double2ObjectRBTreeMap<PriceQtyPair> trxMapMatch,
                     final DoubleComparator comp,
                     PriceQtyPair priceQty) {

        logger.info("OrderBook"+this.getClass().toString()+" "+trxType.toString()+": " + priceQty);

        double firstPrice;
        if (trxMapMatch.isEmpty()
                || (comp.compare(firstPrice = trxMapMatch.firstDoubleKey(), priceQty.price) > 0)) { // > for buys
            trxMapSrc.put(priceQty.price, priceQty);
        } else {
            while(comp.compare(firstPrice, priceQty.price) <= 0) { // <= for buys
                PriceQtyPair firstPriceQty = trxMapMatch.get(firstPrice);
                if (firstPriceQty.qty <= priceQty.qty) {
                    priceQty.qty -= firstPriceQty.qty;
                    trxMapMatch.remove(firstPrice);
                    if (!trxMapMatch.isEmpty()) {
                        firstPrice = trxMapMatch.firstDoubleKey();
                    } else {
                        trxMapSrc.put(priceQty.price, priceQty);
                        break;
                    }
                } else {
                    firstPriceQty.qty -= priceQty.qty;
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