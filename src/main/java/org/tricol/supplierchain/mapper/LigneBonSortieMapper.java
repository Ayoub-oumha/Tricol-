package org.tricol.supplierchain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tricol.supplierchain.dto.request.LigneBonSortieRequestDTO;
import org.tricol.supplierchain.dto.response.LigneBonSortieResponseDTO;
import org.tricol.supplierchain.entity.LigneBonSortie;

@Mapper(componentModel = "spring", uses = {ProduitMapper.class})
public interface LigneBonSortieMapper {

    LigneBonSortie toEntity(LigneBonSortieRequestDTO ligneBonSortieRequestDTO);


    LigneBonSortieResponseDTO toResponseDTO(LigneBonSortie ligneBonSortie);
}


