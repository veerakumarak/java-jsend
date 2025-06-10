package io.github.veerakumarak.jsend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;
import java.util.Objects;

@JsonSerialize(using = ResponseSerializer.class)
public class Response<T> {
    private final Status status;
    private Map<String, T> data;
    private Map<String, String> reasons;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    private Response(Status status) {
        this.status = status;
    }

    public static <T> Response<T> success() {
        return new Response<>(Status.Success);
    }
    public static <T> Response<T> success(String key, T data) {
        Objects.requireNonNull(key);
        var response = new Response<T>(Status.Success);
        response.data = Map.of(key, data);
        return response;
    }
    public static <T> Response<T> fail(Map<String, String>reasons) {
        Objects.requireNonNull(reasons);
        var response = new Response<T>(Status.Fail);
        response.reasons = reasons;
        return response;
    }
    public static <T> Response<T> error(String message) {
        Objects.requireNonNull(message);
        var response = new Response<T>(Status.Error);
        response.message = message;
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

    public String getStatus() {
        return this.status.toString();
    }
//    public Data<T> getData() {
//        return this.data;
//    }


    public Map<String, T> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
