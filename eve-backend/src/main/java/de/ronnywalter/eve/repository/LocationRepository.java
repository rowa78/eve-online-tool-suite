package de.ronnywalter.eve.repository;

import de.ronnywalter.eve.model.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;


public interface LocationRepository extends CrudRepository<Location, Long> {

    @Query("select s.id from #{#entityName} s where s.locationType = ?1")
    Set<Long> getStructureIds(String type);

    List<Location> findByLocationType(String type);

    List<Location> findByLocationTypeAndAccessForbidden(String type, boolean b);

    List<Location> findByRegionIdAndLocationTypeAndAccessForbiddenAndHasMarket(Integer id, String locationType, boolean b, boolean b1);
}
