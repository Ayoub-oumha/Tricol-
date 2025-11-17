package org.tricol.supplierchain.mapper;

import org.mapstruct.Mapper;
import org.tricol.supplierchain.dto.request.LigneCommandeCreateDTO;
import org.tricol.supplierchain.dto.response.LigneCommandeResponseDTO;
import org.tricol.supplierchain.entity.LigneCommande;

@Mapper(componentModel = "spring", uses = {ProduitMapper.class})
public interface LigneCommandeMapper {

    LigneCommande toEntity(LigneCommandeCreateDTO dto);

    LigneCommandeResponseDTO toDto(LigneCommande entity);


}