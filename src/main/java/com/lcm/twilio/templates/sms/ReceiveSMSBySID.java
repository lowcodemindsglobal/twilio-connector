package com.lcm.twilio.templates.sms;

import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.lcm.twilio.templates.ReceiveMessageBySID;


@TemplateId(name = "ReceiveSMSBySID")
public  class ReceiveSMSBySID extends ReceiveMessageBySID {


  @Override
  protected String getMessagePrefix() {
    return "";
  }
}
