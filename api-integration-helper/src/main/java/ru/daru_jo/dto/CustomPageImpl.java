package ru.daru_jo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class CustomPageImpl<T> extends PageImpl<@NonNull T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomPageImpl(@JsonProperty("content") List<T> content,
                          @JsonProperty("number") Integer number,
                          @JsonProperty("size") Integer size,
                          @JsonProperty("totalElements") Long totalElements,
                          @SuppressWarnings("unused")
                          @JsonProperty("pageable") JsonNode pageable,
                          @SuppressWarnings("unused")
                              @JsonProperty("last") Boolean last,
                          @SuppressWarnings("unused")
                              @JsonProperty("totalPages") Integer totalPages,
                          @SuppressWarnings("unused")
                          @JsonProperty("sort") JsonNode sort,
                          @SuppressWarnings("unused")
                              @JsonProperty("numberOfElements") Integer numberOfElements) {
        this(content == null ? new ArrayList<>() : content, PageRequest.of(number == null ? 1 : number, size != null && size >= 1 ? size : 1),
                totalElements != null ? totalElements : 0L);
    }

    public CustomPageImpl(List<T> content, Pageable pageable, Long total) {
        super(content, pageable, total);
    }

    public CustomPageImpl(List<T> content) {
        super(content);
    }

    @SuppressWarnings("unused")
    public CustomPageImpl() {
        this(new ArrayList<>());
    }
}
