package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.response.LotStockResponseDTO;
import org.tricol.supplierchain.entity.CommandeFournisseur;
import org.tricol.supplierchain.entity.LotStock;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T17:46:12+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
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
        lotStockResponseDTO.setId( lotStock.getId() );
        lotStockResponseDTO.setNumeroLot( lotStock.getNumeroLot() );
        lotStockResponseDTO.setQuantiteInitiale( lotStock.getQuantiteInitiale() );
        lotStockResponseDTO.setQuantiteRestante( lotStock.getQuantiteRestante() );
        lotStockResponseDTO.setPrixUnitaireAchat( lotStock.getPrixUnitaireAchat() );
        lotStockResponseDTO.setDateEntree( lotStock.getDateEntree() );
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
