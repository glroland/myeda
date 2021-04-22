import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.DataFormat;
import java.util.Date;
import org.apache.camel.component.jackson.JacksonDataFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class StockPriceSimulationRoute extends RouteBuilder {

    public static class HistoricStockPrice {

        private String symbol;
    
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date date;
    
        private double openPrice;
    
        private double dailyHighPrice;
    
        private double dailyLowPrice;
    
        private double closingPrice;
    
        private double adjustedClosingPrice;
    
        private long dailyVolume;
    
        public String getSymbol() {
            return symbol;
        }
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
        public Date getDate() {
            return date;
        }
        public void setDate(Date date) {
            this.date = date;
        }
        public double getOpenPrice() {
            return openPrice;
        }
        public void setOpenPrice(double openPrice) {
            this.openPrice = openPrice;
        }
        public double getDailyHighPrice() {
            return dailyHighPrice;
        }
        public void setDailyHighPrice(double dailyHighPrice) {
            this.dailyHighPrice = dailyHighPrice;
        }
        public double getDailyLowPrice() {
            return dailyLowPrice;
        }
        public void setDailyLowPrice(double dailyLowPrice) {
            this.dailyLowPrice = dailyLowPrice;
        }
        public double getClosingPrice() {
            return closingPrice;
        }
        public void setClosingPrice(double closingPrice) {
            this.closingPrice = closingPrice;
        }
        public long getDailyVolume() {
            return dailyVolume;
        }
        public void setDailyVolume(long dailyVolume) {
            this.dailyVolume = dailyVolume;
        }
    
        public double getAdjustedClosingPrice() {
            return adjustedClosingPrice;
        }
        public void setAdjustedClosingPrice(double adjustedClosingPrice) {
            this.adjustedClosingPrice = adjustedClosingPrice;
        }
    
        
    }
    
    public static class StockPrice {

        private String symbol;
    
        private double price;
    
        public String getSymbol() {
            return symbol;
        }
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
        public double getPrice() {
            return price;
        }
        public void setPrice(double price) {
            this.price = price;
        }
    }
    
    
    @Override
    public void configure() throws Exception {
        onException(Throwable.class).to("log:fatal");

        JacksonDataFormat df = new JacksonDataFormat(HistoricStockPrice.class);

        from("kafka:{{stock.kafka.priceHistoryTopic}}" + 
                "?brokers={{stock.kafka.brokers}}")
            .log(LoggingLevel.DEBUG, "Stock Price History Record: $simple{body}")
            .unmarshal(df)
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    HistoricStockPrice payload = exchange.getIn().getBody(HistoricStockPrice.class);

                    StockPrice stockPrice = new StockPrice();
                    stockPrice.setSymbol(payload.getSymbol());
                    stockPrice.setPrice(payload.getClosingPrice());

                    exchange.getIn().setBody(stockPrice);
                }
            })
            .marshal().json()
            .log(LoggingLevel.DEBUG, "Stock Price Record: $simple{body}")
            .to("kafka:{{stock.kafka.priceTopic}}" + 
                    "?brokers={{stock.kafka.brokers}}");
    }
}
