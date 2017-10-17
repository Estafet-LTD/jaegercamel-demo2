package com.estafet.batchprime;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class CamelBatchValidator extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        String validator_url = System.getenv("VALIDATOR_URL");
        String rabbitmqUrl = System.getenv("RABBITMQ_URL");

        from("netty4-http:http://0.0.0.0:8080/batchPrime")
                .unmarshal().json(JsonLibrary.Jackson, PrimeBatch.class)
                .split().simple("body.getPrimeList()")
                    .parallelProcessing()
                    .convertBodyTo(String.class)
                    .setHeader(Exchange.HTTP_QUERY, simple("num=${body}"))
                    .to("http://" + validator_url + "/isPrime?bridgeEndpoint=true")
                    .convertBodyTo(String.class)
                    .to("log:foo")
                .end()
                .to("rabbitmq://" + rabbitmqUrl + "/amq.direct?autoDelete=false&routingKey=register")
                .setBody()
                .simple("All done here!");

    }
}
