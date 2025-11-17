package org.tricol.supplierchain.mapper;


import org.mapstruct.*;
import org.tricol.supplierchain.dto.request.ProduitRequestDTO;
import org.tricol.supplierchain.dto.request.ProduitUpdatDTO;
import org.tricol.supplierchain.dto.response.ProduitResponseDTO;
import org.tricol.supplierchain.entity.Produit;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    Produit toEntity(ProduitRequestDTO produitRequestDTO);
    ProduitResponseDTO toResponseDTO(Produit produit);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProduitUpdatDTO produitUpdatDTO, @MappingTarget Produit produit);

}
