package com.covis.Server.geojson.service;

import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.GeometryBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.locationtech.jts.geom.Point;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

import org.springframework.stereotype.Service;

@Service
public class GeoJsonWorldCasesService {

    private static final String WORLD_CASES = "WORLD_CASES";
    private static final SimpleFeatureType WORLD_CASE_TYPE;

    static {
        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
        b.setName( WORLD_CASES );
        b.add("cases", Integer.class);
        b.add("country-slug", String.class);
        b.add("geometry", Point.class);
        WORLD_CASE_TYPE = b.buildFeatureType();
    }

    public SimpleFeature createCountryCovidPoint(String countrySlug, int cases, double longitude, double latitude){
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(WORLD_CASE_TYPE);
        featureBuilder.add(cases);
        featureBuilder.add(countrySlug);

        GeometryBuilder pointBuilder = new GeometryBuilder();
        Point p = pointBuilder.point(longitude, latitude);
        featureBuilder.add(p);

        return featureBuilder.buildFeature( WORLD_CASES );
    }

    public SimpleFeatureCollection createCovidCasesList(Collection<? extends SimpleFeature> list){
        ListFeatureCollection collection = new ListFeatureCollection(WORLD_CASE_TYPE);
        collection.addAll(list);
        return collection;
    }

    public String returnAsGeoJson(SimpleFeatureCollection collection) throws IOException{
        StringWriter stringWriter = new StringWriter();
        FeatureJSON jsonWriter = new FeatureJSON();
        jsonWriter.writeFeatureCollection(collection, stringWriter);

        return stringWriter.toString();
    }


}
