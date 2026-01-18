package org.tricol.supplierchain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PointCommandeRequest {
    @NotNull(message = "le point de commande est obligatoire")
    @Min(value = 0, message = "le point de commande doit être positif")
    private BigDecimal pointCommande;
}
