package de.ronnywalter.eve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorpDTO {

    private Integer id;

    private int userId;

    private String name;
    private String ticker;
    private Integer memberCount;

    private Integer ceoId;
    private String ceoName;

    private String logo;

}
