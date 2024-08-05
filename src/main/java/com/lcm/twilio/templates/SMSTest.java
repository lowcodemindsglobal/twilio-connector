package com.lcm.twilio.templates;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMSTest {
    public static void main(String[] args){
        String accountSID ="";
        String authToken ="";
        String from = "";
        String to ="";
        String msg ="hi from sangeerththan";

        Twilio.init(accountSID, authToken);
    try {
        Message message = Message.creator(new PhoneNumber(to),new PhoneNumber(from), msg).create();
    } catch (Exception e){
        System.out.println("*****************"+e.getMessage()+"****************");
    }
    }
}
