package com.backend.golvia.app.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Validates if a string is valid JSON.
     *
     * @param json the string to validate
     * @return true if valid JSON, otherwise false
     */
    private static boolean isValidJson(String json) {
        try {
            new ObjectMapper().readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Converts a JSON string into the specified class type.
     *
     * @param jsonString the JSON string to be deserialized
     * @param clazz the class type to which the JSON should be converted
     * @param <T> the type of the class
     * @return the converted object of type T
     * @throws Exception if the conversion fails
     */
    public static <T> T convertJsonToObject(String jsonString, Class<T> clazz) throws Exception {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            throw new Exception("Invalid Payload Please Reference Documentation: " + e.getMessage(), e);
        }
    }

    /**
     * Converts an object into a JSON string.
     *
     * @param object the object to be serialized
     * @param <T> the type of the object
     * @return the JSON string representation of the object
     * @throws Exception if the conversion fails
     */
    public static <T> String convertObjectToJson(T object) throws Exception {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new Exception("Failed to convert object to JSON: " + e.getMessage(), e);
        }
    }
}
