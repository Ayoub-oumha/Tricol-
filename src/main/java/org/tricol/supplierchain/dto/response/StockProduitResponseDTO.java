package org.tricol.supplierchain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class StockProduitResponseDTO {
    private Long produitId;
    private String referenceProduit;
    private String nomProduit;
    private String uniteMesure;
    private BigDecimal stockTotal;
    private BigDecimal pointCommande;
    private BigDecimal valorisationTotale;
    private Boolean alerteSeuil;
    private List<LotStockResponseDTO> lots;
}
