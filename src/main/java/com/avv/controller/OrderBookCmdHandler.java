package com.avv.controller;

import com.avv.orderbook.OrderBook;
import com.avv.orderbook.OrderBookCmd;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Disruptor's main processing Event Handler
 *
 * There are many improvements possible, please consider this a working prototype.
 *
 * @author Alexander Voll
 */

@Service
public class OrderBookCmdHandler implements EventHandler<OrderBookCmd>
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OrderBook orderBook;
    private final ObjectMapper mapper;
    @Autowired
    public OrderBookCmdHandler(OrderBook orderBook, ObjectMapper mapper) {
        this.orderBook = orderBook;
        this.mapper = mapper;
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void onEvent(OrderBookCmd cmd, long sequence, boolean endOfBatch)
    {
        logger.info("Handling Event: " + cmd);
        switch (cmd.getCmdType()) {
            case BUY: orderBook.buy(cmd); break;
            case SELL: orderBook.sell(cmd); break;
            case BOOK:
                String json = null;
                try {
                    //json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderBook);
                    json = orderBook.toJson();
                    cmd.getDeferredResult().setResult(json);
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    logger.error("Error handling cmd", e);
                    cmd.getDeferredResult().setErrorResult("Error handling cmd" + e.toString());
                }
                break;
        }
        
    }
}