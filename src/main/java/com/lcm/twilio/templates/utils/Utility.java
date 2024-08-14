package com.lcm.twilio.templates.utils;

import com.appian.connectedsystems.templateframework.sdk.IntegrationError;
import com.appian.connectedsystems.templateframework.sdk.IntegrationResponse;
import com.appian.connectedsystems.templateframework.sdk.diagnostics.IntegrationDesignerDiagnostic;
import com.twilio.rest.api.v2010.account.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utility {
    public static IntegrationResponse buildErrorResponse(String title, String message, Map<String, Object> requestDiagnostic) {
        IntegrationError error = IntegrationError.builder()
                .title(title)
                .message(message)
                .build();
        IntegrationDesignerDiagnostic diagnostic = IntegrationDesignerDiagnostic.builder()
                .addRequestDiagnostic(requestDiagnostic)
                .build();
        return IntegrationResponse.forError(error).withDiagnostic(diagnostic).build();
    }

    public static Map<String, Object> createRequestDiagnosticWithMedia(String messageBody, String from, String to, String mediaUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("messageBody", messageBody);
        map.put("from", from);
        map.put("to", to);
        map.put("mediaUrl", mediaUrl);
        return map;
    }
    public static Map<String, Object> createResultMap(String from, String to, Message message) {
        Map<String, Object> result = new HashMap<>();
        result.put("from", from);
        result.put("to", to);
        result.put("message", message.getBody());
        result.put("messageSID", message.getSid());
        result.put("numMedia", message.getNumMedia());
        result.put("status", message.getStatus());
        return result;
    }
    public static Map<String, Object> createRequestDiagnostic(String messageBody, String from, String to) {
        Map<String, Object> map = new HashMap<>();
        map.put("messageBody", messageBody);
        map.put("from", from);
        map.put("to", to);
        return map;
    }
    public static Map<String, Object> createRequestDiagnostic(
            String to, String startDateStr, String startTimeStr,
            String endDateStr, String endTimeStr, List<String> statusFilters) {
        Map<String, Object> map = new HashMap<>();
        map.put("to", to);
        map.put("startDate", startDateStr);
        map.put("startTime", startTimeStr);
        map.put("endDate", endDateStr);
        map.put("endTime", endTimeStr);
        map.put("status", statusFilters);
        return map;
    }

    public static Map<String, Object> createMessageMap(Message message) {
        Map<String, Object> map = new HashMap<>();
        map.put("messageBody", message.getBody());
        map.put("status", message.getStatus());
        map.put("sid", message.getSid());
        map.put("from", message.getFrom().toString());
        map.put("to", message.getTo());
        map.put("dateSent", message.getDateSent() != null ? message.getDateSent().toString() : "N/A");
        return map;
    }
}
