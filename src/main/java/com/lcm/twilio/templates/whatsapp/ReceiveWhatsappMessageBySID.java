package com.lcm.twilio.templates.whatsapp;

import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.lcm.twilio.templates.ReceiveMessageBySID;


@TemplateId(name = "ReceiveWhatsappMessageBySID")
public  class ReceiveWhatsappMessageBySID extends ReceiveMessageBySID {


  @Override
  protected String getMessagePrefix() {
    return "whatsapp:";
  }
}
