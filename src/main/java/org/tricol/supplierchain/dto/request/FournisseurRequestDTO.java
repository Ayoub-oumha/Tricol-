package org.tricol.supplierchain.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurRequestDTO {

    @NotBlank(message = "le raison sociale  est obligatoire")
    private String raisonSociale;

    @NotBlank(message = "l'adresse  est obligatoire")
    private String adresse;

    @NotBlank(message = "la ville  est obligatoire")
    private String ville;

    @NotBlank(message = "Le nom de la personne de contact est obligatoire")
    private String personneContact;

    @Email(message = "email invalide")
    @NotBlank(message = "l'email n'est pas valide")
    private String email;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String telephone;

    @NotBlank(message = "Le numéro ICE est obligatoire")
    @Pattern(regexp = "\\d{15}", message = "Le numéro ICE doit contenir exactement 15 chiffres")
    private String ice;



}
