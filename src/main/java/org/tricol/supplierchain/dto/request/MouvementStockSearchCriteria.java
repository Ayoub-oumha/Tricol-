package org.tricol.supplierchain.dto.request;

import lombok.Builder;
import lombok.Data;
import org.tricol.supplierchain.enums.TypeMouvement;

import java.time.LocalDate;

@Data
@Builder
public class MouvementStockSearchCriteria {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long produitId;
    private String reference;
    private TypeMouvement type;
    private String numeroLot;
}