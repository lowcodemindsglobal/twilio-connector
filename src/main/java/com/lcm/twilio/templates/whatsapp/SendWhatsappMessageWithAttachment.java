package com.lcm.twilio.templates.whatsapp;

import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.IntegrationResponse;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.appian.connectedsystems.templateframework.sdk.configuration.PropertyPath;
import com.appian.connectedsystems.templateframework.sdk.diagnostics.IntegrationDesignerDiagnostic;
import com.lcm.twilio.templates.TwilioConnectedSystemTemplate;
import com.lcm.twilio.templates.TwilioIntegrationTemplate;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.util.HashMap;
import java.util.Map;
import static com.lcm.twilio.templates.utils.Utility.buildErrorResponse;

@TemplateId(name = "SendWhatsappMessageWithAttachment")
public class SendWhatsappMessageWithAttachment extends TwilioIntegrationTemplate {

    public static final String MESSAGE = "msg";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String MEDIA_URL = "mediaUrl";

    @Override
    protected String getMessagePrefix() {
        return "whatsapp:"; // No prefix for this example, but you can set one if needed.
    }

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
                                .build(),
                        textProperty(MEDIA_URL).label("Media URL")
                                .isRequired(false).isExpressionable(true)
                                .description("URL of the media to be attached to the message")
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
            String mediaUrl = integrationConfiguration.getValue(MEDIA_URL);

            requestDiagnostic.put("messageBody", messageBody);
            requestDiagnostic.put("from", from);
            requestDiagnostic.put("to", to);
            requestDiagnostic.put("mediaUrl", mediaUrl);

            // Initialize Twilio client
            Twilio.init(accountSID, authToken);
            final long start = System.currentTimeMillis();
            // Create and send the message
            Message message;
            if (mediaUrl != null && !mediaUrl.isEmpty()) {
                message = Message.creator(
                                new PhoneNumber(to),         // To number
                                new PhoneNumber(from),       // From number
                                messageBody                  // Message body
                        )
                        .setMediaUrl(mediaUrl) // Set media URL
                        .create();                       // Create and send message
            } else {
                message = Message.creator(
                        new PhoneNumber(to),         // To number
                        new PhoneNumber(from),       // From number
                        messageBody                  // Message body
                ).create();                       // Create and send message
            }

            // Collect results
            result.put("from", from);
            result.put("to", to);
            result.put("message", message.getBody());
            result.put("messageSID", message.getSid());
            result.put("numMedia",message.getNumMedia());
            result.put("status", message.getStatus());

            // Add execution time to diagnostics
            final long executionTime = System.currentTimeMillis() - start;
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
