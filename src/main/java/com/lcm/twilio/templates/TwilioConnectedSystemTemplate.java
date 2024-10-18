package com.lcm.twilio.templates;

import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.simplified.sdk.connectiontesting.SimpleTestableConnectedSystemTemplate;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.appian.connectedsystems.templateframework.sdk.connectiontesting.TestConnectionResult;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.Account;

@TemplateId(name = "TwilioConnectedSystemTemplate")
public class TwilioConnectedSystemTemplate extends SimpleTestableConnectedSystemTemplate {

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

  @Override
  protected TestConnectionResult testConnection(SimpleConfiguration configuration, ExecutionContext executionContext) {
    String accountSid = configuration.getValue(ACCOUNT_SID);
    String authToken = configuration.getValue(AUTH_TOKEN);

    try {
      Twilio.init(accountSid, authToken);
      // Attempt to fetch the account to verify credentials
      ResourceSet<Account> accounts = Account.reader().read();
      if (accounts.iterator().hasNext()) {
          return TestConnectionResult.success();
      } else {
        return TestConnectionResult.error("Connection failed: No accounts found.");
      }
    } catch (Exception e) {
      return TestConnectionResult.error("Connection failed: " + e.getMessage());
    }
  }
}
