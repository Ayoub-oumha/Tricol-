package org.tricol.supplierchain.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tricol.supplierchain.dto.response.LotStockResponseDTO;
import org.tricol.supplierchain.entity.LotStock;

@Mapper(componentModel = "spring")
public interface LotStockMapper {

    @Mapping(source = "commande.numeroCommande", target = "numeroCommande")
    LotStockResponseDTO toResponseDTO(LotStock lotStock);

}
