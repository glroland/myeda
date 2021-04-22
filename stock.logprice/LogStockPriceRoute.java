import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class LogStockPriceRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        onException(Throwable.class).to("log:fatal");

        from("kafka:{{stock.kafka.priceTopic}}" + 
                "?brokers={{stock.kafka.brokers}}")
            .log(LoggingLevel.INFO, "Stock Price Record in JSON: $simple{body}")
            .to("atlasmap:mapping.json")
            .log(LoggingLevel.INFO, "Stock Price Record in XML: $simple{body}");
    }
}
