package com.covis.Server.Services;


import com.covis.Server.DAO.MainRepository;
import com.covis.Server.Entities.DatabaseRecord;
import com.covis.Server.geojson.service.GeoJsonWorldCasesService;
import com.covis.api.country.dto.CovidDailyCasesDto;
import com.covis.api.covid.CovidCasesType;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorldInfoService {

    @Autowired
    MainRepository mainRepository;

    @Autowired
    private GeoJsonWorldCasesService geoJsonWorldCasesService;


    public String getDailyWorldInfo(LocalDate date, CovidCasesType type) throws IOException{

        List<DatabaseRecord> records = mainRepository.findAllByDate(date);
        SimpleFeatureCollection covidCasesList = null;
        switch (type){
            case DEATH:
                covidCasesList = geoJsonWorldCasesService.createCovidCasesList(records.stream().map(
                        (elem) -> {
                            Optional<Integer> value = Optional.ofNullable(elem.getDeaths());
                            return geoJsonWorldCasesService.createCountryCovidPoint(elem.getCountryName(),value.orElse(-1),elem.getLongitude(),elem.getLatitude());
                        }
                ).collect(Collectors.toList()));
                break;
            case RECOVERED:
                covidCasesList = geoJsonWorldCasesService.createCovidCasesList(records.stream().map((elem) -> {
                    Optional<Integer> value = Optional.ofNullable(elem.getRecovered());
                    return geoJsonWorldCasesService.createCountryCovidPoint(elem.getCountryName(),value.orElse(-1),elem.getLongitude(),elem.getLatitude());
                }).collect(Collectors.toList()));
                break;
            case NEW:
                covidCasesList = geoJsonWorldCasesService.createCovidCasesList(records.stream().map((elem) -> {
                    Optional<Integer> value = Optional.ofNullable(elem.getConfirmed());
                    Integer valueYesterday = mainRepository.findOneByCountryNameAndDate(elem.getCountryName(), date.minusDays(1)).get().getConfirmed();
                    return geoJsonWorldCasesService.createCountryCovidPoint(elem.getCountryName(),value.get() - valueYesterday,elem.getLongitude(),elem.getLatitude());
                }).collect(Collectors.toList()));
                break;
            case ACTIVE:
                covidCasesList = geoJsonWorldCasesService.createCovidCasesList(records.stream().map((elem) -> {
                    Optional<Integer> confirmed = Optional.ofNullable(elem.getConfirmed());
                    Optional<Integer> deaths = Optional.ofNullable(elem.getDeaths());
                    Optional<Integer> recovered = Optional.ofNullable(elem.getRecovered());
                    Integer value = confirmed.orElse(-1) - deaths.orElse(-1) - recovered.orElse(-1);
                    return geoJsonWorldCasesService.createCountryCovidPoint(elem.getCountryName(),value,elem.getLongitude(),elem.getLatitude());
                }).collect(Collectors.toList()));
                break;
        }


        return geoJsonWorldCasesService.returnAsGeoJson(covidCasesList);

    }

}
