package org.tricol.supplierchain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tricol.supplierchain.dto.response.BonSortieResponseDTO;
import org.tricol.supplierchain.dto.response.DeficitStockResponseDTO;
import org.tricol.supplierchain.entity.*;
import org.tricol.supplierchain.enums.Atelier;
import org.tricol.supplierchain.enums.MotifBonSortie;
import org.tricol.supplierchain.enums.StatutBonSortie;
import org.tricol.supplierchain.enums.StatutLot;
import org.tricol.supplierchain.enums.TypeMouvement;
import org.tricol.supplierchain.exception.BusinessException;
import org.tricol.supplierchain.exception.StockInsuffisantException;
import org.tricol.supplierchain.mapper.BonSortieMapper;
import org.tricol.supplierchain.repository.BonSortieRepository;
import org.tricol.supplierchain.repository.LotStockRepository;
import org.tricol.supplierchain.repository.MouvementStockRepository;
import org.tricol.supplierchain.repository.ProduitRepository;
import org.tricol.supplierchain.service.inter.GestionStockService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BonSortieServiceImplTest {

    @Mock
    private BonSortieRepository bonSortieRepository;
    
    @Mock
    private ProduitRepository produitRepository;
    
    @Mock
    private LotStockRepository lotStockRepository;
    
    @Mock
    private MouvementStockRepository mouvementStockRepository;
    
    @Mock
    private BonSortieMapper bonSortieMapper;
    
    @Mock
    private GestionStockService gestionStockService;
    
    @InjectMocks
    private BonSortieServiceImpl bonSortieService;

    private Produit produit;
    private BonSortie bonSortie;
    private LotStock lotStock1;
    private LotStock lotStock2;
    private LotStock lotStock3;

    @BeforeEach
    void setUp() {
        produit = Produit
                .builder()
                .id(1L)
                .reference("PROD-001")
                .stockActuel(BigDecimal.valueOf(150))
                .build();

        bonSortie = BonSortie
                .builder()
                .id(1L)
                .numeroBon("BS-001")
                .motif(MotifBonSortie.PRODUCTION)
                .statut(StatutBonSortie.BROUILLON)
                .atelier(Atelier.ATELIER1)
                .build();

        LigneBonSortie ligne = LigneBonSortie
                .builder()
                .produit(produit)
                .quantite(new BigDecimal("100.00"))
                .bonSortie(bonSortie)
                .build();
        bonSortie.setLigneBonSorties(Arrays.asList(ligne));

        lotStock1 = LotStock.builder()
                .id(1L)
                .numeroLot("LOT-001")
                .produit(produit)
                .quantiteInitiale(new BigDecimal("100.00"))
                .quantiteRestante(new BigDecimal("50.00"))
                .prixUnitaireAchat(new BigDecimal("10.00"))
                .statut(StatutLot.ACTIF)
                .dateEntree(LocalDateTime.now().minusDays(10))
                .build();

        lotStock2 = LotStock.builder()
                .id(2L)
                .numeroLot("LOT-002")
                .produit(produit)
                .quantiteInitiale(new BigDecimal("80.00"))
                .quantiteRestante(new BigDecimal("80.00"))
                .prixUnitaireAchat(new BigDecimal("12.00"))
                .statut(StatutLot.ACTIF)
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        lotStock3 = LotStock.builder()
                .id(3L)
                .numeroLot("LOT-003")
                .produit(produit)
                .quantiteInitiale(new BigDecimal("50.00"))
                .quantiteRestante(new BigDecimal("20.00"))
                .prixUnitaireAchat(new BigDecimal("11.00"))
                .statut(StatutLot.ACTIF)
                .dateEntree(LocalDateTime.now().minusDays(8))
                .build();
    }

    @Test
    @DisplayName("Sortie simple consommant partiellement un seul lot")
    void testFIFO_SortieSimplePartielle() {
        LigneBonSortie ligne = bonSortie.getLigneBonSorties().get(0);
        ligne.setQuantite(new BigDecimal("30.00"));

        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));
        when(gestionStockService.verifyStockPourBonSortie(any(BonSortie.class))).thenReturn(new ArrayList<>());
        when(lotStockRepository.findByProduitIdOrderByDateEntreeAsc(1L)).thenReturn(Arrays.asList(lotStock1, lotStock3, lotStock2));
        when(bonSortieRepository.save(any(BonSortie.class))).thenReturn(bonSortie);
        when(bonSortieMapper.toResponseDTO(any(BonSortie.class))).thenReturn(BonSortieResponseDTO.builder().build());

        BonSortieResponseDTO result = bonSortieService.validationBonSortie(1L);

        assertNotNull(result);

        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        verify(mouvementStockRepository, times(1)).save(mouvementCaptor.capture());
        MouvementStock mouvement = mouvementCaptor.getValue();
        assertEquals(TypeMouvement.SORTIE, mouvement.getTypeMouvement());
        assertEquals(new BigDecimal("30.00"), mouvement.getQuantite());
        assertEquals(lotStock1.getId(), mouvement.getLotStock().getId());

        verify(lotStockRepository, times(1)).save(any(LotStock.class));
        verify(produitRepository, times(1)).save(any(Produit.class));
    }

    @Test
    @DisplayName("Sortie necessitant la consommation de plusieurs lots successifs")
    void testFIFO_SortieMultiplesLots() {
        LigneBonSortie ligne = bonSortie.getLigneBonSorties().get(0);
        ligne.setQuantite(new BigDecimal("100.00"));

        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));
        when(gestionStockService.verifyStockPourBonSortie(any())).thenReturn(new ArrayList<>());
        when(lotStockRepository.findByProduitIdOrderByDateEntreeAsc(1L)).thenReturn(Arrays.asList(lotStock1, lotStock3, lotStock2));
        when(bonSortieRepository.save(any(BonSortie.class))).thenReturn(bonSortie);
        when(bonSortieMapper.toResponseDTO(any(BonSortie.class))).thenReturn(BonSortieResponseDTO.builder().build());

        BonSortieResponseDTO result = bonSortieService.validationBonSortie(1L);

        assertNotNull(result);
        
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        verify(mouvementStockRepository, times(3)).save(mouvementCaptor.capture());
        
        List<MouvementStock> mouvements = mouvementCaptor.getAllValues();
        assertEquals(3, mouvements.size());
        assertEquals(new BigDecimal("50.00"), mouvements.get(0).getQuantite());
        assertEquals(new BigDecimal("0.00"),mouvements.get(0).getLotStock().getQuantiteRestante());
        assertEquals(new BigDecimal("20.00"), mouvements.get(1).getQuantite());
        assertEquals(new BigDecimal("0.00"),mouvements.get(1).getLotStock().getQuantiteRestante());
        assertEquals(new BigDecimal("30.00"), mouvements.get(2).getQuantite());
        assertEquals(new BigDecimal("50.00"),mouvements.get(2).getLotStock().getQuantiteRestante());

        verify(lotStockRepository, times(3)).save(any(LotStock.class));
        verify(produitRepository, times(1)).save(any(Produit.class));
    }

    @Test
    @DisplayName("Sortie avec stock insuffisant")
    void testFIFO_StockInsuffisant() {
        LigneBonSortie ligne = bonSortie.getLigneBonSorties().get(0);
        ligne.setQuantite(new BigDecimal("200.00"));

        DeficitStockResponseDTO deficit = DeficitStockResponseDTO
                .builder()
                .produitId(1L)
                .quantiteDemandee(new BigDecimal("200.00"))
                .quantiteDisponible(new BigDecimal("150.00"))
                .quantiteManquante(new BigDecimal("50.00"))
                .build();

        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));
        when(gestionStockService.verifyStockPourBonSortie(any())).thenReturn(Arrays.asList(deficit));

        assertThrows(StockInsuffisantException.class,
                () -> bonSortieService.validationBonSortie(1L)
        );
        
        verifyNoInteractions(mouvementStockRepository);
        verifyNoInteractions(lotStockRepository);

    }

    @Test
    @DisplayName("Sortie epuisant exactement le stock disponible")
    void testFIFO_SortieExacte() {
        LigneBonSortie ligne = bonSortie.getLigneBonSorties().get(0);
        ligne.setQuantite(new BigDecimal("150.00"));

        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));
        when(gestionStockService.verifyStockPourBonSortie(any())).thenReturn(new ArrayList<>());
        when(lotStockRepository.findByProduitIdOrderByDateEntreeAsc(1L)).thenReturn(Arrays.asList(lotStock1, lotStock3, lotStock2));
        when(bonSortieRepository.save(any(BonSortie.class))).thenReturn(bonSortie);
        when(bonSortieMapper.toResponseDTO(any(BonSortie.class))).thenReturn(BonSortieResponseDTO.builder().build());

        BonSortieResponseDTO result = bonSortieService.validationBonSortie(1L);

        assertNotNull(result);
        
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        verify(mouvementStockRepository, times(3)).save(mouvementCaptor.capture());

        List<MouvementStock> mouvements = mouvementCaptor.getAllValues();
        BigDecimal totalSortie = mouvements.stream()
                .map(MouvementStock::getQuantite)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        assertEquals(new BigDecimal("150.00"), totalSortie);
        verify(lotStockRepository, times(3)).save(any(LotStock.class));
        verify(produitRepository, times(1)).save(any(Produit.class));
    }

    @Test
    @DisplayName("Validation bon de sortie BROUILLON vers VALIDE avec creation de mouvements")
    void testTransitionBrouillonVersValide_CreationMouvements() {
        LigneBonSortie ligne = bonSortie.getLigneBonSorties().get(0);
        ligne.setQuantite(new BigDecimal("50.00"));

        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));
        when(gestionStockService.verifyStockPourBonSortie(any())).thenReturn(new ArrayList<>());
        when(lotStockRepository.findByProduitIdOrderByDateEntreeAsc(1L)).thenReturn(Arrays.asList(lotStock1, lotStock3, lotStock2));
        when(bonSortieRepository.save(any(BonSortie.class))).thenAnswer(invocation -> {
            BonSortie bs = invocation.getArgument(0);
            assertEquals(StatutBonSortie.VALIDE, bs.getStatut());
            return bs;
        });
        when(bonSortieMapper.toResponseDTO(any(BonSortie.class))).thenReturn(BonSortieResponseDTO.builder().build());

        BonSortieResponseDTO result = bonSortieService.validationBonSortie(1L);

        assertNotNull(result);
        verify(bonSortieRepository, times(1)).save(argThat(bs -> 
            bs.getStatut() == StatutBonSortie.VALIDE
        ));
        verify(mouvementStockRepository, atLeastOnce()).save(any(MouvementStock.class));
    }

    @Test
    @DisplayName("Impossible de valider un bon de sortie deja valide")
    void testTransitionImpossible_BonSortieDejaValide() {
        bonSortie.setStatut(StatutBonSortie.VALIDE);

        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));

        assertThrows(BusinessException.class, () -> {
            bonSortieService.validationBonSortie(1L);
        });

        verify(mouvementStockRepository, never()).save(any(MouvementStock.class));
        verify(lotStockRepository, never()).save(any(LotStock.class));
    }
}