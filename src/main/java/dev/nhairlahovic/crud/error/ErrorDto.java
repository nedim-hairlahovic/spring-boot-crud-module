package dev.nhairlahovic.crud.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {

    @With
    private String code;

    private final String message;

    @With
    private Map<String, FieldErrorInfo> fieldErrors;

    @With
    private Map<String, Object> params;

    @With
    private List<Map<String, String>> details;

    public ErrorDto withErrorCodeEnum(Enum<?> enumValue) {
        return this.withCode(enumValue.name());
    }

    public ErrorDto withErrorInfo(ErrorInfo errorInfo) {
        if (errorInfo == null) return this;

        return this
                .withCode(errorInfo.getCode())
                .withFieldErrors(errorInfo.getFieldErrors())
                .withParams(errorInfo.getParams());
    }
}
