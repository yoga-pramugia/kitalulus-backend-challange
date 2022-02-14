package com.kitalulus.challenge.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "COUNTRY_RATE")
public class CountryRate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "weather_id_seq_gen")
    @SequenceGenerator(name = "weather_id_seq_gen", sequenceName = "weather_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Version
    private long version;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "rate")
    private Double rate;

    @PrePersist
    public void prePersist() {
        this.setCreatedDate(LocalDateTime.now());
    }

    @PostPersist
    public void preUpdate() {
        this.setLastModifiedDate(LocalDateTime.now());
    }
}
