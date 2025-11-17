package org.tricol.supplierchain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.tricol.supplierchain.enums.StatutLot;
import org.tricol.supplierchain.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lot_stock")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_lot", nullable = false, unique = true)
    private String numeroLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private CommandeFournisseur commande;

    @Column(name = "quantitee_initiale", nullable = false)
    private BigDecimal quantiteInitiale;

    @Column(name = "quantite_restante", nullable = false)
    private BigDecimal quantiteRestante;

    @Column(name = "prix_achat_unitatire", nullable = false)
    private BigDecimal prixUnitaireAchat;

    @CreatedDate
    @Column(name = "date_entree", nullable = false)
    private LocalDateTime dateEntree;

    @Enumerated(EnumType.STRING)
    private StatutLot statut;

    public BigDecimal consommer(BigDecimal quantite) {
        if (quantite.compareTo(this.quantiteRestante) > 0) {
            throw new BusinessException("Impossible de consommer plus que la quantité restante");
        }
        this.quantiteRestante = this.quantiteRestante.subtract(quantite);
        if (this.quantiteRestante.compareTo(BigDecimal.ZERO) == 0) {
            this.statut = StatutLot.EPUISE;
        }
        return this.quantiteRestante;
    }

    public boolean isEpuise() {
        return this.statut == StatutLot.EPUISE ||
                this.quantiteRestante.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isActif() {
        return this.statut == StatutLot.ACTIF &&
                this.quantiteRestante.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getValorisation() {
        return this.quantiteRestante.multiply(this.prixUnitaireAchat);
    }


}
