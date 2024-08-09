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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.lcm.twilio.templates.utils.Utility.buildErrorResponse;

@TemplateId(name = "ReceiveMessageIntegrationTemplate")
public abstract class ReceiveMessageIntegrationTemplate extends SimpleIntegrationTemplate {

    public static final String TO = "to";
    public static final String LIMIT = "limit";
    public static final String START_DATE = "startDate";
    public static final String START_TIME = "startTime";
    public static final String END_DATE = "endDate";
    public static final String END_TIME = "endTime";
    public static final String STATUS = "status";

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
                        .build(),
                textProperty(START_DATE).label("Start Date (UTC) - Format YYYY-MM-DD")
                        .isRequired(false).isExpressionable(true)
                        .description("The start date to filter messages in UTC")
                        .build(),
                textProperty(START_TIME).label("Start Time (UTC) - Format HH:mm:ss")
                        .isRequired(false).isExpressionable(true)
                        .description("The start time to filter messages in UTC")
                        .build(),
                textProperty(END_DATE).label("End Date (UTC) - Format YYYY-MM-DD")
                        .isRequired(false).isExpressionable(true)
                        .description("The end date to filter messages in UTC")
                        .build(),
                textProperty(END_TIME).label("End Time (UTC) - Format HH:mm:ss")
                        .isRequired(false).isExpressionable(true)
                        .description("The end time to filter messages in UTC")
                        .build(),
                textProperty(STATUS).label("Message Status")
                        .isRequired(false).isExpressionable(true)
                        .description("Enter one or more statuses separated by commas (e.g., 'read, undelivered').")
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

            String startDateStr = integrationConfiguration.getValue(START_DATE);
            String startTimeStr = integrationConfiguration.getValue(START_TIME);
            String endDateStr = integrationConfiguration.getValue(END_DATE);
            String endTimeStr = integrationConfiguration.getValue(END_TIME);
            String statusStr = integrationConfiguration.getValue(STATUS);

            // Normalize statuses to lowercase and create the list
            List<String> statusFilters = statusStr == null || statusStr.isEmpty()
                    ? new ArrayList<>()
                    : Arrays.stream(statusStr.split("\\s*,\\s*"))
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            requestDiagnostic.put("to", to);
            requestDiagnostic.put("startDate", startDateStr);
            requestDiagnostic.put("startTime", startTimeStr);
            requestDiagnostic.put("endDate", endDateStr);
            requestDiagnostic.put("endTime", endTimeStr);
            requestDiagnostic.put("status", statusFilters);

            // Parse dates and times
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            LocalDateTime startDateTime = (startDateStr != null && startTimeStr != null)
                    ? LocalDateTime.of(LocalDate.parse(startDateStr, dateFormatter), LocalTime.parse(startTimeStr, timeFormatter))
                    : null;
            LocalDateTime endDateTime = (endDateStr != null && endTimeStr != null)
                    ? LocalDateTime.of(LocalDate.parse(endDateStr, dateFormatter), LocalTime.parse(endTimeStr, timeFormatter))
                    : null;

            Instant startInstant = startDateTime != null ? startDateTime.toInstant(ZoneOffset.UTC) : null;
            Instant endInstant = endDateTime != null ? endDateTime.toInstant(ZoneOffset.UTC) : null;

            // Initialize Twilio client
            Twilio.init(accountSID, authToken);

            // Receive the messages
            final long start = System.currentTimeMillis();
            ResourceSet<Message> messages = Message.reader()
                    .setTo(new PhoneNumber(getMessagePrefix() + to))
                    .limit(noOfMessages)
                    .read();

            // Manual filtering of messages
            List<Map<String, Object>> messagesList = new ArrayList<>();
            for (Message message : messages) {
                Instant messageDateSent = message.getDateSent().toInstant();

                // Filter based on date and time
                boolean isWithinDateRange = (startInstant == null || !messageDateSent.isBefore(startInstant)) &&
                        (endInstant == null || !messageDateSent.isAfter(endInstant));

                // Filter based on status
                boolean matchesStatus = statusFilters.isEmpty() ||
                        statusFilters.contains(message.getStatus().toString().toLowerCase());

                if (isWithinDateRange && matchesStatus) {
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("messageBody", message.getBody());
                    messageMap.put("status", message.getStatus());
                    messageMap.put("sid", message.getSid());
                    messageMap.put("from", message.getFrom().toString());
                    messageMap.put("to", message.getTo());
                    messageMap.put("dateSent", message.getDateSent().toString());
                    messagesList.add(messageMap);
                }
            }

            result.put("Messages", messagesList);

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
