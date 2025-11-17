package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tricol.supplierchain.entity.CommandeFournisseur;

import java.util.List;

@Repository
public interface CommandeFournisseurRepository extends JpaRepository<CommandeFournisseur, Long> {

    @Query("SELECT c FROM CommandeFournisseur c LEFT JOIN FETCH c.lignesCommande WHERE c.fournisseur.id = :fournisseurId")
    List<CommandeFournisseur> findBySupplierIdWithRelationship(@Param("fournisseurId") Long fournisseurId);

    @Query("SELECT DISTINCT c FROM CommandeFournisseur c JOIN c.lignesCommande l WHERE l.produit.id = :produitId")
    List<CommandeFournisseur> findCommandesAvecProduit(@Param("produitId") Long produitId);


}
