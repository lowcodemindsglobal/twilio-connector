package com.lcm.twilio.templates.sms;

import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.lcm.twilio.templates.SendMessageIntegrationTemplate;

@TemplateId(name = "SendSMSIntegrationTemplate")
public class SendSMSIntegrationTemplate extends SendMessageIntegrationTemplate {

  @Override
  protected String getMessagePrefix() {
    return ""; // No prefix for SMS
  }
}
