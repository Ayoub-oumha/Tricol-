package org.tricol.supplierchain.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.tricol.supplierchain.entity.Produit;

public class ProduitSpecification {




    public static Specification<Produit> hasCategory(String category){
        return ((root, query, criteriaBuilder) -> {
            if (category == null){
                return criteriaBuilder.conjunction(); // true
            }
            return criteriaBuilder.equal(root.get("categorie"), category);
        });
    }


    public static Specification<Produit> hasUnitMesure(String unitMesure){
        return ((root, query, criteriaBuilder) -> {
            if (unitMesure == null){
                return criteriaBuilder.conjunction(); // true
            }
            return criteriaBuilder.equal(root.get("uniteMesure"), unitMesure);
        });
    }


}
