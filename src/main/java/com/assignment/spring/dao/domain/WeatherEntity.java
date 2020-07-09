package com.assignment.spring.dao.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather")
@Data
@NoArgsConstructor
public class WeatherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime datetime;
    private String city;
    private String country;
    private Double temperature;

    public WeatherEntity(LocalDateTime datetime, String city, String country, Double temperature) {
        this.datetime = datetime;
        this.city = city;
        this.country = country;
        this.temperature = temperature;
    }
}
