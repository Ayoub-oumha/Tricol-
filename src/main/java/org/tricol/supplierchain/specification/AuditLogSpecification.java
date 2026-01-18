package org.tricol.supplierchain.specification;

import org.springframework.data.jpa.domain.Specification;
import org.tricol.supplierchain.dto.request.AuditLogSearchCriteria;
import org.tricol.supplierchain.entity.AuditLog;

import java.time.LocalDateTime;

public class AuditLogSpecification {

    public static Specification<AuditLog> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("userId"), userId);
        };
    }

    public static Specification<AuditLog> hasResource(String resource) {
        return (root, query, criteriaBuilder) -> {
            if (resource == null || resource.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("resource"), resource);
        };
    }

    public static Specification<AuditLog> hasAction(String action) {
        return (root, query, criteriaBuilder) -> {
            if (action == null || action.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("action"), action);
        };
    }

    public static Specification<AuditLog> hasActionTimestampAfter(LocalDateTime dateStart) {
        return (root, query, criteriaBuilder) -> {
            if (dateStart == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("actionTimestamp"), dateStart);
        };
    }

    public static Specification<AuditLog> hasActionTimestampBefore(LocalDateTime dateEnd) {
        return (root, query, criteriaBuilder) -> {
            if (dateEnd == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("actionTimestamp"), dateEnd);
        };
    }

    public static Specification<AuditLog> hasActionTimestampBetween(LocalDateTime dateStart, LocalDateTime dateEnd) {
        return (root, query, criteriaBuilder) -> {
            if (dateStart == null && dateEnd == null) {
                return criteriaBuilder.conjunction();
            }
            
            if (dateStart != null && dateEnd != null) {
                return criteriaBuilder.between(root.get("actionTimestamp"), dateStart, dateEnd);
            } else if (dateStart != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("actionTimestamp"), dateStart);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("actionTimestamp"), dateEnd);
            }
        };
    }

    public static Specification<AuditLog> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            if (username == null || username.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.upper(root.get("username")),
                "%" + username.toUpperCase() + "%"
            );
        };
    }


    public static Specification<AuditLog> withSearchCriteria(AuditLogSearchCriteria criteria) {
        return Specification.allOf(
                hasUserId(criteria.getUserId()),
                hasResource(criteria.getResource()),
                hasAction(criteria.getAction()),
                hasActionTimestampBetween(criteria.getDateStart(), criteria.getDateEnd())
        );
    }
}
