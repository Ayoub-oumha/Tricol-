package org.tricol.supplierchain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LigneBonSortieRequestDTO {

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité doit être positive")
    private BigDecimal quantite;

    @NotNull(message = "L'ID du produit est obligatoire")
    private Long produitId;
}

