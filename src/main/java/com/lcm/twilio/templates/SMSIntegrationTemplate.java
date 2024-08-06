package com.lcm.twilio.templates;

import com.appian.connectedsystems.templateframework.sdk.TemplateId;

@TemplateId(name = "SMSIntegrationTemplate")
public class SMSIntegrationTemplate extends TwilioIntegrationTemplate {

  @Override
  protected String getMessagePrefix() {
    return ""; // No prefix for SMS
  }
}
