package com.lcm.twilio.templates;

import com.appian.connectedsystems.templateframework.sdk.TemplateId;

@TemplateId(name = "WhatsappIntegrationTemplate")
public class WhatsappIntegrationTemplate extends TwilioIntegrationTemplate {

  @Override
  protected String getMessagePrefix() {
    return "whatsapp:";
  }
}
