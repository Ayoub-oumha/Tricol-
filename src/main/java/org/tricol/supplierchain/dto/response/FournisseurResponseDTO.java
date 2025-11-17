package org.tricol.supplierchain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurResponseDTO {

    private Long id;
    private String raisonSociale;
    private String adresse;
    private String ville;
    private String personneContact;
    private String email;
    private String telephone;
    private String ice;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

}
