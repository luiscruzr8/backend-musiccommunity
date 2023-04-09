package es.tfg.musiccommunity.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public class TagDto {
    private String tagName;

    @JsonCreator
    public TagDto(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return this.tagName;
    }
}
