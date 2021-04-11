package com.glroland.myeda.manualeda;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class TimerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // generate random number every second
        // which is send to this seda queue that the NumberPojo will consume
        from("timer:number?period=1000")
            .transform().simple("${random(0,200)}")
            .to("log:info");
    }

}