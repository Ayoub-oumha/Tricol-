package org.tricol.supplierchain.dto.response;

import lombok.Data;
import org.tricol.supplierchain.enums.Atelier;
import org.tricol.supplierchain.enums.MotifBonSortie;
import org.tricol.supplierchain.enums.StatutBonSortie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BonSortieResponseDTO {

    private Long id;

    private String numeroBon;

    private LocalDate dateSortie;

    private StatutBonSortie statut;

    private MotifBonSortie motif;

    private Atelier atelier;

    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    private List<LigneBonSortieResponseDTO> ligneBonSorties;

    private BigDecimal montantTotal;
}
