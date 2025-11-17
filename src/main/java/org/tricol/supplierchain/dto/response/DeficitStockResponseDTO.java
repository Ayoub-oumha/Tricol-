package org.tricol.supplierchain.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;


@Data
@Builder
public class DeficitStockResponseDTO {
    private Long produitId;
    private String referenceProduit;
    private String nomProduit;
    private BigDecimal quantiteDemandee;
    private BigDecimal quantiteDisponible;
    private BigDecimal quantiteManquante;
    private Long fournisseurId;
}
