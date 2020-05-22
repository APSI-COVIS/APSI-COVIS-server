package com.covis.Server.world.controller;

import com.covis.Server.geojson.service.GeoJsonWorldCasesService;
import com.covis.api.covid.CovidCasesType;
import com.covis.api.world.WorldCovidInformationResource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class WorldCovidInformationController implements WorldCovidInformationResource{

    @Autowired
    private GeoJsonWorldCasesService geoJsonWorldCasesService;
    /**
     * Metoda zwraca nie kolekcję obiektów Dto, ale GeoJsona jako stringa
     * @param date Data dla której mają zostać zwrócone dane
     * @param type Rodzaj danej (aktywne zarażenia, zgony, zarażenia danego dnia)
     * @return GeoJson z lista punktów do wyswietlenia w mapBoxie
     */
    @Override
    public String listWorldEpidemyInfoAsGeoJson(Date date, CovidCasesType type){
        //sample data
        SimpleFeature f1 = geoJsonWorldCasesService.createCountryCovidPoint("poland", 120, 12.5, -12.5),
                f2 = geoJsonWorldCasesService.createCountryCovidPoint("germany", 1200, -40.5, 30.5);

        String geojson = "";

        try{
            SimpleFeatureCollection covidCasesList = geoJsonWorldCasesService.createCovidCasesList(
                    Stream.of(f1,f2).collect(Collectors.toList()));
            geojson = geoJsonWorldCasesService.returnAsGeoJson(covidCasesList);
        }catch (Exception e){

        }

        return geojson;
    }

}
