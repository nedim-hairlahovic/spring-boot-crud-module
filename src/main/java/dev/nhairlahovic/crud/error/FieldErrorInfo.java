package dev.nhairlahovic.crud.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldErrorInfo {
    private String code;
    private String message;
    private Object rejectedValue;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> params;

    public static class FieldErrorInfoBuilder {
        public <E extends Enum<E>> FieldErrorInfoBuilder code(E enumValue) {
            this.code = enumValue.name();
            return this;
        }
    }
}
