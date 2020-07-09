package com.assignment.spring.controller;

import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.dao.WeatherRepository;
import com.assignment.spring.dao.domain.WeatherEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@RestController
@Slf4j
public class WeatherController {
    public static final String PARAM_CITY = "q";
    public static final String PARAM_APPID = "appid";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherRepository weatherDao;

    @Value("${lusthaus.weather.openweather.baseUrl}")
    private String baseUrl;

    @Value("${lusthaus.weather.openweather.appId}")
    private String appId;

    @Value("${lusthaus.weather.openweather.apiKey}")
    private String key;

    @PostConstruct
    public void init(){
        log.info("Using base url {}, application ID {}, key {}", baseUrl, appId, key);
    }


    /**
     * Will proxy the call to the API with a validated city.
     * No retries
     */
    @RequestMapping(path = "/weather", method = RequestMethod.GET)
    public @ResponseBody WeatherEntity weatherInfo(@RequestParam(value="city") String city) throws JsonProcessingException {
        log.trace("weatherInfo for city '{}'", city);
        String cleanedCity = validateCity(city);
        log.debug("Calling API for city '{}'", cleanedCity);

        WeatherResponse weatherResponse;
        String fullUrl = "";

        try {
            fullUrl = createFullUrl(baseUrl, key, cleanedCity);

            ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);

            try {
                weatherResponse = objectMapper.readValue(response.getBody(), WeatherResponse.class);
            } catch (Exception e) {
                log.error(String.format("Exception while converting weather response : '%s'", response.getBody()), e);
                throw e;
            }
        } catch (HttpClientErrorException e){
            log.error(String.format("Exception during the request with url '%s'", fullUrl), e);
            throw e;
        }

        WeatherEntity weatherEntity = saveWeather(weatherResponse);
        return weatherEntity;
    }

    /**
     * Save the weather to the DB with the current date-time, based on the api response
     */
    WeatherEntity saveWeather(WeatherResponse response) {
        WeatherEntity entity = new WeatherEntity(LocalDateTime.now(), response.getName(), response.getSys().getCountry(), response.getMain().getTemp());
        return weatherDao.save(entity);
    }

    /**
     * Must be a reasonable sized city, and be of characters
     */
    String validateCity(String city) {
        String res = city.trim();

        if (res.isEmpty() || res.length()>100) {
            throw new IllegalArgumentException("Incorrect CITY parameter " + city);
        }

        // TODO in version 2 we could validate this with a Pattern..
        // !PARAM_CITY_PATTERN.matcher(city).matches() ...

        return res;
    }


    /**
     * Create the safe url based on base url, app ID and city
     */
    public String createFullUrl(String baseUrl, String apiKey, String city){
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam(PARAM_CITY, city)
                .queryParam(PARAM_APPID, apiKey);//this is the key actually.
        return uriBuilder.toUriString();
    }

}
