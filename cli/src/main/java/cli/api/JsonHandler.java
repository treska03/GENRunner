package cli.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonHandler {
    private final ObjectMapper objectMapper;

    public JsonHandler() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    }

    public String toJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    public <T> T fromJson(InputStream inputStream, Class<T> clazz) throws IOException {
        return objectMapper.readValue(inputStream, clazz);
    }

    public <T> T fromJson(InputStream inputStream, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(inputStream, typeReference);
    }
}