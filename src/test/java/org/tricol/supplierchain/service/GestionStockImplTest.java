package org.tricol.supplierchain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tricol.supplierchain.dto.response.DeficitStockResponseDTO;
import org.tricol.supplierchain.dto.response.StockProduitResponseDTO;
import org.tricol.supplierchain.entity.*;
import org.tricol.supplierchain.enums.StatutLot;
import org.tricol.supplierchain.exception.ResourceNotFoundException;
import org.tricol.supplierchain.mapper.LotStockMapper;
import org.tricol.supplierchain.mapper.MouvementStockMapper;
import org.tricol.supplierchain.mapper.StockMapper;
import org.tricol.supplierchain.repository.*;
import org.tricol.supplierchain.service.inter.CommandeFournisseurService;

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
class GestionStockImplTest {

    @Mock
    private StockMapper stockMapper;
    
    @Mock
    private LotStockMapper lotStockMapper;
    
    @Mock
    private MouvementStockMapper mouvementStockMapper;
    
    @Mock
    private ProduitRepository produitRepository;
    
    @Mock
    private LotStockRepository lotStockRepository;
    
    @Mock
    private MouvementStockRepository mouvementStockRepository;
    
    @Mock
    private CommandeFournisseurService commandeFournisseurService;
    
    @Mock
    private CommandeFournisseurRepository commandeFournisseurRepository;
    
    @Mock
    private FournisseurRepository fournisseurRepository;
    
    @Mock
    private LigneCommandeRepository ligneCommandeRepository;
    
    @InjectMocks
    private GestionStockImpl gestionStockService;

    private Produit produit;
    private LotStock lotStock1;
    private LotStock lotStock2;
    private LotStock lotStock3;

    @BeforeEach
    void setUp() {
        produit = new Produit();
        produit.setId(1L);
        produit.setReference("PROD-001");
        produit.setStockActuel(new BigDecimal("150.00"));
        produit.setPointCommande(new BigDecimal("50.00"));

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
    @DisplayName("Calcul de valorisation du stock avec plusieurs lots a prix differents")
    void testCalculerValorisationTotale_AvecPlusieursLotsAPrixDifferents() {
        List<LotStock> lots = Arrays.asList(lotStock1, lotStock2, lotStock3);
        when(lotStockRepository.findByStatut(StatutLot.ACTIF)).thenReturn(lots);

        BigDecimal valorisation = gestionStockService.getValorisationTotale();

        BigDecimal expectedValorisation = lotStock1.getQuantiteRestante().multiply(lotStock1.getPrixUnitaireAchat())
                .add(lotStock2.getQuantiteRestante().multiply(lotStock2.getPrixUnitaireAchat()))
                .add(lotStock3.getQuantiteRestante().multiply(lotStock3.getPrixUnitaireAchat()));

        assertNotNull(valorisation);
        assertEquals(0, expectedValorisation.compareTo(valorisation));
        verify(lotStockRepository, times(1)).findByStatut(StatutLot.ACTIF);
    }

    @Test
    @DisplayName("Calcul de valorisation avec stock vide")
    void testCalculerValorisationTotale_AvecStockVide() {
        when(lotStockRepository.findByStatut(StatutLot.ACTIF)).thenReturn(new ArrayList<>());

        BigDecimal valorisation = gestionStockService.getValorisationTotale();

        assertNotNull(valorisation);
        assertEquals(0, BigDecimal.ZERO.compareTo(valorisation));
        verify(lotStockRepository, times(1)).findByStatut(StatutLot.ACTIF);
    }

    @Test
    @DisplayName("Obtenir la quantite disponible pour un produit")
    void testGetQuantiteDisponible_ProduitExiste() {
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        BigDecimal quantite = gestionStockService.getQuantiteDisponible(1L);

        assertNotNull(quantite);
        assertEquals(0, produit.getStockActuel().compareTo(quantite));
        verify(produitRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Obtenir la quantite disponible pour un produit inexistant")
    void testGetQuantiteDisponible_ProduitInexistant() {
        when(produitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            gestionStockService.getQuantiteDisponible(999L);
        });

        verify(produitRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Verification du stock pour bon de sortie - Stock suffisant")
    void testVerifyStockPourBonSortie_StockSuffisant() {
        BonSortie bonSortie = new BonSortie();
        LigneBonSortie ligne = new LigneBonSortie();
        ligne.setProduit(produit);
        ligne.setQuantite(new BigDecimal("100.00"));
        bonSortie.setLigneBonSorties(Arrays.asList(ligne));

        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        List<DeficitStockResponseDTO> deficits = gestionStockService.verifyStockPourBonSortie(bonSortie);

        assertTrue(deficits.isEmpty());
        verify(produitRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Verification du stock pour bon de sortie - Stock insuffisant")
    void testVerifyStockPourBonSortie_StockInsuffisant() {
        BonSortie bonSortie = new BonSortie();
        bonSortie.setNumeroBon("BS-001");
        
        LigneBonSortie ligne = new LigneBonSortie();
        ligne.setProduit(produit);
        ligne.setQuantite(new BigDecimal("200.00"));
        bonSortie.setLigneBonSorties(Arrays.asList(ligne));

        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(1L);
        fournisseur.setRaisonSociale("Fournisseur Test");

        CommandeFournisseur commande = new CommandeFournisseur();
        commande.setId(1L);
        commande.setFournisseur(fournisseur);

        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(commandeFournisseurRepository.findCommandesAvecProduit(1L))
                .thenReturn(Arrays.asList(commande));

        List<DeficitStockResponseDTO> deficits = gestionStockService.verifyStockPourBonSortie(bonSortie);

        assertFalse(deficits.isEmpty());
        assertEquals(1, deficits.size());
        
        DeficitStockResponseDTO deficit = deficits.get(0);
        assertEquals(produit.getId(), deficit.getProduitId());
        assertEquals(new BigDecimal("200.00"), deficit.getQuantiteDemandee());
        assertEquals(produit.getStockActuel(), deficit.getQuantiteDisponible());
        assertEquals(new BigDecimal("50.00"), deficit.getQuantiteManquante());

        verify(produitRepository, times(1)).findById(1L);
        verify(commandeFournisseurRepository, times(1)).findCommandesAvecProduit(1L);
    }


}