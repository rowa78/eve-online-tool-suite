package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.TypeDTO;
import de.ronnywalter.eve.model.Type;
import de.ronnywalter.eve.service.TypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("types")
@RequiredArgsConstructor
@Slf4j
public class TypeController extends AbstractController{

    private final TypeService typeService;

    @GetMapping(value = "")
    public List<TypeDTO> getTypes() {
        return mapList(typeService.getTypes(), TypeDTO.class);
    }

    @GetMapping(value = "/{typeId}")
    public TypeDTO getType(@PathVariable int typeId) {
        return map(typeService.getType(typeId), TypeDTO.class);
    }

    @PostMapping(value = "")
    public TypeDTO saveType(@RequestBody TypeDTO typeDTO) {
        return map(typeService.saveType(map(typeDTO, Type.class)), TypeDTO.class);
    }

}
