package org.tricol.supplierchain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class StockGlobalResponseDTO {
    private List<StockProduitResponseDTO> produits;
    private BigDecimal valorisationTotale;
    private Integer nombreProduitsTotal;
    private Long nombreProduitsEnAlerte;
    private Long nombreLotsActifs;
}
