package com.covis.Server.world.controller;

import com.covis.Server.Services.WorldInfoService;
import com.covis.Server.geojson.service.GeoJsonWorldCasesService;
import com.covis.api.covid.CovidCasesType;
import com.covis.api.world.WorldCovidInformationResource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class WorldCovidInformationController implements WorldCovidInformationResource{

    @Autowired
    WorldInfoService worldInfoService;
    /**
     * Metoda zwraca nie kolekcję obiektów Dto, ale GeoJsona jako stringa
     * @param date Data dla której mają zostać zwrócone dane
     * @param type Rodzaj danej (aktywne zarażenia, zgony, zarażenia danego dnia)
     * @return GeoJson z lista punktów do wyswietlenia w mapBoxie
     */
    @Override
    public String listWorldEpidemyInfoAsGeoJson(Date date, CovidCasesType type){
        String geojson = "";

        try{
            geojson = worldInfoService.getDailyWorldInfo(LocalDate.ofInstant(date.toInstant(), ZoneId.of("Europe/Warsaw")), type);
        }catch (Exception e){
            e.printStackTrace();
        }

        return geojson;
    }

}
