package org.tricol.supplierchain.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ValorisationStockResponseDTO {
    private BigDecimal valorisationTotale;
    private Integer nombreProduits;
    private Integer nombreLotsActifs;
    private LocalDateTime dateCalcul;
}
