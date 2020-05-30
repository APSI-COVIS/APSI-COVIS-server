package com.covis.Server.DAO;

import com.covis.Server.Entities.CountryPopulationInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PopulationRepository extends CrudRepository<CountryPopulationInfo, String> {

    Optional<CountryPopulationInfo> findOneByCountrySlug(String countryName);

}
