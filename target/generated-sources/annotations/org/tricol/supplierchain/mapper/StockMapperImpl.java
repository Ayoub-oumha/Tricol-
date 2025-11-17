package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.response.AlerteStockResponseDTO;
import org.tricol.supplierchain.dto.response.StockProduitResponseDTO;
import org.tricol.supplierchain.entity.Produit;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T09:13:52+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
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
        stockProduitResponseDTO.pointCommande( produit.getPointCommande() );
        stockProduitResponseDTO.uniteMesure( produit.getUniteMesure() );

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
        alerteStockResponseDTO.setCategorie( produit.getCategorie() );
        alerteStockResponseDTO.setPointCommande( produit.getPointCommande() );

        return alerteStockResponseDTO;
    }
}
