package com.covis.Server.Entities;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(name="covid", schema = "public")
public class DatabaseRecord {
    @Id
    @Column(name="index")
    private int id;
    @Column(name = "Country/Region")
    private String countryName;
    @Column(name = "Province/State")
    private String province;
    @Column(name = "Date")
    private LocalDate date;
    @Column(name = "Lat")
    private double latitude;
    @Column(name = "Long")
    private double longitude;
    @Column(name = "Confirmed")
    private Integer confirmed;
    @Column(name = "Recovered")
    private Integer recovered;
    @Column(name = "Deaths")
    private Integer deaths;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Integer getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Integer confirmed) {
        this.confirmed = Optional.ofNullable(confirmed).orElse(0);
    }

    public Integer getRecovered() {
        return recovered;
    }

    public void setRecovered(Integer recovered) {
        this.recovered = Optional.ofNullable(recovered).orElse(0);
    }

    public Integer getDeaths() {
        return deaths;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = Optional.ofNullable(deaths).orElse(0);
    }
}
