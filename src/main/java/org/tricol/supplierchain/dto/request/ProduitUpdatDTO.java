package org.tricol.supplierchain.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class ProduitUpdatDTO {


    private String nom;

    private String description;

    @Min(value = 0, message = "le stock actuel doit être positif")
    private BigDecimal stockActuel;

    @Min(value = 0, message = "le point de commande doit être positif")
    private BigDecimal pointCommande;

    private String uniteMesure;

    private String categorie;
}
