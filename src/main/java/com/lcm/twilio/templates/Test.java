package com.lcm.twilio.templates;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.base.ResourceSet;

public class Test {
    // Find your Account SID and Auth Token at twilio.com/console
    public static final String ACCOUNT_SID = "ACd7c17456bace5bc27bc468918695286c";
    public static final String AUTH_TOKEN = "faa97fe61f8dee34b195e39d5171ad56";

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Fetch messages for a specific phone number
        String phoneNumber = "+917871222941"; // Replace with your phone number
        ResourceSet<Message> messages = Message.reader()
                .setTo(new com.twilio.type.PhoneNumber(phoneNumber))
                .limit(10) // Adjust the limit as needed
                .read();

        for (Message message : messages) {
            System.out.println("From: " + message.getFrom());
            System.out.println("To: " + message.getTo());
            System.out.println("Body: " + message.getBody());
            System.out.println("Date Sent: " + message.getDateSent());
            System.out.println("Status: " + message.getStatus());
            System.out.println("sid: " + message.getSid());
            System.out.println("-----");
        }
    }
}
