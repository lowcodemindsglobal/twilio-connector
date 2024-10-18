package com.lcm.twilio.templates.whatsapp;

import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.lcm.twilio.templates.SendMessageIntegrationTemplate;

@TemplateId(name = "SendWhatsappIntegrationTemplate")
public class SendWhatsappIntegrationTemplate extends SendMessageIntegrationTemplate {

  @Override
  protected String getMessagePrefix() {
    return "whatsapp:";
  }
}
