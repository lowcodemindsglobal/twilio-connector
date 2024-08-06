package com.lcm.twilio.templates;


import com.appian.connectedsystems.simplified.sdk.SimpleConnectedSystemTemplate;
import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;

@TemplateId(name="TwilioConnectedSystemTemplate")
public class TwilioConnectedSystemTemplate extends SimpleConnectedSystemTemplate {

  public static final String ACCOUNT_SID = "accountSID";
  public static final String AUTH_TOKEN = "authToken";


  @Override
  protected SimpleConfiguration getConfiguration(
          SimpleConfiguration simpleConfiguration, ExecutionContext executionContext) {

    return simpleConfiguration.setProperties(
            encryptedTextProperty(ACCOUNT_SID)
                    .label("Account SID")
                    .description("Account SID for Twilio")
                    .isRequired(true)
                    .isImportCustomizable(true)
                    .build(),
            encryptedTextProperty(AUTH_TOKEN)
                    .label("Auth Token")
                    .description("Auth Token for Twilio")
                    .isRequired(true)
                    .isImportCustomizable(true)
                    .build()
    );
  }

}
