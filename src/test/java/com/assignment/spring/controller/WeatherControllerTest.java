package com.assignment.spring.controller;

import com.assignment.spring.api.Main;
import com.assignment.spring.api.Sys;
import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.dao.WeatherRepository;
import com.assignment.spring.dao.domain.WeatherEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeatherControllerTest {

    @Autowired
    private WeatherRepository weatherDao;

    @Autowired
    private WeatherController sut;

    @Test
    public void testValidateCity_Correct(){
        Assert.assertEquals("city 1", sut.validateCity("city 1"));
        Assert.assertEquals("city 2", sut.validateCity("city 2 "));
        Assert.assertEquals("city  3", sut.validateCity(" city  3 "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateCity_excOnShort(){
        sut.validateCity("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateCity_excOnLong(){

        //this would be longer than 100 characters.
        String city = IntStream.range(0, 60).mapToObj(Integer::toString).collect(Collectors.joining());
        sut.validateCity(city);
    }


    @Test
    public void testUrlCreated(){
        String url = sut.createFullUrl("http://localhost:8080", "my-key", " The nices tCity in the worlsds\n delete database");
        Assert.assertEquals("http://localhost:8080?q=%20The%20nices%20tCity%20in%20the%20worlsds%0A%20delete%20database&appid=my-key", url);
    }


    @Test
    public void testSaveWeather(){
        WeatherResponse wResponse = new WeatherResponse();
        wResponse.setName("Amsterdam");
        wResponse.setSys(new Sys());
        wResponse.getSys().setCountry("Netherlands");
        wResponse.setMain(new Main());
        wResponse.getMain().setTemp(7.123);
        WeatherEntity we = sut.saveWeather(wResponse);
        WeatherEntity weFromDb = weatherDao.findById(we.getId()).get();
        Assert.assertEquals("Netherlands", weFromDb.getCountry());
        Assert.assertEquals("Amsterdam", weFromDb.getCity());
        Assert.assertEquals(7.123, weFromDb.getTemperature(), 0.00001d);
        Assert.assertNotNull(weFromDb.getDatetime());
    }

}
