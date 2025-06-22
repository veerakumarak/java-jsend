
# Java JSend Response Library

A lightweight Java library designed to create standardized API responses following the [JSend specification](https://github.com/omniti-labs/jsend). This library provides a fluent API for constructing `success`, `fail`, and `error` responses, ensuring consistent JSON output for your APIs.

## Table of Contents

* [Features](https://www.google.com/search?q=%23features)
* [Core Concepts (JSend)](https://www.google.com/search?q=%23core-concepts-jsend)
* [Installation](https://www.google.com/search?q=%23installation)
* [Usage](https://www.google.com/search?q=%23usage)
    * [Success Responses](https://www.google.com/search?q=%23success-responses)
    * [Fail Responses](https://www.google.com/search?q=%23fail-responses)
    * [Error Responses](https://www.google.com/search?q=%23error-responses)
    * [Checking Response Status](https://www.google.com/search?q=%23checking-response-status)
    * [Retrieving Data/Messages/Reasons](https://www.google.com/search?q=%23retrieving-datamessagesreasons)
* [JSend Compliance Details](https://www.google.com/search?q=%23jsend-compliance-details)
* [Dependencies](https://www.google.com/search?q=%23dependencies)
* [Contributing](https://www.google.com/search?q=%23contributing)
* [License](https://www.google.com/search?q=%23license)

## Features

* **JSend Specification Adherence:** Strictly follows the JSend specification for `success`, `fail`, and `error` statuses.
* **Fluent API:** Easy-to-use static factory methods (`Response.success()`, `Response.fail()`, `Response.error()`) for building responses.
* **Type-Safe Success Data:** Generic `Response<T>` ensures compile-time type safety for your success payloads.
* **Detailed Error Reporting:** Supports mapping validation failures (`fail` status) to field-specific reasons or general messages.
* **Comprehensive Error Handling:** Provides options for including error codes and arbitrary error data for `error` status responses.
* **Clean JSON Output:** Utilizes Jackson for serialization, ensuring only relevant fields are included and nulls are omitted.

## Core Concepts (JSend)

JSend is a simple JSON format for API responses, defining three primary statuses:

* **`success`**: All went well, and `data` contains the requested information.
  ```json
  { "status": "success", "data": { ... } }
  ```
* **`fail`**: There was an issue with the provided data (e.g., validation errors). `data` contains details about the failure.
  ```json
  { "status": "fail", "data": { ... } }
  ```
* **`error`**: An unexpected server-side error occurred. `message` is mandatory. `code` and `data` are optional.
  ```json
  { "status": "error", "message": "...", "code": ..., "data": { ... } }
  ```

This library maps these concepts directly:

* `io.github.veerakumarak.jsend.Status` enum: `Success`, `Fail`, `Error`.
* `io.github.veerakumarak.jsend.Response<T>` class: The core class for building responses.
* `io.github.veerakumarak.jsend.ResponseSerializer`: Custom Jackson serializer to ensure strict JSend output formatting.

## Installation

This library is published to Maven Central. You can add it as a dependency in your project's `pom.xml` (Maven) or `build.gradle` (Gradle) file.

**Maven:**

```xml
<dependency>
    <groupId>io.github.veerakumarak</groupId>
    <artifactId>jsend</artifactId>
    <version>3.0.0</version>
</dependency>
```

**Gradle:**

```gradle
implementation 'io.github.veerakumarak:jsend:3.0.0'
```

## Usage

Here are examples demonstrating how to create different types of JSend responses using the `Response` class.

To convert `Response` objects to JSON, you'll need a Jackson `ObjectMapper`:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.veerakumarak.jsend.Response;
import java.util.Map;

public class JSendExample {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        // Example usage will go here
    }

    private static void printResponseJson(String label, Response<?> response) throws Exception {
        System.out.println("--- " + label + " ---");
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
        System.out.println();
    }
}
```

### Success Responses

```java
// 1. Empty Success Response
Response<Void> emptySuccess = Response.success();
printResponseJson("Empty Success", emptySuccess);
// Expected JSON: {"status": "success", "data": {}}

// 2. Success with Single Data Entry
Response<String> singleDataSuccess = Response.success("message", "Operation completed successfully!");
printResponseJson("Single Data Success", singleDataSuccess);
// Expected JSON: {"status": "success", "data": {"message": "Operation completed successfully!"}}

// 3. Success with Multiple Data Entries
Map<String, Object> userData = Map.of(
    "id", 123,
    "name", "Alice Wonderland",
    "email", "alice@example.com"
);
Response<Object> complexDataSuccess = Response.success(userData);
printResponseJson("Complex Data Success", complexDataSuccess);
/* Expected JSON:
{
  "status": "success",
  "data": {
    "id": 123,
    "name": "Alice Wonderland",
    "email": "alice@example.com"
  }
}
*/
```

### Fail Responses

Used for client-side errors, typically validation failures. The `data` field describes the failure.

```java
// 1. Fail with a General Message
Response<Void> generalFail = Response.fail("Invalid input provided.");
printResponseJson("General Fail", generalFail);
/* Expected JSON:
{
  "status": "fail",
  "data": {
    "message": "Invalid input provided."
  }
}
*/

// 2. Fail with Field-Specific Reasons (Validation Errors)
Response<Void> fieldFail = Response.fail(
    Map.of(
        "username", "Username must be at least 5 characters.",
        "email", "Invalid email format."
    )
);
printResponseJson("Field-Specific Fail", fieldFail);
/* Expected JSON:
{
  "status": "fail",
  "data": {
    "username": "Username must be at least 5 characters.",
    "email": "Invalid email format."
  }
}
*/
```

### Error Responses

Used for server-side errors. Always includes a `message`. Can optionally include `code` and `data`.

```java
// 1. Basic Error Response
Response<Void> basicError = Response.error("Internal Server Error occurred.");
printResponseJson("Basic Error", basicError);
/* Expected JSON:
{
  "status": "error",
  "message": "Internal Server Error occurred."
}
*/

// 2. Error with Code
Response<Void> errorWithCode = Response.error("Database connection lost.", 5001);
printResponseJson("Error with Code", errorWithCode);
/* Expected JSON:
{
  "status": "error",
  "message": "Database connection lost.",
  "code": 5001
}
*/

// 3. Error with Code and Additional Data
Map<String, Object> errorDetails = Map.of(
    "traceId", "abc-123",
    "component", "auth-service"
);
Response<Void> errorWithCodeAndData = Response.error("Failed to process request.", 4002, errorDetails);
printResponseJson("Error with Code and Data", errorWithCodeAndData);
/* Expected JSON:
{
  "status": "error",
  "message": "Failed to process request.",
  "code": 4002,
  "data": {
    "traceId": "abc-123",
    "component": "auth-service"
  }
}
*/
```

### Checking Response Status

You can easily check the status of a `Response` object:

```java
Response<String> successResponse = Response.success("status", "OK");
Response<Void> failResponse = Response.fail("Validation failed");
Response<Void> errorResponse = Response.error("Server exception");

System.out.println("Is successResponse successful? " + successResponse.isSuccess()); // true
System.out.println("Is failResponse a failure? " + failResponse.isFail());         // true
System.out.println("Is errorResponse an error? " + errorResponse.isError());       // true
```

### Retrieving Data/Messages/Reasons

Access the relevant fields based on the response type:

```java
Response<String> successResult = Response.success("data", "User profile loaded");
if (successResult.isSuccess()) {
    // For single key success, or iterate if it's a map
    System.out.println("Success Data: " + successResult.getData()); // {data=User profile loaded}
}

Response<Void> failResult = Response.fail(Map.of("field", "Value is too short"));
if (failResult.isFail()) {
    // getReasons() returns an empty map if fail was created with a general message
    System.out.println("Fail Reasons: " + failResult.getReasons()); // {field=Value is too short}
    // For general message fail, use getMessage()
    Response<Void> generalFailResult = Response.fail("Invalid request");
    System.out.println("General Fail Message: " + generalFailResult.getMessage()); // Invalid request
}

Response<Void> errorResult = Response.error("Critical error", 999);
if (errorResult.isError()) {
    System.out.println("Error Message: " + errorResult.getMessage()); // Critical error
    System.out.println("Error Code: " + errorResult.getCode());       // 999
    System.out.println("Error Data: " + errorResult.getErrorData());   // {} (or actual map if present)
}
```

## JSend Compliance Details

This library is designed for strict JSend compliance:

* **`status` Field:** Always output as a lowercase string (`"success"`, `"fail"`, `"error"`).
* **`success` Responses:**
    * If no data is provided (`Response.success()`), `data` is serialized as an empty JSON object: `{"data": {}}`.
    * If data is provided (`Response.success(key, value)` or `Response.success(Map)`), `data` is serialized as a JSON object containing the provided payload.
* **`fail` Responses:**
    * The `data` field is always a JSON object.
    * If created with a general message (`Response.fail(String message)`), the message is wrapped as `{"data": {"message": "..."}}`.
    * If created with specific reasons (`Response.fail(Map reasons)`), the reasons map is directly serialized into the `data` object: `{"data": {"field1": "reason1", ...}}`.
* **`error` Responses:**
    * Always includes a top-level `message` string.
    * `code` (Integer) is included if provided.
    * `data` (arbitrary JSON object) is included if provided.

## Dependencies

This library has a direct dependency on **Jackson Databind** for JSON serialization. When you include `io.github.veerakumarak:jsend:3.0.0` in your build, Jackson Databind will be automatically pulled in as a transitive dependency.

## Contributing

Contributions are welcome\! If you have suggestions for improvements, new features, or bug fixes, please feel free to open an issue or submit a pull request.

## License

This library is released under the MIT License. See the `LICENSE` file for more details.