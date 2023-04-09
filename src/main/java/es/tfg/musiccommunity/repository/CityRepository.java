package es.tfg.musiccommunity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.City;

@Repository
public interface CityRepository extends JpaRepository <City, Long> {

	public Optional<City> findByName(String name);
	
	List<City> findByOrderByCountryAscNameAsc();

	@Query("SELECT c FROM City c WHERE c.name LIKE %:keyword% OR c.country LIKE %:keyword% " +
		"ORDER BY c.country ASC, c.name ASC")
    public List<City> findByNameLike(@Param("keyword") String keyword);

	static final String CLOSEST_CITY = "SELECT c.* FROM cities c " + 
			"ORDER BY SQRT(POWER(69.1*(c.latitude - :ourLat),2) + POWER(69.1*(:ourLng - c.longitude) * COS(c.latitude/57.3),2)) LIMIT 1";

	@Query(nativeQuery = true, value = CLOSEST_CITY)
  	City findClosestCity(@Param("ourLat") double ourLat, @Param("ourLng") double ourLng);
    
    static final String CLOSER_CITIES = "SELECT c.* FROM cities c WHERE SQRT(" + 
		"POWER(69.1*(c.latitude - :ourLat),2) + POWER(69.1*(:ourLng - c.longitude) * COS(c.latitude/57.3),2)) < 100";

	@Query(nativeQuery = true, value = CLOSER_CITIES)
	List<City> findCloserCities(@Param("ourLat") double ourLat, @Param("ourLng") double ourLng);

}
