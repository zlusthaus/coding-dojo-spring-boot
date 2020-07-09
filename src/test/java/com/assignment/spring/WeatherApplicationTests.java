package com.assignment.spring;

import com.assignment.spring.controller.WeatherController;
import com.assignment.spring.dao.WeatherRepository;
import com.assignment.spring.dao.domain.WeatherEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeatherApplicationTests {

    @Autowired
    private WeatherRepository weatherDao;

    @Autowired
    private WeatherController controller;

	@Test
	public void testContextLoads() {
        System.out.println("context loaded properly");
	}

	@Test
    public void testDbAccessible(){

        WeatherEntity toSave = new WeatherEntity(LocalDateTime.now(), "a", "b", 1.23);
	    WeatherEntity we = weatherDao.save(toSave);

	    WeatherEntity queried = weatherDao.findById(we.getId()).get();

	    Assert.assertEquals(queried, we);
        toSave.setId(queried.getId());

        Assert.assertEquals(queried, toSave);
    }
}
