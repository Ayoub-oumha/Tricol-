package org.tricol.supplierchain.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tricol.supplierchain.entity.LotStock;
import org.tricol.supplierchain.enums.StatutLot;
import org.tricol.supplierchain.mapper.LotStockMapper;
import org.tricol.supplierchain.mapper.MouvementStockMapper;
import org.tricol.supplierchain.mapper.StockMapper;
import org.tricol.supplierchain.repository.*;
import org.tricol.supplierchain.service.inter.CommandeFournisseurService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GestionStockImplTest {

    @InjectMocks
    GestionStockImpl gestionStock;

    @Mock
    StockMapper stockMapper;
    @Mock
    LotStockMapper lotStockMapper;
    @Mock
    MouvementStockMapper mouvementStockMapper;
    @Mock
    ProduitRepository produitRepository;
    @Mock
    LotStockRepository lotStockRepository;
    @Mock
    MouvementStockRepository mouvementStockRepository;
    @Mock
    CommandeFournisseurRepository commandeFournisseurRepository;
    @Mock
    FournisseurRepository fournisseurRepository;
    @Mock
    LigneCommandeRepository ligneCommandeRepository;




    @Test
    public void testGetValorisationTotale_multipleLotsDifferentPrices() {
        LotStock lot1 = LotStock.builder()
                .id(1L)
                .quantiteRestante(new BigDecimal("2"))
                .prixUnitaireAchat(new BigDecimal("5"))
                .dateEntree(LocalDateTime.now().minusDays(10))
                .build();

        LotStock lot2 = LotStock.builder()
                .id(2L)
                .quantiteRestante(new BigDecimal("3"))
                .prixUnitaireAchat(new BigDecimal("6"))
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        LotStock lot3 = LotStock.builder()
                .id(3L)
                .quantiteRestante(new BigDecimal("10"))
                .prixUnitaireAchat(new BigDecimal("7"))
                .dateEntree(LocalDateTime.now().minusDays(2))
                .build();

        when(lotStockRepository.findByStatut(StatutLot.ACTIF))
                .thenReturn(List.of(lot1, lot2, lot3));

        BigDecimal expected = new BigDecimal("98");

        BigDecimal actual = gestionStock.getValorisationTotale();

        assertThat(actual).isEqualByComparingTo(expected);
    }
}

