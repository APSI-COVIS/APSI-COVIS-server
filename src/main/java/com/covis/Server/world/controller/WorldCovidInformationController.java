package com.covis.Server.world.controller;

import com.covis.api.covid.CovidCasesType;
import com.covis.api.geolocation.Point;
import com.covis.api.world.WorldCovidInformationResource;
import com.covis.api.world.dto.CountryCovidInfoDto;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class WorldCovidInformationController implements WorldCovidInformationResource{

    @Override
    public List<CountryCovidInfoDto> listWorldEpidemyInfo(Date date, CovidCasesType type){
        //return sample data
        CountryCovidInfoDto poland = new CountryCovidInfoDto("poland", 15000,
                new Point(20.0, 20.5)),
                germany = new CountryCovidInfoDto("germany", 50000,
                        new Point(15.0, 0.5));

        return Stream.of(poland, germany).collect(Collectors.toList());
    }

}
