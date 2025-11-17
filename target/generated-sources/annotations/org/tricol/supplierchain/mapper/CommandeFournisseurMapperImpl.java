package org.tricol.supplierchain.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.request.CommandeFournisseurCreateDTO;
import org.tricol.supplierchain.dto.request.CommandeFournisseurUpdateDTO;
import org.tricol.supplierchain.dto.response.CommandeFournisseurResponseDTO;
import org.tricol.supplierchain.dto.response.LigneCommandeResponseDTO;
import org.tricol.supplierchain.entity.CommandeFournisseur;
import org.tricol.supplierchain.entity.LigneCommande;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T09:13:52+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class CommandeFournisseurMapperImpl implements CommandeFournisseurMapper {

    @Autowired
    private LigneCommandeMapper ligneCommandeMapper;
    @Autowired
    private FournisseurMapper fournisseurMapper;

    @Override
    public CommandeFournisseur toEntity(CommandeFournisseurCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CommandeFournisseur.CommandeFournisseurBuilder commandeFournisseur = CommandeFournisseur.builder();

        commandeFournisseur.dateLivraisonPrevue( dto.getDateLivraisonPrevue() );

        return commandeFournisseur.build();
    }

    @Override
    public void updateEntityFromDto(CommandeFournisseurUpdateDTO dto, CommandeFournisseur entity) {
        if ( dto == null ) {
            return;
        }

        entity.setDateLivraisonPrevue( dto.getDateLivraisonPrevue() );
    }

    @Override
    public CommandeFournisseurResponseDTO toResponseDto(CommandeFournisseur entity) {
        if ( entity == null ) {
            return null;
        }

        CommandeFournisseurResponseDTO commandeFournisseurResponseDTO = new CommandeFournisseurResponseDTO();

        commandeFournisseurResponseDTO.setDateCommande( entity.getDateCommande() );
        commandeFournisseurResponseDTO.setDateLivraisonEffective( entity.getDateLivraisonEffective() );
        commandeFournisseurResponseDTO.setDateLivraisonPrevue( entity.getDateLivraisonPrevue() );
        commandeFournisseurResponseDTO.setFournisseur( fournisseurMapper.toResponseDTO( entity.getFournisseur() ) );
        commandeFournisseurResponseDTO.setId( entity.getId() );
        commandeFournisseurResponseDTO.setLignesCommande( ligneCommandeListToLigneCommandeResponseDTOList( entity.getLignesCommande() ) );
        commandeFournisseurResponseDTO.setMontantTotal( entity.getMontantTotal() );
        commandeFournisseurResponseDTO.setNumeroCommande( entity.getNumeroCommande() );
        commandeFournisseurResponseDTO.setStatut( entity.getStatut() );
        commandeFournisseurResponseDTO.setUpdatedAt( entity.getUpdatedAt() );

        return commandeFournisseurResponseDTO;
    }

    protected List<LigneCommandeResponseDTO> ligneCommandeListToLigneCommandeResponseDTOList(List<LigneCommande> list) {
        if ( list == null ) {
            return null;
        }

        List<LigneCommandeResponseDTO> list1 = new ArrayList<LigneCommandeResponseDTO>( list.size() );
        for ( LigneCommande ligneCommande : list ) {
            list1.add( ligneCommandeMapper.toDto( ligneCommande ) );
        }

        return list1;
    }
}
