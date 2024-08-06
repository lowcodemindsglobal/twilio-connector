package com.lcm.twilio.templates;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMSTest {
    public static void main(String[] args){
        String accountSID ="ACd7c17456bace5bc27bc468918695286c";
        String authToken ="faa97fe61f8dee34b195e39d5171ad56";
        String from = "+16283458401";
        String to ="+917871222941";
        String msg ="hi from sangeerththan";

        Twilio.init(accountSID, authToken);
    try {
        Message message = Message.creator(new PhoneNumber(to),new PhoneNumber(from), msg).create();
    } catch (Exception e){
        System.out.println("*****************"+e.getMessage()+"****************");
    }
    }
}
