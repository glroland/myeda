package com.glroland.myeda.manualeda;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;

@Component
public class QuoteSimulationRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(Throwable.class).to("log:fatal");

        from("kafka:{{myeda.kafka.quoteHistoryTopic}}" + 
                "?brokers={{myeda.kafka.brokers}}" +
                "&securityProtocol=SSL" + 
                "&sslTruststoreLocation={{myeda.kafka.tsLocation}}" +
                "&sslTruststorePassword={{myeda.kafka.tsPassword}}")
            .log(LoggingLevel.INFO, "Quote History Record: $simple{body}")
            .unmarshal().json(HistoricQuote.class)
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    HistoricQuote payload = exchange.getIn().getBody(HistoricQuote.class);

                    Quote quote = new Quote();
                    quote.setSymbol(payload.getSymbol());
                    quote.setPrice(payload.getClosingPrice());

                    exchange.getIn().setBody(quote);
                }
            })
            .marshal().json()
            .log(LoggingLevel.INFO, "Quote Record: $simple{body}")
            .to("kafka:{{myeda.kafka.quoteTopic}}" + 
                    "?brokers={{myeda.kafka.brokers}}" +
                    "&securityProtocol=SSL" + 
                    "&sslTruststoreLocation={{myeda.kafka.tsLocation}}" +
                    "&sslTruststorePassword={{myeda.kafka.tsPassword}}");
    }
}
