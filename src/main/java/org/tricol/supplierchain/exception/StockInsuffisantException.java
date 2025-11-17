package org.tricol.supplierchain.exception;


import lombok.Getter;
import org.tricol.supplierchain.dto.response.DeficitStockResponseDTO;

import java.util.List;

@Getter
public class StockInsuffisantException extends RuntimeException{

    private final List<DeficitStockResponseDTO> deficits;
    private final String numeroBonSortie;


    public StockInsuffisantException(String numeroBon, List<DeficitStockResponseDTO> deficits) {
        super(buildMessage(numeroBon, deficits));
        this.deficits = deficits;
        this.numeroBonSortie = numeroBon;
    }
    private static String buildMessage(String numeroBon, List<DeficitStockResponseDTO> deficits) {
        return String.format("Stock insuffisant pour le bon %s. %d produit(s) en déficit. Action par defaut: des commandes sont creer pour les fournisseurs",
                numeroBon, deficits.size());
    }

}
