package de.ronnywalter.eve.service;

import com.google.common.collect.Lists;
import de.ronnywalter.eve.model.*;
import de.ronnywalter.eve.repository.ConstellationRepository;
import de.ronnywalter.eve.repository.LocationRepository;
import de.ronnywalter.eve.repository.RegionRepository;
import de.ronnywalter.eve.repository.SystemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UniverseService {

    private final RegionRepository regionRepository;
    private final ConstellationRepository constellationRepository;
    private final SystemRepository systemRepository;
    private final LocationRepository locationRepository;

    //@Cacheable("locations")
    public Location getLocation(Number id) {
        Location l = locationRepository.findById(id.longValue()).orElse(null);

        return l;
    }

    public Location getStructure(long locationId) {
        return locationRepository.findById(locationId).orElse(null);
    }

    public Region getRegion(Integer regionId) {
        return regionRepository.findById(regionId).orElse(null);
    }

    public Region getRegion(String name) {
        return regionRepository.findByName(name).orElse(null);
    }


    public List<Region> getRegions() {
        return Lists.newArrayList(regionRepository.findAll());
    }


    //@Cacheable("systems")
    public SolarSystem getSolarSystem(Integer solarSystemId) {
        log.debug("Loading Solarsystem " + solarSystemId + " from db.");
        return systemRepository.findById(solarSystemId).orElse(null);
    }

    public List<SolarSystem> getSolarSystems() {
        return Lists.newArrayList(systemRepository.findAll());
    }

    public Region saveRegion(Region region) {
        return regionRepository.save(region);
    }


    public List<Region> saveAllRegions(List<Region> regions) {
        return Lists.newArrayList(regionRepository.saveAll(regions));
    }


    public List<Constellation> saveAllConstellations(List<Constellation> constellations) {
        return Lists.newArrayList(constellationRepository.saveAll(constellations));
    }


    public List<SolarSystem> saveAllSolarSystems(List<SolarSystem> solarSystems) {
        return Lists.newArrayList(systemRepository.saveAll(solarSystems));
    }


    public List<Location> saveAllLocations(List<Location> locations) {
        return Lists.newArrayList(locationRepository.saveAll(locations));
    }

    //@CachePut(value = "locations", key = "#s.id")
    public Location saveLocation(Location s) {
        return locationRepository.save(s);
    }


    public Constellation saveConstellation(Constellation constellation) {
        return constellationRepository.save(constellation);
    }


    //@CachePut(value = "systems", key = "#system.id")
    public SolarSystem saveSolarSystem(SolarSystem system) {
        return systemRepository.save(system);
    }


    public List<Location> getForbiddenStructures() {
        return locationRepository.findByLocationTypeAndAccessForbidden(Location.STRUCTURE, true);
    }

    public List<Location> getStructures() {
        return Lists.newArrayList(locationRepository.findByLocationType(Location.STRUCTURE));
    }

    public Set<Long> getStructureIds() {
        return locationRepository.getStructureIds(Location.STRUCTURE);
    }

    public List<Location> getAllowedStructuresWithMarketForRegion(Integer id) {
        return locationRepository.findByRegionIdAndLocationTypeAndAccessForbiddenAndHasMarket(id, Location.STRUCTURE, false, true);
    }

    public Constellation getConstellation(int constellationId) {
        return constellationRepository.findById(constellationId).orElse(null);
    }

    public List<Constellation> getConstellations() {
        return Lists.newArrayList(constellationRepository.findAll());
    }

    public List<Location> getStations() {
        return locationRepository.findByLocationType(Location.STATION);
    }

    public Location getStation(int stationId) {
        return locationRepository.findById(Long.valueOf(stationId)).orElse(null);
    }
}
