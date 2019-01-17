package com.heibaiying.stream.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author : heibaiying
 */
public interface CustomStream {

    String INPUT = "customInput";
    String OUTPUT = "customOutput";

    @Input(CustomStream.INPUT)
    SubscribableChannel input();

    @Output(CustomStream.OUTPUT)
    MessageChannel output();

}
