package com.lcm.twilio.templates;


import com.appian.connectedsystems.simplified.sdk.SimpleConnectedSystemTemplate;
import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.simplified.sdk.oauth.SimpleOAuthConnectedSystemTemplate;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.appian.connectedsystems.templateframework.sdk.oauth.OAuthConfigurationData;

@TemplateId(name="TwilioConnectedSystemTemplate")
public class TwilioConnectedSystemTemplate extends SimpleConnectedSystemTemplate {

  public static final String ACCOUNT_SID = "accountSID";
  public static final String AUTH_TOKEN = "authToken";
  public static final String AUTH_URL = "authURL";
  public static final String TOKEN_URL = "tokenURL";
  public static final String SCOPE = "scope";

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
//            ,
//            textProperty(AUTH_URL)
//                    .label("Auth URL")
//                    .description("URL for authentication")
//                    .isRequired(true)
//                    .isImportCustomizable(true)
//                    .build(),
//            textProperty(TOKEN_URL)
//                    .label("Token URL")
//                    .description("URL for token generation")
//                    .isRequired(true)
//                    .isImportCustomizable(true)
//                    .build(),
//            textProperty(SCOPE)
//                    .label("Scope")
//                    .description("Scope for authentication")
//                    .isRequired(true)
//                    .isImportCustomizable(true)
//                    .build()
    );
  }

//  @Override
//  protected OAuthConfigurationData getOAuthConfiguration(SimpleConfiguration simpleConfiguration) {
//    return OAuthConfigurationData.builder()
//            .authUrl(simpleConfiguration.getValue(AUTH_URL))
//            .clientId(simpleConfiguration.getValue(ACCOUNT_SID))
//            .clientSecret(simpleConfiguration.getValue(AUTH_TOKEN))
//            .scope(simpleConfiguration.getValue(SCOPE))
//            .tokenUrl(simpleConfiguration.getValue(TOKEN_URL))
//            .build();
//  }
}
