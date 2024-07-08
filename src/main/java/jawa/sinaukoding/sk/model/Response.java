package jawa.sinaukoding.sk.model;

public record Response<T>(String code, String message, T data) {

    public static <T> Response<T> create(String serviceCode, String responseCode, String message, T data) {
        return new Response<>(serviceCode + responseCode, message, data);
    }

    public static Response<Object> unauthenticated() {
        return new Response<>("0101", "unauthenticated", null);
    }

    public static Response<Object> unauthorized() {
        return new Response<>("0201", "unauthorized", null);
    }


    public static <T> Response<T> badRequest() {
        return new Response<>("0301", "bad request", null);
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>("0000", "success", data);
    }
}
