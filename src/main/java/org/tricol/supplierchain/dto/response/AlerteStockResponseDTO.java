package org.tricol.supplierchain.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlerteStockResponseDTO {
    private Long produitId;
    private String referenceProduit;
    private String nomProduit;
    private BigDecimal stockActuel;
    private BigDecimal pointCommande;
    private BigDecimal quantiteManquante;
    private String categorie;
}
