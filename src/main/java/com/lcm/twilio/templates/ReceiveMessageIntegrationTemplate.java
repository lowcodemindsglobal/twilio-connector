package com.lcm.twilio.templates;

import com.appian.connectedsystems.simplified.sdk.SimpleIntegrationTemplate;
import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.IntegrationResponse;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.appian.connectedsystems.templateframework.sdk.configuration.PropertyPath;
import com.appian.connectedsystems.templateframework.sdk.diagnostics.IntegrationDesignerDiagnostic;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.lcm.twilio.templates.utils.Utility.buildErrorResponse;

@TemplateId(name = "ReceiveMessageIntegrationTemplate")
public  abstract class ReceiveMessageIntegrationTemplate extends SimpleIntegrationTemplate {

    public static final String TO = "to";
    public static final String LIMIT = "limit";

    protected abstract String getMessagePrefix();


    @Override
    protected SimpleConfiguration getConfiguration(
            SimpleConfiguration integrationConfiguration,
            SimpleConfiguration connectedSystemConfiguration,
            PropertyPath propertyPath,
            ExecutionContext executionContext) {
        return integrationConfiguration.setProperties(
                textProperty(TO).label("To Number")
                        .isRequired(true).isExpressionable(true)
                        .description("The phone number to which the messages are received")
                        .build(),
                integerProperty(LIMIT).label("Message Limit")
                        .isRequired(true).isExpressionable(true)
                        .description("Total Number of messages to receive")
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
            String to = integrationConfiguration.getValue(TO);
            int noOfMessages = integrationConfiguration.getValue(LIMIT);

            requestDiagnostic.put("to", to);

            // Initialize Twilio client
            Twilio.init(accountSID, authToken);

            // Receive the messages
            final long start = System.currentTimeMillis();
            ResourceSet<Message> messages = Message.reader()
                    .setTo(new PhoneNumber(getMessagePrefix()+to))
                    .limit(noOfMessages) // Adjust the limit as needed
                    .read();
            List<Map<String, Object>> messagesList = new ArrayList<>();
            for (Message message : messages) {
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("messageBody", message.getBody());
                messageMap.put("status",message.getStatus());
                messageMap.put("sid", message.getSid());
                messageMap.put("from",message.getFrom().toString());
                messageMap.put("to",message.getTo());
                messageMap.put("dateSent",message.getDateSent().toString());
                messagesList.add(messageMap);
            }
            result.put("Messages",messagesList);

            final long end = System.currentTimeMillis();
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
