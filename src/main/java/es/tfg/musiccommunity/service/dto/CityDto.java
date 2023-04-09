package es.tfg.musiccommunity.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public class CityDto {
    private Long id;
    private String cityName;
    private String countryName;

    @JsonCreator
    public CityDto(Long id, String cityName, String countryName) {
        this.id = id;
        this.cityName = cityName;
        this.countryName = countryName;
    }

    public Long getId() {
        return this.id;
    }

    public String getCityName() {
        return this.cityName;
    }

    public String getCountryName() {
        return this.countryName;
    }
}
