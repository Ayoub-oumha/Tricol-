package org.tricol.supplierchain.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.tricol.supplierchain.entity.BonSortie;
import org.tricol.supplierchain.entity.LigneBonSortie;
import org.tricol.supplierchain.entity.Produit;
import org.tricol.supplierchain.enums.StatutBonSortie;
import org.tricol.supplierchain.mapper.BonSortieMapper;
import org.tricol.supplierchain.repository.BonSortieRepository;
import org.tricol.supplierchain.repository.LotStockRepository;
import org.tricol.supplierchain.repository.MouvementStockRepository;
import org.tricol.supplierchain.repository.ProduitRepository;
import org.tricol.supplierchain.service.inter.BonSortieService;

import java.math.BigDecimal;
import java.util.List;


public class BonSortieServiceTest {

    @InjectMocks
    BonSortieService bonSortieService;

    @Mock
    LotStockRepository lotStockRepository;
    @Mock
    BonSortieRepository bonSortieRepository;
    @Mock
    ProduitRepository produitRepository;
    @Mock
    MouvementStockRepository mouvementStockRepository;
    @Mock
    BonSortieMapper bonSortieMapper;

    private Produit produit;
    private BonSortie bonSortie;
    private LigneBonSortie ligne;

    @BeforeEach
    public void setUp() {
        produit = Produit.builder()
                .id(1L)
                .nom("Paracétamol")
                .stockActuel(new BigDecimal("30"))
                .build();

        ligne = LigneBonSortie.builder()
                .produit(produit)
                .quantite(new BigDecimal("10"))
                .build();

        bonSortie = BonSortie.builder()
                .id(1L)
                .numeroBon("BS001")
                .statut(StatutBonSortie.BROUILLON)
                .ligneBonSorties(List.of(ligne))
                .build();


    }


}
