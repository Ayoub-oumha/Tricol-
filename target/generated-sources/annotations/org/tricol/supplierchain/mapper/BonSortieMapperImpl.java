package org.tricol.supplierchain.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.request.BonSortieRequestDTO;
import org.tricol.supplierchain.dto.request.BonSortieUpdateDTO;
import org.tricol.supplierchain.dto.response.BonSortieResponseDTO;
import org.tricol.supplierchain.dto.response.LigneBonSortieResponseDTO;
import org.tricol.supplierchain.entity.BonSortie;
import org.tricol.supplierchain.entity.LigneBonSortie;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T09:13:52+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class BonSortieMapperImpl implements BonSortieMapper {

    @Autowired
    private LigneBonSortieMapper ligneBonSortieMapper;

    @Override
    public BonSortie toEntity(BonSortieRequestDTO bonSortieRequestDTO) {
        if ( bonSortieRequestDTO == null ) {
            return null;
        }

        BonSortie.BonSortieBuilder bonSortie = BonSortie.builder();

        bonSortie.atelier( bonSortieRequestDTO.getAtelier() );
        bonSortie.dateSortie( bonSortieRequestDTO.getDateSortie() );
        bonSortie.motif( bonSortieRequestDTO.getMotif() );

        return bonSortie.build();
    }

    @Override
    public BonSortieResponseDTO toResponseDTO(BonSortie bonSortie) {
        if ( bonSortie == null ) {
            return null;
        }

        BonSortieResponseDTO bonSortieResponseDTO = new BonSortieResponseDTO();

        bonSortieResponseDTO.setAtelier( bonSortie.getAtelier() );
        bonSortieResponseDTO.setDateCreation( bonSortie.getDateCreation() );
        bonSortieResponseDTO.setDateModification( bonSortie.getDateModification() );
        bonSortieResponseDTO.setDateSortie( bonSortie.getDateSortie() );
        bonSortieResponseDTO.setId( bonSortie.getId() );
        bonSortieResponseDTO.setLigneBonSorties( ligneBonSortieListToLigneBonSortieResponseDTOList( bonSortie.getLigneBonSorties() ) );
        bonSortieResponseDTO.setMontantTotal( bonSortie.getMontantTotal() );
        bonSortieResponseDTO.setMotif( bonSortie.getMotif() );
        bonSortieResponseDTO.setNumeroBon( bonSortie.getNumeroBon() );
        bonSortieResponseDTO.setStatut( bonSortie.getStatut() );

        return bonSortieResponseDTO;
    }

    @Override
    public void updateEntityFromDto(BonSortieUpdateDTO bonSortieUpdateDTO, BonSortie bonSortie) {
        if ( bonSortieUpdateDTO == null ) {
            return;
        }

        if ( bonSortieUpdateDTO.getAtelier() != null ) {
            bonSortie.setAtelier( bonSortieUpdateDTO.getAtelier() );
        }
        if ( bonSortieUpdateDTO.getDateSortie() != null ) {
            bonSortie.setDateSortie( bonSortieUpdateDTO.getDateSortie() );
        }
        if ( bonSortieUpdateDTO.getMotif() != null ) {
            bonSortie.setMotif( bonSortieUpdateDTO.getMotif() );
        }
        if ( bonSortieUpdateDTO.getStatut() != null ) {
            bonSortie.setStatut( bonSortieUpdateDTO.getStatut() );
        }
    }

    protected List<LigneBonSortieResponseDTO> ligneBonSortieListToLigneBonSortieResponseDTOList(List<LigneBonSortie> list) {
        if ( list == null ) {
            return null;
        }

        List<LigneBonSortieResponseDTO> list1 = new ArrayList<LigneBonSortieResponseDTO>( list.size() );
        for ( LigneBonSortie ligneBonSortie : list ) {
            list1.add( ligneBonSortieMapper.toResponseDTO( ligneBonSortie ) );
        }

        return list1;
    }
}
