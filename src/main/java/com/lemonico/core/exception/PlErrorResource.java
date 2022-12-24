package com.lemonico.core.exception;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlErrorResource
{
    private final String code;
    private final String message;
}
