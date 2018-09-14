package com.avv;

import com.avv.controller.OrderBookCmdHandler;
import com.avv.orderbook.OrderBookCmd;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;
import java.util.concurrent.ThreadFactory;

/**
 * Main Entry point to a basic order book implementation POC
 *
 * There are many improvements possible, please consider this a working prototype.
 *
 * I'm using Spring Boot, Spring, Jersey, Disruptor, and fastutil to put things together.
 *
 * @author Alexander Voll
 */

@SpringBootApplication
public class Application {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderBookCmdHandler orderBookCmdHandler;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean(name = "RingBuffer<OrderBookCmd>")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public RingBuffer<OrderBookCmd> setupDisruptor() {
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        int bufferSize = 1024;

        // Construct the Disruptor
        Disruptor<OrderBookCmd> disruptor = new Disruptor<>(OrderBookCmd::new, bufferSize, threadFactory);

        // Connect the handler
        disruptor.handleEventsWith(orderBookCmdHandler);

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<OrderBookCmd> ringBuffer = disruptor.getRingBuffer();

        Thread disruptorShutDown = new Thread() {
            public void run() {
                logger.info("Shutting down Disruptor and its Thread Pool...");
                disruptor.shutdown();
                logger.info("Disruptor has shut down.");
            }
        };
        Runtime.getRuntime().addShutdownHook(disruptorShutDown);

        return ringBuffer;
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            logger.info("Inspecting the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                logger.info(beanName);
            }

        };
    }
}
