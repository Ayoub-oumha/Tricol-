package org.tricol.supplierchain.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tricol.supplierchain.dto.response.AlerteStockResponseDTO;
import org.tricol.supplierchain.dto.response.StockProduitResponseDTO;
import org.tricol.supplierchain.entity.Produit;


@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(source = "id", target = "produitId")
    @Mapping(source = "reference", target = "referenceProduit")
    @Mapping(source = "nom", target = "nomProduit")
    @Mapping(source = "stockActuel", target = "stockTotal")
    @Mapping(target = "valorisationTotale", ignore = true)
    @Mapping(target = "alerteSeuil", ignore = true)
    @Mapping(target = "lots", ignore = true)
    StockProduitResponseDTO toStockProduitDto(Produit produit);

    @Mapping(source = "id", target = "produitId")
    @Mapping(source = "reference", target = "referenceProduit")
    @Mapping(source = "nom", target = "nomProduit")
    @Mapping(source = "stockActuel", target = "stockActuel")
    @Mapping(target = "quantiteManquante", ignore = true)
    AlerteStockResponseDTO toAlertStock(Produit produit);
}
