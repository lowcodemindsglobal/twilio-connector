package com.lcm.twilio.templates.sms;

import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.lcm.twilio.templates.ReceiveMessageIntegrationTemplate;


@TemplateId(name = "ReceiveSMSIntegrationTemplate")
public  class ReceiveSMSIntegrationTemplate extends ReceiveMessageIntegrationTemplate {


  @Override
  protected String getMessagePrefix() {
    return "";
  }
}
