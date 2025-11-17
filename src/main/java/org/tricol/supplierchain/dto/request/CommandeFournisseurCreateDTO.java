package org.tricol.supplierchain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeFournisseurCreateDTO {

    @NotNull(message = "L'identifiant du fournisseur est obligatoire")
    private Long fournisseurId;

    @NotNull(message = "La date de livraison prévue est obligatoire")
    @FutureOrPresent(message = "La date de livraison prevue doit etre aujourd'hui ou ulterieure")
    private LocalDate dateLivraisonPrevue;

    @Valid
    @Size(min = 1, message = "La commande doit contenir au moins une ligne.")
    private List<LigneCommandeCreateDTO> lignes;

}
