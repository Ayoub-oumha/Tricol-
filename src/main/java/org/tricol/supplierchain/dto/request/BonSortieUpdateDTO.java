package org.tricol.supplierchain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;
import org.tricol.supplierchain.enums.Atelier;
import org.tricol.supplierchain.enums.MotifBonSortie;
import org.tricol.supplierchain.enums.StatutBonSortie;

import java.time.LocalDate;
import java.util.List;

@Data
public class BonSortieUpdateDTO {

    @FutureOrPresent(message = "La date de livraison prevue doit etre aujourd'hui ou ulterieure")
    private LocalDate dateSortie;

    private StatutBonSortie statut;

    private MotifBonSortie motif;

    private Atelier atelier;

    @Valid
    private List<LigneBonSortieRequestDTO> lignesBonSortie;
}
