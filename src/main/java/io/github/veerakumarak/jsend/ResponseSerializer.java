package io.github.veerakumarak.jsend;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Objects;

public class ResponseSerializer extends JsonSerializer<Response<?>> {

    @Override
    public void serialize(Response response, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        Status status = response.getStatus();
        jsonGenerator.writeStringField("status", status.toString().toLowerCase());
        switch (status) {
            case Error:
                jsonGenerator.writeStringField("message", response.getMessage());
                break;
            case Fail:
                if (Objects.nonNull(response.getMessage())) {
                    jsonGenerator.writeStringField("data", response.getMessage());
                } else {
                    jsonGenerator.writeObjectField("data", response.getReasons());
                }
                break;
            case Success:
                jsonGenerator.writeObjectField("data", response.getData());
                break;
            default:
                break;
        }
        jsonGenerator.writeEndObject();
    }
}
