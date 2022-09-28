package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.ConstellationDTO;
import de.ronnywalter.eve.dto.LocationDTO;
import de.ronnywalter.eve.dto.RegionDTO;
import de.ronnywalter.eve.dto.SolarSystemDTO;
import de.ronnywalter.eve.model.Constellation;
import de.ronnywalter.eve.model.Location;
import de.ronnywalter.eve.model.Region;
import de.ronnywalter.eve.model.SolarSystem;
import de.ronnywalter.eve.service.UniverseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("universe")
@RequiredArgsConstructor
@Slf4j
public class UniverseController extends AbstractController {

    private final UniverseService universeService;

    @GetMapping("/regions")
    public List<RegionDTO> getRegions() {
        List<RegionDTO> regionDTOS = new ArrayList<>();
        return mapList(universeService.getRegions(), RegionDTO.class);
    }

    @GetMapping("/regions/{regionId}")
    public RegionDTO getRegion(@PathVariable Integer regionId) {
        Region savedRegion = universeService.getRegion(regionId);
        return map(savedRegion, RegionDTO.class);
    }

    @PostMapping("/regions")
    public RegionDTO saveRegion(@RequestBody RegionDTO regionDTO) {
        return map(universeService.saveRegion(map(regionDTO, Region.class)), RegionDTO.class);
    }

    @GetMapping("/constellations")
    public List<ConstellationDTO> getConstellations() {
        List<RegionDTO> regionDTOS = new ArrayList<>();
        return mapList(universeService.getConstellations(), ConstellationDTO.class);
    }

    @GetMapping("/constellations/{constellationId}")
    public ConstellationDTO getConstellation(@PathVariable Integer constellationId) {
        Constellation constellation = universeService.getConstellation(constellationId);
        return map(constellation, ConstellationDTO.class);
    }

    @PostMapping("/constellations")
    public ConstellationDTO saveConstellation(@RequestBody ConstellationDTO constellationDTO) {
        return map(universeService.saveConstellation(map(constellationDTO, Constellation.class)), ConstellationDTO.class);
    }

    @GetMapping("/systems")
    public List<SolarSystemDTO> getSolarSystems() {
        List<SolarSystemDTO> systemDTOS = new ArrayList<>();
        return mapList(universeService.getSolarSystems(), SolarSystemDTO.class);
    }

    @GetMapping("/systems/{systemId}")
    public SolarSystemDTO getSolarSystem(@PathVariable Integer solarSystemId) {
        SolarSystem solarSystem = universeService.getSolarSystem(solarSystemId);
        return map(solarSystem, SolarSystemDTO.class);
    }

    @PostMapping("/systems")
    public SolarSystemDTO saveSolarSystem(@RequestBody SolarSystemDTO solarSystemDTO) {
        return map(universeService.saveSolarSystem(map(solarSystemDTO, SolarSystem.class)), SolarSystemDTO.class);
    }

    @GetMapping("stations")
    public List<LocationDTO> getStations() {
        return mapList(universeService.getStations(), LocationDTO.class);
    }

    @GetMapping("stations/{stationId}")
    public LocationDTO getStation(@PathVariable int stationId) {
        return map(universeService.getStation(stationId), LocationDTO.class);
    }

    @PostMapping("stations")
    public LocationDTO saveStation(@RequestBody LocationDTO locationDTO) {
        return map(universeService.saveLocation(map(locationDTO, Location.class)), LocationDTO.class);
    }

    @GetMapping("structures")
    public List<LocationDTO> getStructures() {
        return mapList(universeService.getStructures(), LocationDTO.class);
    }

    @GetMapping("structures-markets/{regionId}")
    public List<LocationDTO> getStructuresWithMarkets(@PathVariable int regionId) {
        return mapList(universeService.getAllowedStructuresWithMarketForRegion(regionId), LocationDTO.class);
    }

    @GetMapping("structures/{structureId}")
    public LocationDTO getStructure(@PathVariable int structureId) {
        return map(universeService.getLocation(structureId), LocationDTO.class);
    }

    @PostMapping("structures")
    public LocationDTO saveStructure(@RequestBody LocationDTO locationDTO) {
        return map(universeService.saveLocation(map(locationDTO, Location.class)), LocationDTO.class);
    }

}
