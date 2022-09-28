package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.TypeGroupDTO;
import de.ronnywalter.eve.model.TypeGroup;
import de.ronnywalter.eve.service.TypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("typegroups")
@RequiredArgsConstructor
@Slf4j
public class TypeGroupController extends AbstractController{

    private final TypeService typeService;

    @GetMapping(value = "")
    public List<TypeGroupDTO> getTypeGroups() {
        return mapList(typeService.getTypeGroups(), TypeGroupDTO.class);
    }

    @GetMapping(value = "/{groupId}")
    public TypeGroupDTO getTypeGroup(@PathVariable int groupId) {
        return map(typeService.getTypeGroup(groupId), TypeGroupDTO.class);
    }

    @PostMapping(value = "")
    public TypeGroupDTO saveTypeGroup(@RequestBody TypeGroupDTO typeDTO) {
        return map(typeService.saveTypeGroup(map(typeDTO, TypeGroup.class)), TypeGroupDTO.class);
    }

}
