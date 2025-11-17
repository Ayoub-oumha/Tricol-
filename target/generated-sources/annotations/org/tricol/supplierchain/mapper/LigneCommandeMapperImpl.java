package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.request.LigneCommandeCreateDTO;
import org.tricol.supplierchain.dto.response.LigneCommandeResponseDTO;
import org.tricol.supplierchain.entity.LigneCommande;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T09:13:52+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
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

        ligneCommande.prixUnitaire( dto.getPrixUnitaire() );
        ligneCommande.quantite( dto.getQuantite() );

        return ligneCommande.build();
    }

    @Override
    public LigneCommandeResponseDTO toDto(LigneCommande entity) {
        if ( entity == null ) {
            return null;
        }

        LigneCommandeResponseDTO ligneCommandeResponseDTO = new LigneCommandeResponseDTO();

        ligneCommandeResponseDTO.setId( entity.getId() );
        ligneCommandeResponseDTO.setMontantLigneTotal( entity.getMontantLigneTotal() );
        ligneCommandeResponseDTO.setPrixUnitaire( entity.getPrixUnitaire() );
        ligneCommandeResponseDTO.setProduit( produitMapper.toResponseDTO( entity.getProduit() ) );
        ligneCommandeResponseDTO.setQuantite( entity.getQuantite() );

        return ligneCommandeResponseDTO;
    }
}
