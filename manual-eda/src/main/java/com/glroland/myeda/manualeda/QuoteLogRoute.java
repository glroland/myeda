package com.glroland.myeda.manualeda;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class QuoteLogRoute  extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        onException(Throwable.class).to("log:fatal");

        from("kafka:{{myeda.kafka.quoteTopic}}" + 
                "?brokers={{myeda.kafka.brokers}}" +
                "&securityProtocol=SSL" + 
                "&sslTruststoreLocation={{myeda.kafka.tsLocation}}" +
                "&sslTruststorePassword={{myeda.kafka.tsPassword}}")
            .log(LoggingLevel.INFO, "Quote Record in JSON: $simple{body}")
            .to("atlasmap:atlasmap-mapping.adm")
            .log(LoggingLevel.INFO, "Quote Record in XML: $simple{body}");
    }
}
