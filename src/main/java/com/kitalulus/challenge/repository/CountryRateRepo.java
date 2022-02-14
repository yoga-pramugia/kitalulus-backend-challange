package com.kitalulus.challenge.repository;

import com.kitalulus.challenge.entity.CountryRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRateRepo extends JpaRepository<CountryRate, Long> {
}
