package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.request.LigneBonSortieRequestDTO;
import org.tricol.supplierchain.dto.response.LigneBonSortieResponseDTO;
import org.tricol.supplierchain.entity.LigneBonSortie;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T09:13:52+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class LigneBonSortieMapperImpl implements LigneBonSortieMapper {

    @Autowired
    private ProduitMapper produitMapper;

    @Override
    public LigneBonSortie toEntity(LigneBonSortieRequestDTO ligneBonSortieRequestDTO) {
        if ( ligneBonSortieRequestDTO == null ) {
            return null;
        }

        LigneBonSortie.LigneBonSortieBuilder ligneBonSortie = LigneBonSortie.builder();

        ligneBonSortie.quantite( ligneBonSortieRequestDTO.getQuantite() );

        return ligneBonSortie.build();
    }

    @Override
    public LigneBonSortieResponseDTO toResponseDTO(LigneBonSortie ligneBonSortie) {
        if ( ligneBonSortie == null ) {
            return null;
        }

        LigneBonSortieResponseDTO ligneBonSortieResponseDTO = new LigneBonSortieResponseDTO();

        ligneBonSortieResponseDTO.setId( ligneBonSortie.getId() );
        ligneBonSortieResponseDTO.setProduit( produitMapper.toResponseDTO( ligneBonSortie.getProduit() ) );
        ligneBonSortieResponseDTO.setQuantite( ligneBonSortie.getQuantite() );

        return ligneBonSortieResponseDTO;
    }
}
