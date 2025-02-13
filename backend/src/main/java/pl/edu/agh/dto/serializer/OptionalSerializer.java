package pl.edu.agh.dto.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Optional;

public class OptionalSerializer extends JsonSerializer<Optional<?>> {

    @Override
    public void serialize(Optional<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value.isPresent() && value.get() instanceof Exception) {
            gen.writeObject(((Exception) value.get()).getMessage());
        } else if (value.isPresent()) {
            gen.writeObject(value.get());
        }
    }
}
