package com.avv.controller;

import com.avv.orderbook.CmdType;
import com.avv.orderbook.OrderBookCmd;
import com.avv.orderbook.PriceQtyPair;
import com.lmax.disruptor.EventTranslatorThreeArg;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Primitive Disruptor's translator implementation
 *
 * There are many improvements possible, please consider this a working prototype.
 *
 * @author Alexander Voll
 */

public class RequestToCmdTranslator
        implements EventTranslatorThreeArg<OrderBookCmd, CmdType, PriceQtyPair, DeferredResult<String>> {
    @Override
    public void translateTo(OrderBookCmd cmd, long sequence, CmdType cmdType, PriceQtyPair priceQty,
                            DeferredResult<String> deferredResult) {
        cmd.set(cmdType, priceQty, deferredResult);
    }
};