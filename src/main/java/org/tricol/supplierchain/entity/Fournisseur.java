package org.tricol.supplierchain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fournisseurs")
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raison_sociale", nullable = false, length = 255)
    private String raisonSociale;

    @Column(nullable = false, length = 500)
    private String adresse;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(name = "personne_contact", nullable = false, length = 255)
    private String personneContact;

    @Column(nullable = false, length = 255 , unique = true)
    private String email;

    @Column(nullable = false)
    private String telephone;

    @Column(unique = true, nullable = false)
    private String ice;

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
