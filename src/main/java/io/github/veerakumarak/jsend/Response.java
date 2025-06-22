package io.github.veerakumarak.jsend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonSerialize(using = ResponseSerializer.class)
public class Response<T> {
    private final Status status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> reasons;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, T> data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer code; // For Error status (optional error code)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> errorData; // For Error status (optional arbitrary data)

    private Response(Status status) {
        this.status = status;
        this.reasons = null;
        this.data = null;
        this.message = null;
        this.code = null;
        this.errorData = null;
    }

    /**
     * Creates a JSend success response with an empty data object: {"status": "success", "data": {}}
     */
    public static <T> Response<T> success() {
        var response = new Response<T>(Status.Success);
        response.data = Collections.emptyMap(); // JSend requires "data": {} for empty success
        return response;
    }

    /**
     * Creates a JSend success response with a single key-value pair in data: {"status": "success", "data": {"key": value}}
     */
    public static <T> Response<T> success(String key, T value) {
        Objects.requireNonNull(key, "Data key cannot be null for success response.");
        var response = new Response<T>(Status.Success);
        response.data = Collections.singletonMap(key, value);
        return response;
    }

    /**
     * Creates a JSend success response with a custom data map: {"status": "success", "data": {...}}
     */
    public static <T> Response<T> success(Map<String, T> data) {
        Objects.requireNonNull(data, "Data map cannot be null for success response.");
        var response = new Response<T>(Status.Success);
        response.data = Map.copyOf(data);
        return response;
    }

    /**
     * Creates a JSend fail response with a general message: {"status": "fail", "data": {"message": "..."}}
     * The message will be wrapped in a 'data' object by the serializer.
     */
    public static <T> Response<T> fail(String message) {
        Objects.requireNonNull(message, "Fail message cannot be null."); // Added null check
        var response = new Response<T>(Status.Fail);
        response.message = message;
        return response;
    }

    /**
     * Creates a JSend fail response with a single key-value reason: {"status": "fail", "data": {"field": "reason"}}
     */
    public static <T> Response<T> fail(String key, String message) {
        return fail(Map.of(key, message));
    }

    /**
     * Creates a JSend fail response with a map of reasons (e.g., field validation errors): {"status": "fail", "data": {...}}
     */
    public static <T> Response<T> fail(Map<String, String> reasons) {
        Objects.requireNonNull(reasons, "Reasons map cannot be null for fail response.");
        var response = new Response<T>(Status.Fail);
        response.reasons = Map.copyOf(reasons); // Ensure immutability
        return response;
    }

    /**
     * Creates a JSend error response with a mandatory message: {"status": "error", "message": "..."}
     */
    public static <T> Response<T> error(String message) {
        Objects.requireNonNull(message, "Error message cannot be null.");
        var response = new Response<T>(Status.Error);
        response.message = message;
        return response;
    }

    /**
     * Creates a JSend error response with a message and an optional error code: {"status": "error", "message": "...", "code": ...}
     */
    public static <T> Response<T> error(String message, Integer code) {
        Objects.requireNonNull(message, "Error message cannot be null.");
        var response = new Response<T>(Status.Error);
        response.message = message;
        response.code = code;
        return response;
    }

    /**
     * Creates a JSend error response with a message, optional code, and additional arbitrary data: {"status": "error", "message": "...", "code": ..., "data": {...}}
     */
    public static <T> Response<T> error(String message, Integer code, Map<String, Object> errorData) {
        Objects.requireNonNull(message, "Error message cannot be null.");
        var response = new Response<T>(Status.Error);
        response.message = message;
        response.code = code;
        if (Objects.nonNull(errorData)) {
            response.errorData = Collections.unmodifiableMap(new HashMap<>(errorData)); // Ensure immutability
        }
        return response;
    }

    public boolean isSuccess() {
        return this.status == Status.Success;
    }

    public boolean isFail() {
        return this.status == Status.Fail;
    }

    public boolean isError() {
        return this.status == Status.Error;
    }

    public Status getStatus() {
        return this.status;
    }

    public Map<String, T> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getReasons() {
        return reasons != null ? reasons : Map.of();
    }

    public Integer getCode() {
        return code;
    }

    public Map<String, Object> getErrorData() {
        return errorData;
    }

}
