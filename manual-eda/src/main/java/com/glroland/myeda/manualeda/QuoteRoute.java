package com.glroland.myeda.manualeda;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;

@Component
public class QuoteRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        DataFormat bindy = new BindyCsvDataFormat(HistoricQuote.class);

        onException(Throwable.class).to("log:fatal");

        from("ftp:{{myeda.ftp.username}}@{{myeda.ftp.host}}:{{myeda.ftp.port}}/{{myeda.ftp.directoryName}}?" +
                "password={{myeda.ftp.password}}" +
                "&download=true" +
                "&passiveMode=true" + 
                "&recursive=false" +
                "&move=.done" +
                "&moveFailed=.failed")
            .log(LoggingLevel.INFO, "Received file via FTP for processing: $simple{in.header.CamelFileName}")
            .to("direct:processFile")
            .log(LoggingLevel.INFO, "Processed file received via FTP: $simple{in.header.CamelFileName}");
        
        from("direct:processFile")
            .split(bodyAs(String.class).tokenize("\n"))
            .filter(simple("${header.CamelSplitIndex} > 0"))
            .unmarshal(bindy)
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    HistoricQuote payload = exchange.getIn().getBody(HistoricQuote.class);

                    String relativeFilename = (String)exchange.getIn().getHeader("CamelFileRelativePath");
                    String symbol = null;
                    int firstDotIndex = relativeFilename.indexOf(".");
                    if (firstDotIndex != -1)
                        symbol = relativeFilename.substring(0, firstDotIndex);
                    else
                        symbol = relativeFilename;
                    payload.setSymbol(symbol);

                    exchange.getIn().setBody(payload);
                }
            })
            .marshal().json()
            .to("direct:historicQuoteRecord");

        from("direct:historicQuoteRecord")
            .log(LoggingLevel.DEBUG, "Quote History Record: $simple{body}");    
    }

}
