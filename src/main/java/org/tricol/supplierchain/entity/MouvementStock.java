package org.tricol.supplierchain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.tricol.supplierchain.enums.TypeMouvement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mouvements_stock")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_stock_id")
    private LotStock lotStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMouvement typeMouvement;

    @Column(nullable = false)
    private BigDecimal quantite;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime dateMouvement;

    @Column(name = "code_reference", nullable = false)
    private String reference;

    @Column(nullable = false)
    private String motif;


}
