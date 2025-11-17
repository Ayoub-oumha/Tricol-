package org.tricol.supplierchain.dto.response;


import lombok.Data;
import org.tricol.supplierchain.entity.Produit;
import org.tricol.supplierchain.enums.TypeMouvement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MouvementStockResponseDTO {
    private Long id;
    private Long produitId;
    private String referenceProduit;
    private String nomProduit;
    private TypeMouvement typeMouvement;
    private BigDecimal quantite;
    private LocalDateTime dateMouvement;
    private String reference;
    private String motif;
    private String numeroLot;
}
