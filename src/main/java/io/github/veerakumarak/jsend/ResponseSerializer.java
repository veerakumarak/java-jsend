package io.github.veerakumarak.jsend;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ResponseSerializer extends JsonSerializer<Response<?>> {
    @Override
    public void serialize(Response response, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("status", response.getStatus().toLowerCase());
        if (!response.isError()) {
            jsonGenerator.writeObjectField("data", response.getData());
        } else {
            jsonGenerator.writeStringField("error", response.getMessage());
        }

        jsonGenerator.writeEndObject();
    }
}
