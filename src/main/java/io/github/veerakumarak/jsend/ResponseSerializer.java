package io.github.veerakumarak.jsend;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ResponseSerializer extends JsonSerializer<Response<?>> {

    @Override
    public void serialize(Response<?> response, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        Status status = response.getStatus();
            jsonGenerator.writeStringField("status", status.toString());
        switch (status) {
            case Error:
                jsonGenerator.writeStringField("message", response.getMessage());
                // Write optional 'code' field if present
                if (Objects.nonNull(response.getCode())) {
                    jsonGenerator.writeNumberField("code", response.getCode());
                }
                // Write optional 'data' field for error if present and not empty
                if (Objects.nonNull(response.getErrorData()) && !response.getErrorData().isEmpty()) {
                    jsonGenerator.writeObjectField("data", response.getErrorData());
                }
                break;
            case Fail:
                // JSend Fail 'data' must be an object
                jsonGenerator.writeObjectFieldStart("data");
                if (Objects.nonNull(response.getMessage())) {
                    // Wrap the general message in an object as {"message": "..."} inside 'data'
                    jsonGenerator.writeStringField("message", response.getMessage());
                } else if (Objects.nonNull(response.getReasons()) && !response.getReasons().isEmpty()) {
                    // Write reasons (field errors) directly into the 'data' object
                    for (Map.Entry<String, String> reason : response.getReasons().entrySet()) {
                        jsonGenerator.writeStringField(reason.getKey(), reason.getValue());
                    }
                }
                // If neither message nor reasons, 'data' will be an empty object {}
                jsonGenerator.writeEndObject();
                break;
            case Success:
                if (response.getData().isEmpty()) {
                    jsonGenerator.writeObjectFieldStart("data"); // Writes "data": {}
                    jsonGenerator.writeEndObject();
                } else {
                    jsonGenerator.writeObjectField("data", response.getData());
                }
                break;
            default:
                break;
        }
        jsonGenerator.writeEndObject();
    }
}
