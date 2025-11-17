package org.tricol.supplierchain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.tricol.supplierchain.enums.Atelier;
import org.tricol.supplierchain.enums.MotifBonSortie;
import org.tricol.supplierchain.enums.StatutBonSortie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BonSortie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "numero_bon", nullable = false, unique = true)
    private String numeroBon;
    @Column(name = "date_sortie", nullable = false)
    private LocalDate dateSortie;
    @Enumerated(EnumType.STRING)
    private StatutBonSortie statut;
    @Enumerated(EnumType.STRING)
    private MotifBonSortie motif;

    @Enumerated(EnumType.STRING)
    @Column(name = "atelier")
    private Atelier atelier;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime dateModification;

    @ToString.Exclude
    @OneToMany(mappedBy = "bonSortie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneBonSortie> ligneBonSorties = new ArrayList<>();

    @Column(name = "montant_total", nullable = false)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.dateCreation = now;
        this.dateModification = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }


}
