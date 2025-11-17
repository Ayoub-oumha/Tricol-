package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.response.MouvementStockResponseDTO;
import org.tricol.supplierchain.entity.LotStock;
import org.tricol.supplierchain.entity.MouvementStock;
import org.tricol.supplierchain.entity.Produit;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T09:13:52+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
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
        mouvementStockResponseDTO.setDateMouvement( mouvementStock.getDateMouvement() );
        mouvementStockResponseDTO.setId( mouvementStock.getId() );
        mouvementStockResponseDTO.setMotif( mouvementStock.getMotif() );
        mouvementStockResponseDTO.setQuantite( mouvementStock.getQuantite() );
        mouvementStockResponseDTO.setReference( mouvementStock.getReference() );
        mouvementStockResponseDTO.setTypeMouvement( mouvementStock.getTypeMouvement() );

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
