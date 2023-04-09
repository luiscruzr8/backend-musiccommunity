package es.tfg.musiccommunity;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.tfg.musiccommunity.model.City;

import es.tfg.musiccommunity.repository.CityRepository;
import es.tfg.musiccommunity.service.CityService;
import es.tfg.musiccommunity.service.dto.CityDto;
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class CityServiceTest {

    @Autowired
    private CityRepository cityRepository;
    
    @Autowired
    private CityService cityService;

    private static final String SANTIAGO = "Santiago de compostela";
    private static final double LATITUDE_SANTIAGO = 42.880241;
    private static final double LONGITUDE_SANTIAGO = -8.5473632;
    private static final String CORUNA = "A Coruña";
    private static final double LATITUDE_CORUNA = 43.3712591;
    private static final double LONGITUDE_CORUNA = -8.418801;
    private static final String ESPANA ="España";
    private static final String BILBAO = "Bilbao";
    private static final double LATITUDE_BILBAO = 43.2603479;
    private static final double LONGITUDE_BILBAO = -2.9334110;
    private static final double LATITUDE_ORDES = 43.0879655;
    private static final double LONGITUDE_ORDES = -8.3973677;

    @Test
    public void getZeroCitiesTest() {
        ResponseEntity<List<CityDto>> zeroCities = cityService.getAllCities("");
        Assert.assertEquals(HttpStatus.OK,zeroCities.getStatusCode());
        Assert.assertEquals(0,zeroCities.getBody().size());

        ResponseEntity<List<CityDto>> zeroCities2 = cityService.getAllCities("abc");
        Assert.assertEquals(HttpStatus.OK,zeroCities2.getStatusCode());
        Assert.assertEquals(0,zeroCities2.getBody().size());
    }

    @Test
    public void getCitiesTest(){
        City bb = new City(BILBAO, ESPANA, LATITUDE_BILBAO, LONGITUDE_BILBAO);
        cityRepository.save(bb);
        City sdc = new City(SANTIAGO, ESPANA, LATITUDE_SANTIAGO, LONGITUDE_SANTIAGO);
        cityRepository.save(sdc);
        City lcr = new City(CORUNA, ESPANA, LATITUDE_CORUNA, LONGITUDE_CORUNA);
        cityRepository.save(lcr);

        ResponseEntity<List<CityDto>> cities = cityService.getAllCities("");
        Assert.assertEquals(HttpStatus.OK,cities.getStatusCode());
        Assert.assertEquals(3,cities.getBody().size());
        Assert.assertEquals(lcr.getName(),cities.getBody().get(0).getCityName());
        Assert.assertEquals(bb.getName(),cities.getBody().get(1).getCityName());
        Assert.assertEquals(sdc.getName(),cities.getBody().get(2).getCityName());
    }

    @Test
    public void getClosestCityTest() {
        City lcr = new City(CORUNA, ESPANA, LATITUDE_CORUNA, LONGITUDE_CORUNA);
        cityRepository.save(lcr);
        City sdc = new City(SANTIAGO, ESPANA, LATITUDE_SANTIAGO, LONGITUDE_SANTIAGO);
        cityRepository.save(sdc);
        /* ORDES ESTÁ MÁS CERCA DE SANTIAGO QUE DE CORUÑA, ASIQUE: */
        ResponseEntity<CityDto> city = cityService.getClosestCity(LATITUDE_ORDES, LONGITUDE_ORDES);
        Assert.assertEquals(HttpStatus.OK,city.getStatusCode());
        Assert.assertEquals(sdc.getName(),city.getBody().getCityName());
    }

}