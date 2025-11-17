package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.request.ProduitRequestDTO;
import org.tricol.supplierchain.dto.request.ProduitUpdatDTO;
import org.tricol.supplierchain.dto.response.ProduitResponseDTO;
import org.tricol.supplierchain.entity.Produit;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T09:13:52+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ProduitMapperImpl implements ProduitMapper {

    @Override
    public Produit toEntity(ProduitRequestDTO produitRequestDTO) {
        if ( produitRequestDTO == null ) {
            return null;
        }

        Produit.ProduitBuilder produit = Produit.builder();

        produit.categorie( produitRequestDTO.getCategorie() );
        produit.description( produitRequestDTO.getDescription() );
        produit.nom( produitRequestDTO.getNom() );
        produit.pointCommande( produitRequestDTO.getPointCommande() );
        produit.reference( produitRequestDTO.getReference() );
        produit.stockActuel( produitRequestDTO.getStockActuel() );
        produit.uniteMesure( produitRequestDTO.getUniteMesure() );

        return produit.build();
    }

    @Override
    public ProduitResponseDTO toResponseDTO(Produit produit) {
        if ( produit == null ) {
            return null;
        }

        ProduitResponseDTO produitResponseDTO = new ProduitResponseDTO();

        produitResponseDTO.setCategorie( produit.getCategorie() );
        produitResponseDTO.setDateCreation( produit.getDateCreation() );
        produitResponseDTO.setDateModification( produit.getDateModification() );
        produitResponseDTO.setDescription( produit.getDescription() );
        produitResponseDTO.setId( produit.getId() );
        produitResponseDTO.setNom( produit.getNom() );
        produitResponseDTO.setPointCommande( produit.getPointCommande() );
        produitResponseDTO.setReference( produit.getReference() );
        produitResponseDTO.setStockActuel( produit.getStockActuel() );
        produitResponseDTO.setUniteMesure( produit.getUniteMesure() );

        return produitResponseDTO;
    }

    @Override
    public void updateEntityFromDto(ProduitUpdatDTO produitUpdatDTO, Produit produit) {
        if ( produitUpdatDTO == null ) {
            return;
        }

        if ( produitUpdatDTO.getCategorie() != null ) {
            produit.setCategorie( produitUpdatDTO.getCategorie() );
        }
        if ( produitUpdatDTO.getDescription() != null ) {
            produit.setDescription( produitUpdatDTO.getDescription() );
        }
        if ( produitUpdatDTO.getNom() != null ) {
            produit.setNom( produitUpdatDTO.getNom() );
        }
        if ( produitUpdatDTO.getPointCommande() != null ) {
            produit.setPointCommande( produitUpdatDTO.getPointCommande() );
        }
        if ( produitUpdatDTO.getStockActuel() != null ) {
            produit.setStockActuel( produitUpdatDTO.getStockActuel() );
        }
        if ( produitUpdatDTO.getUniteMesure() != null ) {
            produit.setUniteMesure( produitUpdatDTO.getUniteMesure() );
        }
    }
}
