package org.tricol.supplierchain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tricol.supplierchain.dto.request.CommandeFournisseurCreateDTO;
import org.tricol.supplierchain.dto.request.CommandeFournisseurUpdateDTO;
import org.tricol.supplierchain.dto.response.CommandeFournisseurResponseDTO;
import org.tricol.supplierchain.entity.CommandeFournisseur;

@Mapper(componentModel = "spring", uses = {LigneCommandeMapper.class, FournisseurMapper.class})
public interface CommandeFournisseurMapper {


    CommandeFournisseur toEntity(CommandeFournisseurCreateDTO dto);


    void updateEntityFromDto(CommandeFournisseurUpdateDTO dto, @MappingTarget CommandeFournisseur entity);

    CommandeFournisseurResponseDTO toResponseDto(CommandeFournisseur entity);

}