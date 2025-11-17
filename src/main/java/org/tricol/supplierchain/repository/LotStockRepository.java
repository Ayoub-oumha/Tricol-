package org.tricol.supplierchain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tricol.supplierchain.entity.LotStock;
import org.tricol.supplierchain.enums.StatutLot;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LotStockRepository extends JpaRepository<LotStock, Long> {



    List<LotStock> findByProduitIdAndStatutOrderByDateEntreeAsc(Long produitId, StatutLot statut);

    List<LotStock> findByCommandeFournisseurId(Long commandeId);

    boolean existsByNumeroLot(String numeroLot);

    Long countLotStockByStatut(StatutLot statut);

    List<LotStock> findByStatut(StatutLot statut);

    List<LotStock> findByProduitIdOrderByDateEntreeAsc(Long produitId);

}
