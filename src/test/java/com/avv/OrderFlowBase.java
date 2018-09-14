package com.avv;

import com.avv.orderbook.CmdType;
import com.avv.orderbook.OrderBookCmd;
import com.avv.utils.DoublesWithPrecisionJSONComparator;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * Match Engine Order Book test data and order flow
 *
 * @author Alexander Voll
 */


public class OrderFlowBase {
    public static final OrderBookCmd[] orders = new OrderBookCmd[] {
            new OrderBookCmd(CmdType.SELL, 15, 10),
            new OrderBookCmd(CmdType.SELL, 13, 10),
            new OrderBookCmd(CmdType.BUY, 7, 10),
            new OrderBookCmd(CmdType.BUY, 9.5, 10),

            new OrderBookCmd(CmdType.SELL, 9.5, 5),

            new OrderBookCmd(CmdType.BUY, 13, 6),

            new OrderBookCmd(CmdType.SELL, 7, 7),

            new OrderBookCmd(CmdType.SELL, 6, 12),
    };

    public static final String finalBookJson = "{\n" +
            "    \"buys\": [ ],\n" +
            "    \"sells\":[ { \"qty\":4, \"prc\":6.0 }, { \"qty\":4, \"prc\":13.0 }, { \"qty\":10, \"prc\":15.0 } ] \n" +
            "  }";

    public static final DoublesWithPrecisionJSONComparator dblJsonComp =
            new DoublesWithPrecisionJSONComparator(JSONCompareMode.LENIENT, 0.0000001);

    public OrderFlowBase () {}
}
