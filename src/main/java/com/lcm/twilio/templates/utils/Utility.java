package com.lcm.twilio.templates.utils;

import com.appian.connectedsystems.templateframework.sdk.IntegrationError;
import com.appian.connectedsystems.templateframework.sdk.IntegrationResponse;
import com.appian.connectedsystems.templateframework.sdk.diagnostics.IntegrationDesignerDiagnostic;

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
}
