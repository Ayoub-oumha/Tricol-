package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tricol.supplierchain.entity.BonSortie;
import org.tricol.supplierchain.enums.Atelier;

import java.util.List;


@Repository
public interface BonSortieRepository extends JpaRepository<BonSortie, Long> {

    List<BonSortie> findByAtelier(Atelier atelier);
}
