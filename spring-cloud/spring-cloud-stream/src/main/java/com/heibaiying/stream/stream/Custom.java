package com.heibaiying.stream.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author : heibaiying
 */
public interface Custom {

    String INPUT = "input";
    String OUTPUT = "output";

    @Input(Custom.INPUT)
    SubscribableChannel input();

    @Output(Custom.OUTPUT)
    MessageChannel output();

}
