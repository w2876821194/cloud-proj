package com.imooc.employee.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    @JmsListener(destination = "YourQueueName")
    public void receive(String message) {
        System.out.println("Received message: " + message);
    }
}
