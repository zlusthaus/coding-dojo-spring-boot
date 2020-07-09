package com.assignment.spring.dao;

import com.assignment.spring.dao.domain.WeatherEntity;
import org.springframework.data.repository.CrudRepository;

public interface WeatherRepository extends CrudRepository<WeatherEntity, Integer> {
}
