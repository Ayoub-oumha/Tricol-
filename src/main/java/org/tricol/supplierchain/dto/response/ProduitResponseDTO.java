package org.tricol.supplierchain.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProduitResponseDTO {

    private Long id;

    private String reference;

    private String nom;

    private String description;


    private BigDecimal stockActuel;

    private BigDecimal pointCommande;

    private String uniteMesure;

    private String categorie;

    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;
}
