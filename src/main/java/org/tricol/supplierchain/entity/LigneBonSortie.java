package org.tricol.supplierchain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "ligne_bon_sortie")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LigneBonSortie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal quantite;

    @ManyToOne
    @JoinColumn(name = "bon_sortie_id", nullable = false)
    private BonSortie bonSortie;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

}
