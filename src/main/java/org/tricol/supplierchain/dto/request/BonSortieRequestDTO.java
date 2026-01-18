package org.tricol.supplierchain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.tricol.supplierchain.enums.Atelier;
import org.tricol.supplierchain.enums.MotifBonSortie;

import java.time.LocalDate;
import java.util.List;

@Data
public class BonSortieRequestDTO {


    @NotNull(message = "La date de sortie est obligatoire")
    @FutureOrPresent(message = "La date de livraison prevue doit etre aujourd'hui ou ulterieure")
    private LocalDate dateSortie;

    @NotNull(message = "Le motif est obligatoire")
    private MotifBonSortie motif;

    @NotNull(message = "L'atelier est obligatoire")
    private Atelier atelier;

    @NotEmpty(message = "La liste des lignes de bon de sortie ne peut pas être vide")
    @Valid
    private List<LigneBonSortieRequestDTO> ligneBonSorties;
}