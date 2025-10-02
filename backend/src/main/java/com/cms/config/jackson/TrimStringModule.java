package com.cms.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;

/** Module that trims leading/trailing whitespace from incoming JSON strings. */
public class TrimStringModule extends SimpleModule {

  public TrimStringModule() {
    addDeserializer(
        String.class,
        new StdScalarDeserializer<>(String.class) {
          @Override
          public String deserialize(JsonParser parser, DeserializationContext ctxt)
              throws IOException {
            String value = parser.getValueAsString();
            return value != null ? value.trim() : null;
          }

          @Override
          public String getNullValue(DeserializationContext ctxt) {
            return null;
          }
        });
  }
}
