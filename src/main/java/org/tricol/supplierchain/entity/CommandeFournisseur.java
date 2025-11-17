package org.tricol.supplierchain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.tricol.supplierchain.enums.StatutCommande;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "lignesCommande")
@Entity
@Table(name = "commande_fournisseur")
public class CommandeFournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_commande", nullable = false, unique = true)
    private String numeroCommande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

    @Column(name = "date_commande", nullable = false)
    private LocalDateTime dateCommande;

    @Column(name = "date_livraison_prevue")
    private LocalDate dateLivraisonPrevue;

    @Column(name = "date_livraison_effective")
    private LocalDate dateLivraisonEffective;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutCommande statut = StatutCommande.EN_ATTENTE;

    @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignesCommande = new ArrayList<>();


    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (dateCommande == null) {
            dateCommande = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addLigneCommande(LigneCommande ligne) {
        lignesCommande.add(ligne);
    }

    public void removeLigneCommande(LigneCommande ligne) {
        lignesCommande.remove(ligne);
        calculerMontantTotal();
    }

    public void calculerMontantTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LigneCommande ligne : this.lignesCommande) {
            if (ligne.getMontantLigneTotal() != null) {
                total = total.add(ligne.getMontantLigneTotal());
            }
        }
        this.montantTotal = total;
    }


}
