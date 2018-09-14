package com.avv;

import com.avv.orderbook.OrderBookCmd;
import com.avv.orderbook.PriceQtyPair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Integration Match Engine Order Book test
 *
 * @author Alexander Voll
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // classes=com.avv.Application.class)
public class MatchEngineControllerIT extends OrderFlowBase {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/");
    }

    @Test
    public void index() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString(),
                String.class);
        assertThat(response.getBody(), equalTo("Matching Engine is up."));
    }

    /**
     *  Executes a complete sample session and confirms final result
     */
    @Test
    public void sampleSession() throws JsonProcessingException, JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for(OrderBookCmd cmd : orders) {
                String requestJson = mapper.writeValueAsString((PriceQtyPair) cmd);

                HttpEntity<String> requestBody = new HttpEntity<String>(requestJson, headers);

                ResponseEntity<String> response = template.postForEntity(
                        base + cmd.getCmdType().toString().toLowerCase(),
                        requestBody,
                        String.class
                        );
        }

        ResponseEntity<String> response = template.getForEntity(base + "book", String.class);

        JSONAssert.assertEquals("IntegrationTest: Sample Session Order Book matched",
                finalBookJson, response.getBody(), dblJsonComp);
    }
}
