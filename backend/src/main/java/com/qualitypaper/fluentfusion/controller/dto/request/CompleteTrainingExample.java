package com.qualitypaper.fluentfusion.controller.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

public record CompleteTrainingExample(Boolean hint,
                                      Boolean skipped,
                                      @JsonDeserialize(using = LocalDateTimeDeserializer.class) LocalDateTime timestamp) {
}