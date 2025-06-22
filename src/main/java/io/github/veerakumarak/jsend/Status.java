package io.github.veerakumarak.jsend;

public enum Status {
    Success("success"),
    Fail("fail"),
    Error("error");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
