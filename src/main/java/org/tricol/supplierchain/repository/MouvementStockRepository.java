package org.tricol.supplierchain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.tricol.supplierchain.entity.MouvementStock;
import org.tricol.supplierchain.enums.TypeMouvement;

import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long>{

    List<MouvementStock> findByProduitIdOrderByDateMouvementDesc(Long produitId);

    List<MouvementStock> findByTypeMouvementOrderByDateMouvementDesc(TypeMouvement type);

    List<MouvementStock> findByReferenceOrderByDateMouvementDesc(String reference);



}
