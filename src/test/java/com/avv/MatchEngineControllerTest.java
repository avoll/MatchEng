package com.avv;

import com.avv.orderbook.OrderBookCmd;
import com.avv.orderbook.PriceQtyPair;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.anything;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MVC Match Engine Order Book test
 *
 * @author Alexander Voll
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MatchEngineControllerTest extends OrderFlowBase  {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
    }

    /**
     * Helpful function to deal with results and async results uniformly
     *
     * Originally from https://stackoverflow.com/questions/17711615/testing-spring-asyncresult-and-jsonpath-together/17915913
     *
     * @param mockMvc
     * @param builder
     * @return
     * @throws Exception
     */

    private static ResultActions perform(final MockMvc mockMvc, MockHttpServletRequestBuilder builder) throws Exception {
        ResultActions resultActions = mockMvc.perform(builder);
        if (resultActions.andReturn().getRequest().isAsyncStarted()) {
            return mockMvc.perform(asyncDispatch(resultActions
                    .andExpect(request().asyncResult(anything()))
                    .andReturn()));
        } else {
            return resultActions;
        }
    }

    @Test
    public void index() throws Exception {
        perform(mvc, MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Matching Engine is up.")));
    }

    /**
     *  Executes a complete sample session and confirms final result
     */
    @Test
    public void sampleSession() throws Exception {

        for (OrderBookCmd cmd : orders) {
            perform(mvc,
                    MockMvcRequestBuilders.post("/" + cmd.getCmdType().toString().toLowerCase())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString((PriceQtyPair) cmd))
            );
        }

        MockHttpServletResponse response = perform(mvc,
                MockMvcRequestBuilders.get("/book").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        String bookJson = response.getContentAsString();

        JSONAssert.assertEquals("MVCTest: Sample Session Order Book matched",
                                finalBookJson, bookJson, dblJsonComp);
    }
}
