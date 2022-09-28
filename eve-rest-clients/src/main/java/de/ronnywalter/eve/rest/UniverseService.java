package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.ConstellationDTO;
import de.ronnywalter.eve.dto.LocationDTO;
import de.ronnywalter.eve.dto.RegionDTO;
import de.ronnywalter.eve.dto.SolarSystemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "eve-backend/universe", decode404 = true)
public interface UniverseService {

    @GetMapping(value = "regions")
    public List<RegionDTO> getRegions();

    @GetMapping(value = "regions/{regionId}")
    public RegionDTO getRegion(@PathVariable("regionId") int regionId);

    @GetMapping(value = "constellations")
    public List<ConstellationDTO> getConstellations();

    @GetMapping(value = "constellations/{constellationId}")
    public ConstellationDTO getConstellation(@PathVariable("constellationId") int constellationId);

    @GetMapping(value = "systems")
    public List<SolarSystemDTO> getSolarSystems();

    @GetMapping(value = "systems/{solarSystemId}")
    public SolarSystemDTO getSolarSystem(@PathVariable("solarSystemId") int solarSystemId);

    @PostMapping(value="regions")
    public RegionDTO saveRegion(@RequestBody RegionDTO regionDTO);

    @PostMapping(value="constellations")
    public ConstellationDTO saveConstellations(@RequestBody ConstellationDTO constellationDTO);

    @PostMapping(value="systems")
    public SolarSystemDTO saveSolarSystem(@RequestBody SolarSystemDTO solarSystemDTO);

    @GetMapping(value = "stations")
    public List<LocationDTO> getStations();

    @GetMapping(value = "stations/{stationId}")
    public LocationDTO getStation(@PathVariable("stationId") int stationId);

    @PostMapping(value="stations")
    public LocationDTO saveStation(@RequestBody LocationDTO locationDTO);

    @GetMapping(value = "structures")
    public List<LocationDTO> getstructures();

    @GetMapping("structures-markets/{regionId}")
    public List<LocationDTO> getStructuresWithMarkets(@PathVariable("regionId") int regionId);

    @GetMapping(value = "structures/{structureId}")
    public LocationDTO getstructure(@PathVariable("structureId") int structureId);

    @PostMapping(value="structures")
    public LocationDTO savestructure(@RequestBody LocationDTO locationDTO);

}
