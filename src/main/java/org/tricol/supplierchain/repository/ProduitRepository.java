package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tricol.supplierchain.entity.Produit;


@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    boolean existsByReference(String reference);
}
