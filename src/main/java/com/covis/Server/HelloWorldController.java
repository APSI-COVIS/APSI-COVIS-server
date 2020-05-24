package com.covis.Server;


import com.covis.Server.DAO.MainRepository;
import com.covis.Server.Entities.DatabaseRecord;
import com.covis.Server.country.controller.CountryCovidInformationController;
import com.covis.Server.world.controller.WorldCovidInformationController;
import com.covis.api.CovidCaseDto;
import com.covis.api.HelloWorldResource;
import com.covis.api.country.dto.CovidDailyCasesDto;
import com.covis.api.covid.CovidCasesType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
public class HelloWorldController implements HelloWorldResource{

    @Autowired
    CountryCovidInformationController countryCovidInformationController;

    @Autowired
    WorldCovidInformationController worldCovidInformationController;

    @Autowired



    public HelloWorldController(){
        //empty public contructor
    }
    @Autowired
    private MainRepository mainRepository;
    @GetMapping("/helloworld")
    public String helloWorldEndPoint(){
        List<CovidDailyCasesDto> list;
        List<CovidDailyCasesDto> list2;
        List<CovidDailyCasesDto> list3;
        List<CovidDailyCasesDto> list4;

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            list = countryCovidInformationController.listEpidemyDataInCountry(format.parse("2020-05-11"), format.parse("2020-05-16"), "Poland", CovidCasesType.ACTIVE);
            list2 = countryCovidInformationController.listEpidemyDataInCountry(format.parse("2020-05-09"), format.parse("2020-05-16"), "Poland", CovidCasesType.RECOVERED);
            list3 = countryCovidInformationController.listEpidemyDataInCountry(format.parse("2020-04-11"), format.parse("2020-05-16"), "Vietnam", CovidCasesType.DEATH);
            list4 = countryCovidInformationController.listEpidemyDataInCountry(format.parse("2020-04-20"), format.parse("2020-05-16"), "Poland", CovidCasesType.NEW);
            worldCovidInformationController.listWorldEpidemyInfoAsGeoJson(format.parse("2020-04-24"), CovidCasesType.RECOVERED);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Hello Covis";
    }

}
