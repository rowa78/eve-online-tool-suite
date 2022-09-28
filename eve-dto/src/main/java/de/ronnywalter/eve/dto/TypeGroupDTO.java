package de.ronnywalter.eve.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeGroupDTO {

    private Integer id;
    private String name;

    private int categoryId;

}
