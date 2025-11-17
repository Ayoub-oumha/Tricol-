package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.response.LotStockResponseDTO;
import org.tricol.supplierchain.entity.CommandeFournisseur;
import org.tricol.supplierchain.entity.LotStock;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T09:13:52+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class LotStockMapperImpl implements LotStockMapper {

    @Override
    public LotStockResponseDTO toResponseDTO(LotStock lotStock) {
        if ( lotStock == null ) {
            return null;
        }

        LotStockResponseDTO lotStockResponseDTO = new LotStockResponseDTO();

        lotStockResponseDTO.setNumeroCommande( lotStockCommandeNumeroCommande( lotStock ) );
        lotStockResponseDTO.setDateEntree( lotStock.getDateEntree() );
        lotStockResponseDTO.setId( lotStock.getId() );
        lotStockResponseDTO.setNumeroLot( lotStock.getNumeroLot() );
        lotStockResponseDTO.setPrixUnitaireAchat( lotStock.getPrixUnitaireAchat() );
        lotStockResponseDTO.setQuantiteInitiale( lotStock.getQuantiteInitiale() );
        lotStockResponseDTO.setQuantiteRestante( lotStock.getQuantiteRestante() );
        lotStockResponseDTO.setStatut( lotStock.getStatut() );

        return lotStockResponseDTO;
    }

    private String lotStockCommandeNumeroCommande(LotStock lotStock) {
        CommandeFournisseur commande = lotStock.getCommande();
        if ( commande == null ) {
            return null;
        }
        return commande.getNumeroCommande();
    }
}
