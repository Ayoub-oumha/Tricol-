package org.tricol.supplierchain.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.tricol.supplierchain.dto.request.FournisseurRequestDTO;
import org.tricol.supplierchain.dto.request.FournisseurUpdateDTO;
import org.tricol.supplierchain.dto.response.FournisseurResponseDTO;
import org.tricol.supplierchain.entity.Fournisseur;

@Mapper(componentModel = "spring")
public interface FournisseurMapper {


    Fournisseur toEntity(FournisseurRequestDTO fournisseurRequestDTO);
    FournisseurResponseDTO toResponseDTO(Fournisseur fournisseur);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(FournisseurUpdateDTO dto, @MappingTarget Fournisseur fournisseur);

}

