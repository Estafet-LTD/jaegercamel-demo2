package com.estafet.batchprime;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ThreadPoolRejectedPolicy;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.ThreadPoolProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CamelBatchValidator extends RouteBuilder {
    @Autowired
    CamelContext context;

    @Override
    public void configure() throws Exception {
        ThreadPoolProfile profile = new ThreadPoolProfile("bigPool");
        profile.setMaxPoolSize(100);
        profile.setMaxQueueSize(100);
        profile.setPoolSize(20);
        profile.setKeepAliveTime(1L);
        profile.setTimeUnit(TimeUnit.MINUTES);

        profile.setRejectedPolicy(ThreadPoolRejectedPolicy.DiscardOldest);

        context.getExecutorServiceManager().registerThreadPoolProfile(profile);

        String validator_url = System.getenv("VALIDATOR_URL");
        from("netty4-http:http://0.0.0.0:8080/batchPrime")
                .unmarshal().json(JsonLibrary.Jackson, PrimeBatch.class)
                .split().simple("body.getPrimeList()")
                .parallelProcessing().executorServiceRef("bigPool")
                    .convertBodyTo(String.class)
                    .setHeader(Exchange.HTTP_QUERY, simple("num=${body}"))
                    .to("http://"+validator_url+"/isPrime?bridgeEndpoint=true")
                    .convertBodyTo(String.class)
                    .to("log:foo")
                .end();
    }
}
