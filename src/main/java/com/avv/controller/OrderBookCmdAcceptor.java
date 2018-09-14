package com.avv.controller;

import com.avv.orderbook.CmdType;
import com.avv.orderbook.OrderBookCmd;
import com.avv.orderbook.PriceQtyPair;
import com.lmax.disruptor.RingBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Disruptor's Producer implementation - entry point to the ring
 *
 * There are many improvements possible, please consider this a working prototype.
 *
 * @author Alexander Voll
 */

@Component
public class OrderBookCmdAcceptor //Disruptor's Producer
{
    @Autowired
    @Qualifier("RingBuffer<OrderBookCmd>")
    private final RingBuffer<OrderBookCmd> ringBuffer;
    public OrderBookCmdAcceptor(RingBuffer<OrderBookCmd> ringBuffer)
    {
        this.ringBuffer = ringBuffer;
    }

    private static final RequestToCmdTranslator TRANSLATOR = new RequestToCmdTranslator();

    public void accept(CmdType cmdType, PriceQtyPair priceQty) {
        accept(cmdType, priceQty, null);
    }
    public void accept(CmdType cmdType, PriceQtyPair priceQty, DeferredResult<String> deferredResult) {
        ringBuffer.publishEvent(TRANSLATOR, cmdType, priceQty, deferredResult);
    }

}