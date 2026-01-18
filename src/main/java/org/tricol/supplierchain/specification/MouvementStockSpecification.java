package org.tricol.supplierchain.specification;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.tricol.supplierchain.entity.LotStock;
import org.tricol.supplierchain.entity.MouvementStock;
import org.tricol.supplierchain.entity.Produit;
import org.tricol.supplierchain.enums.TypeMouvement;

import java.time.LocalDateTime;

public class MouvementStockSpecification {

    public static Specification<MouvementStock> hasDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return (root, query, cb) -> {
            if (dateDebut != null && dateFin != null) {
                return cb.between(root.get("dateMouvement"), dateDebut, dateFin);
            } else if (dateDebut != null) {
                return cb.greaterThanOrEqualTo(root.get("dateMouvement"), dateDebut);
            } else if (dateFin != null) {
                return cb.lessThanOrEqualTo(root.get("dateMouvement"), dateFin);
            }
            return null;
        };
    }


    public static Specification<MouvementStock> hasProduitId(Long produitId) {
        return (root, query, cb) -> {
            if (produitId == null) return null;
            Join<MouvementStock, Produit> produitJoin = root.join("produit");
            return cb.equal(produitJoin.get("id"), produitId);
        };
    }


    public static Specification<MouvementStock> hasProduitReference(String reference) {
        return (root, query, cb) -> {
            if (reference == null || reference.isEmpty()) return null;
            Join<MouvementStock, Produit> produitJoin = root.join("produit");
            return cb.equal(produitJoin.get("reference"), reference);
        };
    }


    public static Specification<MouvementStock> hasTypeMouvement(TypeMouvement type) {
        return (root, query, cb) -> {
            if (type == null) return null;
            return cb.equal(root.get("typeMouvement"), type);
        };
    }


    public static Specification<MouvementStock> hasNumeroLot(String numeroLot) {
        return (root, query, cb) -> {
            if (numeroLot == null || numeroLot.isEmpty()) return null;
            Join<MouvementStock, LotStock> lotJoin = root.join("lotStock");
            return cb.equal(lotJoin.get("numeroLot"), numeroLot);
        };
    }
}
