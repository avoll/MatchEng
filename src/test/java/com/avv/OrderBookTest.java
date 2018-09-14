package com.avv;

import com.avv.orderbook.OrderBook;
import com.avv.orderbook.OrderBookCmd;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Pure Match Engine Order Book test
 *
 * @author Alexander Voll
 */


@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderBookTest extends OrderFlowBase  {
    @Autowired
    private OrderBook orderBook;

    /**
     *  Executes a complete sample session and confirms final result
     */
    @Test
    public void sampleSession() throws Exception {

        for (OrderBookCmd cmd : orders) {
            switch(cmd.getCmdType()) {
                case BUY:
                    orderBook.buy(cmd); break;
                case SELL:
                    orderBook.sell(cmd); break;
                default:
                    throw new Exception("Unknown Command Type: " + cmd.getCmdType().toString());
            }
        }

        JSONAssert.assertEquals("Class: Sample Session Order Book matched",
                finalBookJson, orderBook.toJson(), dblJsonComp);
    }
}
