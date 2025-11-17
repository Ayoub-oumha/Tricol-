package org.tricol.supplierchain.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LigneBonSortieResponseDTO {

    private Long id;

    private BigDecimal quantite;

    private ProduitResponseDTO produit;
}

