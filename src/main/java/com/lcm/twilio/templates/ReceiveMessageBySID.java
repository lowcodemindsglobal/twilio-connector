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

import java.util.*;

import static com.lcm.twilio.templates.utils.Utility.*;

@TemplateId(name = "ReceiveMessageBySID")
public abstract class ReceiveMessageBySID extends SimpleIntegrationTemplate {

    public static final String TO = "to";
    public static final String SID = "sid";

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
                        .description("phone number to ch messages are sent")
                        .build(),
                textProperty(SID).label("String Identifier")
                        .isRequired(true).isExpressionable(true)
                        .description("")
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
            String sid = integrationConfiguration.getValue(SID);
            requestDiagnostic = createRequestDiagnostic(to, sid);

            // Initialize Twilio client
            Twilio.init(accountSID, authToken);

            // Receive the messages
            final long start = System.currentTimeMillis();
            ResourceSet<Message> messages = Message.reader()
                    .setTo(new PhoneNumber(getMessagePrefix() + to))
                    .read();
            for (Message message : messages) {
                if (message.getSid().equals(sid)) {
                    result.put("Sid", message.getSid());
                    result.put("Body", message.getBody());
                    result.put("DateSent", message.getDateSent());
                    result.put("Status", message.getStatus());
                    break;
                }
            }


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
