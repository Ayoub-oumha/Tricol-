package org.tricol.supplierchain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeResponseDTO {
    
    private Long id;
    private ProduitResponseDTO produit;
    private BigDecimal quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal montantLigneTotal;
}