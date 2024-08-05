package com.lcm.twilio.templates;

import com.appian.connectedsystems.simplified.sdk.SimpleIntegrationTemplate;
import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.IntegrationError;
import com.appian.connectedsystems.templateframework.sdk.IntegrationResponse;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.appian.connectedsystems.templateframework.sdk.configuration.PropertyPath;
import com.appian.connectedsystems.templateframework.sdk.diagnostics.IntegrationDesignerDiagnostic;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.HashMap;
import java.util.Map;

// Must provide an integration id. This value need only be unique for this connected system
@TemplateId(name="WhatsappIntegrationTemplate")
public class WhatsappIntegrationTemplate extends SimpleIntegrationTemplate {

  public static final String MESSAGE = "msg";
  public static final String FROM = "from";
  public static final String TO = "to";

  @Override
  protected SimpleConfiguration getConfiguration(
          SimpleConfiguration integrationConfiguration,
          SimpleConfiguration connectedSystemConfiguration,
          PropertyPath propertyPath,
          ExecutionContext executionContext) {
    return integrationConfiguration.setProperties(
            textProperty(MESSAGE).label("Message")
                    .isRequired(true)
                    .description("Message to be sent via Twilio")
                    .build(),
            textProperty(FROM).label("From Number")
                    .isRequired(true)
                    .description("Phone number from which the message is sent")
                    .build(),
            textProperty(TO).label("To Number")
                    .isRequired(true)
                    .description("Phone number to which the message is sent")
                    .build()
    );
  }

  @Override
  protected IntegrationResponse execute(
          SimpleConfiguration integrationConfiguration,
          SimpleConfiguration connectedSystemConfiguration,
          ExecutionContext executionContext) {
    Map<String, Object> requestDiagnostic = new HashMap<>();
    Map<String, Object> result = new HashMap<>();

    try {
      String accountSID = connectedSystemConfiguration.getValue(TwilioConnectedSystemTemplate.ACCOUNT_SID);
      String authToken = connectedSystemConfiguration.getValue(TwilioConnectedSystemTemplate.AUTH_TOKEN);
      String messageBody = integrationConfiguration.getValue(MESSAGE);
      String from = "whatsapp:"+integrationConfiguration.getValue(FROM);
      String to = "whatsapp:"+integrationConfiguration.getValue(TO);

//      requestDiagnostic.put("accountSID", accountSID);
//      requestDiagnostic.put("authToken", authToken);
      requestDiagnostic.put("messageBody", messageBody);
      requestDiagnostic.put("from", from);
      requestDiagnostic.put("to", to);

      // Initialize Twilio client
      Twilio.init(accountSID, authToken);

      // Send the message
      final long start = System.currentTimeMillis();
      Message message = Message.creator(new PhoneNumber(to), new PhoneNumber(from), messageBody).create();
      final long end = System.currentTimeMillis();

      // Collect results
      result.put("messageSID", message.getSid());
      result.put("status", message.getStatus());
      result.put("from", from);
      result.put("to", to);

      // Add execution time to diagnostics
      final long executionTime = end - start;
      final IntegrationDesignerDiagnostic diagnostic = IntegrationDesignerDiagnostic.builder()
              .addExecutionTimeDiagnostic(executionTime)
              .addRequestDiagnostic(requestDiagnostic)
              .build();

      return IntegrationResponse
              .forSuccess(result)
              .withDiagnostic(diagnostic)
              .build();
    } catch (ApiException e) {
      // Handle Twilio API exceptions
      IntegrationError error = IntegrationError.builder()
                .title("Twilio API error")
                .message( "Twilio API error: " + e.getMessage())
                .build();
      return IntegrationResponse
              .forError(error)
              .withDiagnostic(IntegrationDesignerDiagnostic.builder()
                      .addRequestDiagnostic(requestDiagnostic)
                      .build())
              .build();
    } catch (Exception e) {
      IntegrationError error = IntegrationError.builder()
              .title("Unexpected error")
              .message( "Unexpected " + e.getMessage())
              .build();
      return IntegrationResponse
              .forError(error)
              .withDiagnostic(IntegrationDesignerDiagnostic.builder()
                      .addRequestDiagnostic(requestDiagnostic)
                      .build())
              .build();
    }
  }
}
