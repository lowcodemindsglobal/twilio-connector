package com.lcm.twilio.templates.sms;

import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.lcm.twilio.templates.TwilioIntegrationTemplate;

@TemplateId(name = "SMSIntegrationTemplate")
public class SMSIntegrationTemplate extends TwilioIntegrationTemplate {

  @Override
  protected String getMessagePrefix() {
    return ""; // No prefix for SMS
  }
}
