package com.avv.controller;

import com.avv.orderbook.CmdType;
import com.avv.orderbook.PriceQtyPair;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Rest Controller 
 * 
 * This is where endpoints are defined, and where requests are fed into Disruptor Ring
 *
 * There are many improvements possible, please consider this a working prototype.
 *
 * @author Alexander Voll
 */

@RestController
public class MatchEngineController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final OrderBookCmdAcceptor acceptor;
    public MatchEngineController(OrderBookCmdAcceptor acceptor){this.acceptor = acceptor;}

    private static final PriceQtyPair EMPTY_PRICE_QTY = new PriceQtyPair();

    @RequestMapping("/")
    public String index() {
        return "Matching Engine is up.";
    }

    @PostMapping("/buy")
    public void buy(@RequestBody PriceQtyPair priceQtyPair) {
        acceptor.accept(CmdType.BUY, priceQtyPair);
    }

    @PostMapping("/sell")
    public void sell(@RequestBody PriceQtyPair priceQtyPair) {
        acceptor.accept(CmdType.SELL, priceQtyPair);
    }

    /**
     *
     * Using DeferedResult in order to allow Disruptor ring to process the request
     *
     **/	
    @GetMapping("/book")
    public DeferredResult<String> book() {
        DeferredResult<String> deferredResult = new DeferredResult<>(50000l);
        deferredResult.onTimeout(() ->
                deferredResult.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body("Request timeout occurred.")));

        deferredResult.onTimeout(() ->
                deferredResult.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body("Request timeout occurred.")));

        deferredResult.onCompletion(() -> logger.info("Book Request Processing completed."));

        acceptor.accept(CmdType.BOOK, EMPTY_PRICE_QTY, deferredResult);
        return deferredResult;
    }


    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }
}
