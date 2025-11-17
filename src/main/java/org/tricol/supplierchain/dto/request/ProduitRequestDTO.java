package org.tricol.supplierchain.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProduitRequestDTO {

    @NotBlank(message = "la référence est obligatoire")
    private String reference;

    @NotBlank(message = "le nom est obligatoire")
    private String nom;

    @NotBlank(message = "la description est obligatoire")
    private String description;


    @NotNull(message = "le stock actuel est obligatoire")
    @Min(value = 0, message = "le stock actuel doit être positif")
    private BigDecimal stockActuel;

    @NotNull(message = "le point de commande est obligatoire")
    @Min(value = 0, message = "le point de commande doit être positif")
    private BigDecimal pointCommande;

    @NotBlank(message = "l'unité de mesure est obligatoire")
    private String uniteMesure;

    @NotBlank(message = "la catégorie est obligatoire")
    private String categorie;

}
