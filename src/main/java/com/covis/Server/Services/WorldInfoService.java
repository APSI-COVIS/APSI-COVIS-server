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
                    int i =1;
                    Optional<DatabaseRecord> before;
                    before = mainRepository.findOneByCountryNameAndProvinceAndDate(elem.getCountryName(), elem.getProvince(),date.minusDays(i));
                    Integer valueBefore = before.isPresent() ? (Optional.ofNullable(before.get().getConfirmed()).isPresent() ? before.get().getConfirmed() : 0) : 0;
                    Integer value = Optional.ofNullable(elem.getConfirmed()).orElse(valueBefore);
                    return geoJsonWorldCasesService.createCountryCovidPoint(elem.getCountryName(),value - valueBefore,elem.getLongitude(),elem.getLatitude());
                }).collect(Collectors.toList()));
                break;
            case ACTIVE:
                covidCasesList = geoJsonWorldCasesService.createCovidCasesList(records.stream().map((elem) -> {
                    Integer confirmed = Optional.ofNullable(elem.getConfirmed()).orElse(0);
                    Integer deaths = Optional.ofNullable(elem.getDeaths()).orElse(0);
                    Integer recovered = Optional.ofNullable(elem.getRecovered()).orElse(0);
                    Integer value = confirmed - deaths - recovered;
                    return geoJsonWorldCasesService.createCountryCovidPoint(elem.getCountryName(),value,elem.getLongitude(),elem.getLatitude());
                }).collect(Collectors.toList()));
                break;
        }


        return geoJsonWorldCasesService.returnAsGeoJson(covidCasesList);

    }

}
