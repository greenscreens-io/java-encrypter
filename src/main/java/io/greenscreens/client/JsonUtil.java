/*
 * Copyright (C) 2015 - 2022 Green Screens Ltd.
 */
package io.greenscreens.client;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Utility to convert Java class to and from JSOn format. 
 */
class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();

        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).
                      disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES).
                      disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT).
                      disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }


    /**
     * Conversion from JSON string to Java class instance
     * @param <T>
     * @param type
     * @param json
     * @throws IOException
     */
    public static <T> T parse(final Class<T> type, final String json) throws IOException {
        
    	final JsonFactory factory = new JsonFactory();
        final JsonParser jp = factory.createParser(json);
        final JsonNode jn = OBJECT_MAPPER.readTree(jp);

        return OBJECT_MAPPER.treeToValue(jn, type);
    }


    /**
     * Retrieves internal JSON parser engine
     * @return
     */
    public static ObjectMapper getJSONEngine() {
        return OBJECT_MAPPER;
    }

    /**
     * Parse JSON string to Java JsonNode Object
     * @param data
     * @return
     * @throws Exception
     */
	public static JsonNode parse(final String data) throws Exception {
	    return OBJECT_MAPPER.readTree(data);		
	}

	/**
	 * Convert Java object to JSON string
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static String stringify(final Object object) throws Exception {
		return OBJECT_MAPPER.writeValueAsString(object);
	}

}
