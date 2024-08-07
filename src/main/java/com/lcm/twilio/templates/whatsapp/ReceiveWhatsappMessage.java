package com.lcm.twilio.templates.whatsapp;

import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.lcm.twilio.templates.ReceiveMessageIntegrationTemplate;


@TemplateId(name = "ReceiveWhatsappMessage")
public  class ReceiveWhatsappMessage extends ReceiveMessageIntegrationTemplate {


  protected String getMessagePrefix() {
    return "whatsapp:";
  }
}
