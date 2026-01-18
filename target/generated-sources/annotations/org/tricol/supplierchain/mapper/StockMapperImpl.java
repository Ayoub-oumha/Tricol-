package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.response.AlerteStockResponseDTO;
import org.tricol.supplierchain.dto.response.StockProduitResponseDTO;
import org.tricol.supplierchain.entity.Produit;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T17:46:12+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class StockMapperImpl implements StockMapper {

    @Override
    public StockProduitResponseDTO toStockProduitDto(Produit produit) {
        if ( produit == null ) {
            return null;
        }

        StockProduitResponseDTO.StockProduitResponseDTOBuilder stockProduitResponseDTO = StockProduitResponseDTO.builder();

        stockProduitResponseDTO.produitId( produit.getId() );
        stockProduitResponseDTO.referenceProduit( produit.getReference() );
        stockProduitResponseDTO.nomProduit( produit.getNom() );
        stockProduitResponseDTO.stockTotal( produit.getStockActuel() );
        stockProduitResponseDTO.uniteMesure( produit.getUniteMesure() );
        stockProduitResponseDTO.pointCommande( produit.getPointCommande() );

        return stockProduitResponseDTO.build();
    }

    @Override
    public AlerteStockResponseDTO toAlertStock(Produit produit) {
        if ( produit == null ) {
            return null;
        }

        AlerteStockResponseDTO alerteStockResponseDTO = new AlerteStockResponseDTO();

        alerteStockResponseDTO.setProduitId( produit.getId() );
        alerteStockResponseDTO.setReferenceProduit( produit.getReference() );
        alerteStockResponseDTO.setNomProduit( produit.getNom() );
        alerteStockResponseDTO.setStockActuel( produit.getStockActuel() );
        alerteStockResponseDTO.setPointCommande( produit.getPointCommande() );
        alerteStockResponseDTO.setCategorie( produit.getCategorie() );

        return alerteStockResponseDTO;
    }
}
