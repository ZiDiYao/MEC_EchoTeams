
package com.echoteam.MEC.llm.support;

import com.echoteam.MEC.dto.NormalizedAnalysisRequest;
import org.springframework.stereotype.Component;

/**
 * Are responsible for building very exact Prompt
 */
@Component
public class PromptBuilderImpl implements PromptBuilder {


    public String buildPrompt(NormalizedAnalysisRequest req) {
        String transcript = safe(req.getTranscript());
        String phone = safe(req.getPhoneNumber());
        String requestId = safe(req.getRequestId());
        String time = req.getTimeReported() != null ? req.getTimeReported().toString() : "UNKNOWN";

        // context
        String systemInstruction = """
                You are an emergency call analysis assistant.
                Your task is to read the caller's transcript carefully and extract structured, factual information.
                You MUST output a single valid JSON object strictly following the schema below.
                DO NOT include explanations, markdown, or any text outside the JSON.
                Every field MUST exist in the JSON even if not mentioned in the transcript.
                If the information is not explicitly stated, assign it as "UNKNOWN" (for strings)
                or appropriate neutral values (false, 0, [] etc.).
                """;

        String schemaExplanation = """
                JSON Field Specification:
                
                {
                  "requestId": (string) — same as the input requestId, used for tracking.
                  "phoneNumber": (string) — same as the input phone number in E.164 format.
                  "callerName": (string) — name or identifier of the caller. "UNKNOWN" if not stated.
                  "timeReported": (string, ISO timestamp) — when the incident was reported; use provided input or "UNKNOWN".
                  "incidentType": (string) — main category, choose from ["FIRE", "ACCIDENT", "MEDICAL", "CRIME", "OTHER"].
                                       If multiple possible, choose the most likely; if unclear, return "UNKNOWN".
                  "incidentDescription": (string) — short summary of what happened, in plain English. "UNKNOWN" if unclear.
                  "address": (string) — street address or location explicitly mentioned; otherwise "UNKNOWN".
                  "landmark": (string) — nearby landmark (e.g., "near gas station"); otherwise "UNKNOWN".
                  "victimCount": (integer) — number of victims mentioned; 0 if not specified.
                  "victimDescription": (string) — age/gender/clothing; "UNKNOWN" if not stated.
                  "injurySeverity": (string) — one of ["Minor", "Serious", "Unknown"].
                  "isConscious": (boolean) — true if caller/victim is described as conscious; false if unconscious; false if not stated.
                  "isBreathing": (boolean) — true if breathing is mentioned; false otherwise.
                  "isOngoing": (boolean) — true if the situation is still in progress; false if ended; false if unclear.
                  "isFire": (boolean) — true if there is fire, explosion, smoke; false otherwise.
                  "urgencyLevel": (integer) — integer between 1–100 estimating severity/urgency.
                                      Use 50 if not inferable.
                  "keyPhrases": (array of strings) — 3–10 key phrases or keywords extracted from transcript.
                  "confidence": (integer) — integer between 0–100 indicating how confident the model is about its own extraction.
                  "processedBy": (string) — leave empty or "UNKNOWN"; to be filled by backend later.
                }
                """;

        // Input
        String inputContext = """
                Input Information:
                - requestId: %s
                - phoneNumber: %s
                - timeReported: %s
                
                Transcript:
                %s
                """.formatted(requestId, phone, time, transcript);
        // Build all of the above together

        return systemInstruction + "\n\n" + schemaExplanation + "\n\n" +
                "Now, output ONLY the JSON object below with all fields populated as described.\n\n" +
                inputContext;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
