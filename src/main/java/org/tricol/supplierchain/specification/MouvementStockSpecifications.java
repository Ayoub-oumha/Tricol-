package org.tricol.supplierchain.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import org.tricol.supplierchain.dto.request.MouvementStockSearchCriteria;
import org.tricol.supplierchain.entity.LotStock;
import org.tricol.supplierchain.entity.MouvementStock;
import org.tricol.supplierchain.entity.Produit;
import org.tricol.supplierchain.enums.TypeMouvement;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MouvementStockSpecifications {

    public static Specification<MouvementStock> betweenDates(LocalDate dateDebut, LocalDate dateFin) {
        return (root, query, cb) -> {
            if (dateDebut == null && dateFin == null) {
                return cb.conjunction();
            }
            
            Path<LocalDateTime> datePath = root.get("dateMouvement");

            if (dateDebut != null && dateFin != null) {
                return cb.between(datePath, dateDebut.atStartOfDay(),
                        dateFin.atTime(23, 59, 59));
            } else if (dateDebut != null) {
                return cb.greaterThanOrEqualTo(datePath, dateDebut.atStartOfDay());
            } else {
                return cb.lessThanOrEqualTo(datePath, dateFin.atTime(23, 59, 59));
            }
        };
    }

    public static Specification<MouvementStock> hasProduit(Long produitId) {
        return (root, query, cb) -> {
            if (produitId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("produit").get("id"), produitId);
        };
    }

    public static Specification<MouvementStock> hasProduitReference(String reference) {
        return (root, query, cb) -> {
            if (reference == null || reference.trim().isEmpty()) {
                return cb.conjunction();
            }
            Join<MouvementStock, Produit> produitJoin = root.join("produit");
            return cb.like(cb.upper(produitJoin.get("reference")), "%" + reference.toUpperCase() + "%");
        };
    }

    public static Specification<MouvementStock> hasType(TypeMouvement type) {
        return (root, query, cb) -> {
            if (type == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("typeMouvement"), type);
        };
    }

    public static Specification<MouvementStock> hasNumeroLot(String numeroLot) {
        return (root, query, cb) -> {
            if (numeroLot == null || numeroLot.trim().isEmpty()) {
                return cb.conjunction();
            }
            Join<MouvementStock, LotStock> lotJoin = root.join("lotStock", JoinType.LEFT);
            return cb.like(cb.upper(lotJoin.get("numeroLot")), "%" + numeroLot.toUpperCase() + "%");
        };
    }

    public static Specification<MouvementStock> withCriteria(MouvementStockSearchCriteria criteria) {
        return Specification.allOf(
                betweenDates(criteria.getDateDebut(), criteria.getDateFin()),
                hasProduit(criteria.getProduitId()),
                hasProduitReference(criteria.getReference()),
                hasType(criteria.getType()),
                hasNumeroLot(criteria.getNumeroLot())
        );
    }
}