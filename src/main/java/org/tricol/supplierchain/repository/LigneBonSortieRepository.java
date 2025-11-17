package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tricol.supplierchain.entity.LigneBonSortie;

@Repository
public interface LigneBonSortieRepository extends JpaRepository<LigneBonSortie, Long> {
    boolean existsByProduitId(Long produitId);
}

