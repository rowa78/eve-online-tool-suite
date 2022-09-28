package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.TypeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "eve-backend/types", decode404 = true)
public interface TypeService {

    @RequestMapping(method = RequestMethod.GET, value = "")
    List<TypeDTO> getTypes();

    @RequestMapping(method = RequestMethod.GET, value = "/{typeId}")
    TypeDTO getType(@PathVariable("typeId") int typeId);

    @PostMapping()
    TypeDTO createType(@RequestBody TypeDTO typeDTO);

}
