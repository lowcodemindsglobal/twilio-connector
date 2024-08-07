package com.lcm.twilio.templates.whatsapp;

import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.lcm.twilio.templates.TwilioIntegrationTemplate;

@TemplateId(name = "WhatsappIntegrationTemplate")
public class WhatsappIntegrationTemplate extends TwilioIntegrationTemplate {

  @Override
  protected String getMessagePrefix() {
    return "whatsapp:";
  }
}
