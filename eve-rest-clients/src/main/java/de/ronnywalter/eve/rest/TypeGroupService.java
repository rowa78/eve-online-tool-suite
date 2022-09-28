package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.TypeDTO;
import de.ronnywalter.eve.dto.TypeGroupDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "eve-backend/typegroups", decode404 = true)
public interface TypeGroupService {

    @RequestMapping(method = RequestMethod.GET, value = "")
    List<TypeGroupDTO> getTypeGroups();

    @RequestMapping(method = RequestMethod.GET, value = "/{typeGroupId}")
    TypeGroupDTO getTypeGroup(@PathVariable("typeGroupId") int typeGroupId);

    @PostMapping()
    TypeGroupDTO createTypeGroup(@RequestBody TypeGroupDTO typeGroupDTO);

}
