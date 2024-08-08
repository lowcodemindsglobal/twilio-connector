package com.lcm.twilio.templates;

import com.appian.connectedsystems.simplified.sdk.SimpleIntegrationTemplate;
import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.IntegrationResponse;
import com.appian.connectedsystems.templateframework.sdk.configuration.PropertyPath;
import com.appian.connectedsystems.templateframework.sdk.diagnostics.IntegrationDesignerDiagnostic;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.HashMap;
import java.util.Map;
import static com.lcm.twilio.templates.utils.Utility.buildErrorResponse;

public abstract class TwilioIntegrationTemplate extends SimpleIntegrationTemplate {

  public static final String MESSAGE = "msg";
  public static final String FROM = "from";
  public static final String TO = "to";

  protected abstract String getMessagePrefix();

  @Override
  protected SimpleConfiguration getConfiguration(
          SimpleConfiguration integrationConfiguration,
          SimpleConfiguration connectedSystemConfiguration,
          PropertyPath propertyPath,
          ExecutionContext executionContext) {
    return integrationConfiguration.setProperties(
            textProperty(MESSAGE).label("Message")
                    .isRequired(true).isExpressionable(true)
                    .description("Message to be sent via Twilio")
                    .build(),
            textProperty(FROM).label("From Number")
                    .isRequired(true).isExpressionable(true)
                    .description("Phone number from which the message is sent")
                    .build(),
            textProperty(TO).label("To Number")
                    .isRequired(true).isExpressionable(true)
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

      // Based on prefix integrations will be sms or whatsapp msg
      String from = getMessagePrefix() + integrationConfiguration.getValue(FROM);
      String to = getMessagePrefix() + integrationConfiguration.getValue(TO);

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
      result.put("from", from);
      result.put("to", to);
      result.put("message",message.getBody());
      result.put("messageSID", message.getSid());
      result.put("status", message.getStatus());

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
      return buildErrorResponse("Twilio API error", e.getMessage(), requestDiagnostic);
    } catch (Exception e) {
      return buildErrorResponse("Unexpected error", e.getMessage(), requestDiagnostic);
    }
  }

}
