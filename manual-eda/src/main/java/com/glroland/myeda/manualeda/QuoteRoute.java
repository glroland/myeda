package com.glroland.myeda.manualeda;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class QuoteRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // generate random number every second
        // which is send to this seda queue that the NumberPojo will consume
        from("ftp:{{myeda.ftp.username}}@{{myeda.ftp.host}}:{{myeda.ftp.port}}/{{myeda.ftp.directoryName}}?" +
                "password={{myeda.ftp.password}}" +
                "&download=true")
            .split(bodyAs(String.class).tokenize("\n"))
            .unmarshal().csv()
            
            .to("log:info");
    }

}
