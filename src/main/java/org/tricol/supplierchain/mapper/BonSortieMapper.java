package org.tricol.supplierchain.mapper;

import org.mapstruct.*;
import org.tricol.supplierchain.dto.request.BonSortieRequestDTO;
import org.tricol.supplierchain.dto.request.BonSortieUpdateDTO;
import org.tricol.supplierchain.dto.response.BonSortieResponseDTO;
import org.tricol.supplierchain.entity.BonSortie;

@Mapper(componentModel = "spring", uses = {LigneBonSortieMapper.class})
public interface BonSortieMapper {


    BonSortie toEntity(BonSortieRequestDTO bonSortieRequestDTO);

    BonSortieResponseDTO toResponseDTO(BonSortie bonSortie);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "ligneBonSorties", ignore = true)
    void updateEntityFromDto(BonSortieUpdateDTO bonSortieUpdateDTO, @MappingTarget BonSortie bonSortie);
}
