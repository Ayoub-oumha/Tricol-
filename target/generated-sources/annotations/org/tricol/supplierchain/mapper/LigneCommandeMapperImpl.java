package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.request.LigneCommandeCreateDTO;
import org.tricol.supplierchain.dto.response.LigneCommandeResponseDTO;
import org.tricol.supplierchain.entity.LigneCommande;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T17:46:12+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class LigneCommandeMapperImpl implements LigneCommandeMapper {

    @Autowired
    private ProduitMapper produitMapper;

    @Override
    public LigneCommande toEntity(LigneCommandeCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        LigneCommande.LigneCommandeBuilder ligneCommande = LigneCommande.builder();

        ligneCommande.quantite( dto.getQuantite() );
        ligneCommande.prixUnitaire( dto.getPrixUnitaire() );

        return ligneCommande.build();
    }

    @Override
    public LigneCommandeResponseDTO toDto(LigneCommande entity) {
        if ( entity == null ) {
            return null;
        }

        LigneCommandeResponseDTO ligneCommandeResponseDTO = new LigneCommandeResponseDTO();

        ligneCommandeResponseDTO.setId( entity.getId() );
        ligneCommandeResponseDTO.setProduit( produitMapper.toResponseDTO( entity.getProduit() ) );
        ligneCommandeResponseDTO.setQuantite( entity.getQuantite() );
        ligneCommandeResponseDTO.setPrixUnitaire( entity.getPrixUnitaire() );
        ligneCommandeResponseDTO.setMontantLigneTotal( entity.getMontantLigneTotal() );

        return ligneCommandeResponseDTO;
    }
}
