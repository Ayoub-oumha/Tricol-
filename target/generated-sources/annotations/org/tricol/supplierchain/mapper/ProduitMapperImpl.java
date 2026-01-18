package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.request.ProduitRequestDTO;
import org.tricol.supplierchain.dto.request.ProduitUpdatDTO;
import org.tricol.supplierchain.dto.response.ProduitResponseDTO;
import org.tricol.supplierchain.entity.Produit;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T17:46:12+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class ProduitMapperImpl implements ProduitMapper {

    @Override
    public Produit toEntity(ProduitRequestDTO produitRequestDTO) {
        if ( produitRequestDTO == null ) {
            return null;
        }

        Produit.ProduitBuilder produit = Produit.builder();

        produit.reference( produitRequestDTO.getReference() );
        produit.nom( produitRequestDTO.getNom() );
        produit.description( produitRequestDTO.getDescription() );
        produit.stockActuel( produitRequestDTO.getStockActuel() );
        produit.pointCommande( produitRequestDTO.getPointCommande() );
        produit.uniteMesure( produitRequestDTO.getUniteMesure() );
        produit.categorie( produitRequestDTO.getCategorie() );

        return produit.build();
    }

    @Override
    public ProduitResponseDTO toResponseDTO(Produit produit) {
        if ( produit == null ) {
            return null;
        }

        ProduitResponseDTO produitResponseDTO = new ProduitResponseDTO();

        produitResponseDTO.setId( produit.getId() );
        produitResponseDTO.setReference( produit.getReference() );
        produitResponseDTO.setNom( produit.getNom() );
        produitResponseDTO.setDescription( produit.getDescription() );
        produitResponseDTO.setStockActuel( produit.getStockActuel() );
        produitResponseDTO.setPointCommande( produit.getPointCommande() );
        produitResponseDTO.setUniteMesure( produit.getUniteMesure() );
        produitResponseDTO.setCategorie( produit.getCategorie() );
        produitResponseDTO.setDateCreation( produit.getDateCreation() );
        produitResponseDTO.setDateModification( produit.getDateModification() );

        return produitResponseDTO;
    }

    @Override
    public void updateEntityFromDto(ProduitUpdatDTO produitUpdatDTO, Produit produit) {
        if ( produitUpdatDTO == null ) {
            return;
        }

        if ( produitUpdatDTO.getNom() != null ) {
            produit.setNom( produitUpdatDTO.getNom() );
        }
        if ( produitUpdatDTO.getDescription() != null ) {
            produit.setDescription( produitUpdatDTO.getDescription() );
        }
        if ( produitUpdatDTO.getStockActuel() != null ) {
            produit.setStockActuel( produitUpdatDTO.getStockActuel() );
        }
        if ( produitUpdatDTO.getPointCommande() != null ) {
            produit.setPointCommande( produitUpdatDTO.getPointCommande() );
        }
        if ( produitUpdatDTO.getUniteMesure() != null ) {
            produit.setUniteMesure( produitUpdatDTO.getUniteMesure() );
        }
        if ( produitUpdatDTO.getCategorie() != null ) {
            produit.setCategorie( produitUpdatDTO.getCategorie() );
        }
    }
}
