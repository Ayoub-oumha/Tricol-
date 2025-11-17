package org.tricol.supplierchain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tricol.supplierchain.dto.response.MouvementStockResponseDTO;
import org.tricol.supplierchain.entity.MouvementStock;

@Mapper(componentModel = "spring")
public interface MouvementStockMapper {

    @Mapping(source = "produit.id", target = "produitId")
    @Mapping(source = "produit.reference", target = "referenceProduit")
    @Mapping(source = "produit.nom", target = "nomProduit")
    @Mapping(source = "lotStock.numeroLot" ,target = "numeroLot")
    MouvementStockResponseDTO toResponseDTO(MouvementStock mouvementStock);
}
