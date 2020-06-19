package com.covis.Server.Entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="population", schema="public")
public class CountryPopulationInfo {
    @Column(name="country")
    private String countryName;
    @Column(name="population")
    private Integer population;
    @Id
    @Column(name="country_code")
    private String countrySlug;



    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountrySlug() {
        return countrySlug;
    }

    public void setCountrySlug(String countrySlug) {
        this.countrySlug = countrySlug;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }
}
