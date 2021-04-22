import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

public class ProcessPriceHistoryFileRoute extends RouteBuilder {

    @CsvRecord( separator = "," )
    public static class HistoricStockPrice {

        private String symbol;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @DataField(pos = 1, position = 1, pattern="yyyy-MM-dd")
        private Date date;

        @DataField(pos = 2, position = 2)
        private double openPrice;

        @DataField(pos = 3, position = 3)
        private double dailyHighPrice;

        @DataField(pos = 4, position = 4)
        private double dailyLowPrice;

        @DataField(pos = 5, position = 5)
        private double closingPrice;

        @DataField(pos = 6, position = 6)
        private double adjustedClosingPrice;

        @DataField(pos = 7, position = 7)
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

    public void configure() throws Exception {
        DataFormat bindy = new BindyCsvDataFormat(HistoricStockPrice.class);

        onException(Throwable.class).to("log:fatal");

        from("ftp:{{stock.ftp.username}}@{{stock.ftp.host}}:{{stock.ftp.port}}/{{stock.ftp.directoryName}}?" +
                "password={{stock.ftp.password}}" +
                "&download=true" +
                "&passiveMode=true" + 
                "&recursive=false" +
                "&stepwise=true" +
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
                    HistoricStockPrice payload = exchange.getIn().getBody(HistoricStockPrice.class);

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
            .to("direct:historicPriceRecord");

        from("direct:historicPriceRecord")
            .log(LoggingLevel.DEBUG, "Stock Price History Record: $simple{body}")
            .to("kafka:{{stock.kafka.priceHistoryTopic}}" + 
                    "?brokers={{stock.kafka.brokers}}");
    }

}

