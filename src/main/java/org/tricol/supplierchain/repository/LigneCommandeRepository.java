package org.tricol.supplierchain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tricol.supplierchain.entity.LigneCommande;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
    boolean existsByProduitId(Long produitId);


    @Query("SELECT l.prixUnitaire FROM LigneCommande l WHERE l.produit.id = :produitId ORDER BY l.commande.dateCommande DESC LIMIT 1")
    Optional<BigDecimal> findDernierPrixAchat(@Param("produitId") Long produitId, @Param("fournisseurId") Long fournisseurId);

}
