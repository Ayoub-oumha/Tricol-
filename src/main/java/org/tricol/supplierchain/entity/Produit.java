
package org.tricol.supplierchain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "produits")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @Column(nullable = false)
    private String nom;

    @Lob
    private String description;


    @Column(name = "stock_actuel", nullable = false)
    private BigDecimal stockActuel;

    @Column(name = "point_commande", nullable = false)
    private BigDecimal pointCommande;

    @Column(name = "unite_mesure", nullable = false)
    private String uniteMesure;

    @Column(nullable = false, length = 100)
    private String categorie;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime dateModification;

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
