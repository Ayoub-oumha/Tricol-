package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.response.MouvementStockResponseDTO;
import org.tricol.supplierchain.entity.LotStock;
import org.tricol.supplierchain.entity.MouvementStock;
import org.tricol.supplierchain.entity.Produit;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T17:46:12+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class MouvementStockMapperImpl implements MouvementStockMapper {

    @Override
    public MouvementStockResponseDTO toResponseDTO(MouvementStock mouvementStock) {
        if ( mouvementStock == null ) {
            return null;
        }

        MouvementStockResponseDTO mouvementStockResponseDTO = new MouvementStockResponseDTO();

        mouvementStockResponseDTO.setProduitId( mouvementStockProduitId( mouvementStock ) );
        mouvementStockResponseDTO.setReferenceProduit( mouvementStockProduitReference( mouvementStock ) );
        mouvementStockResponseDTO.setNomProduit( mouvementStockProduitNom( mouvementStock ) );
        mouvementStockResponseDTO.setNumeroLot( mouvementStockLotStockNumeroLot( mouvementStock ) );
        mouvementStockResponseDTO.setId( mouvementStock.getId() );
        mouvementStockResponseDTO.setTypeMouvement( mouvementStock.getTypeMouvement() );
        mouvementStockResponseDTO.setQuantite( mouvementStock.getQuantite() );
        mouvementStockResponseDTO.setDateMouvement( mouvementStock.getDateMouvement() );
        mouvementStockResponseDTO.setReference( mouvementStock.getReference() );
        mouvementStockResponseDTO.setMotif( mouvementStock.getMotif() );

        return mouvementStockResponseDTO;
    }

    private Long mouvementStockProduitId(MouvementStock mouvementStock) {
        Produit produit = mouvementStock.getProduit();
        if ( produit == null ) {
            return null;
        }
        return produit.getId();
    }

    private String mouvementStockProduitReference(MouvementStock mouvementStock) {
        Produit produit = mouvementStock.getProduit();
        if ( produit == null ) {
            return null;
        }
        return produit.getReference();
    }

    private String mouvementStockProduitNom(MouvementStock mouvementStock) {
        Produit produit = mouvementStock.getProduit();
        if ( produit == null ) {
            return null;
        }
        return produit.getNom();
    }

    private String mouvementStockLotStockNumeroLot(MouvementStock mouvementStock) {
        LotStock lotStock = mouvementStock.getLotStock();
        if ( lotStock == null ) {
            return null;
        }
        return lotStock.getNumeroLot();
    }
}
