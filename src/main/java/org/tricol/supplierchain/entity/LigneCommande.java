package org.tricol.supplierchain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "commande")
@Entity
@Table(name = "lignes_commande")
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    @NotNull(message = "La commande est obligatoire")
    private CommandeFournisseur commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    @NotNull(message = "Le produit est obligatoire")
    private Produit produit;

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "La quantité doit être positive")
    @Column(name = "quantite", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantite;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantLigneTotal = BigDecimal.ZERO;

    @PrePersist
    @PreUpdate
    public void calculerMontantTotal() {
        this.montantLigneTotal = this.quantite.multiply(this.prixUnitaire);
    }
}