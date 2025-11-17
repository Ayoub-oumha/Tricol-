package org.tricol.supplierchain.dto.response;


import lombok.Data;
import org.tricol.supplierchain.enums.StatutLot;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LotStockResponseDTO {
    private Long id;
    private String numeroLot;
    private BigDecimal quantiteInitiale;
    private BigDecimal quantiteRestante;
    private BigDecimal prixUnitaireAchat;
    private LocalDateTime dateEntree;
    private StatutLot statut;
    private String numeroCommande;
}
