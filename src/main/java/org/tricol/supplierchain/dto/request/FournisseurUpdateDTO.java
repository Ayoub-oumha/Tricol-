package org.tricol.supplierchain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurUpdateDTO {

    private String raisonSociale;
    private String adresse;
    private String ville;
    private String personneContact;

    @Email(message = "Email invalide")
    private String email;

    private String telephone;

    @Pattern(regexp = "\\d{15}", message = "Le numéro ICE doit contenir exactement 15 chiffres")
    private String ice;
}
