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
import org.tricol.supplierchain.entity.BonSortie;
import org.tricol.supplierchain.entity.LigneBonSortie;
import org.tricol.supplierchain.entity.LotStock;
import org.tricol.supplierchain.entity.MouvementStock;
import org.tricol.supplierchain.entity.Produit;
import org.tricol.supplierchain.enums.StatutBonSortie;
import org.tricol.supplierchain.enums.TypeMouvement;
import org.tricol.supplierchain.exception.BusinessException;
import org.tricol.supplierchain.mapper.BonSortieMapper;
import org.tricol.supplierchain.repository.BonSortieRepository;
import org.tricol.supplierchain.repository.LotStockRepository;
import org.tricol.supplierchain.repository.MouvementStockRepository;
import org.tricol.supplierchain.repository.ProduitRepository;
import org.tricol.supplierchain.service.inter.BonSortieService;
import org.tricol.supplierchain.service.inter.GestionStockService;
import org.tricol.supplierchain.service.BonSortieServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.tricol.supplierchain.enums.MotifBonSortie.PRODUCTION;


@ExtendWith(MockitoExtension.class)
public class BonSortieServiceTest {

    @InjectMocks
    BonSortieServiceImpl bonSortieService;

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
    @Mock
    GestionStockService gestionStockService;

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
                .motif(PRODUCTION)
                .build();
    }



    @Test
    @DisplayName("Scénario 1 : Sortie simple consommant partiellement un seul lot")
    public void testConsumationUnSeulLot() {


        LotStock lotStock = LotStock.builder()
                .id(1L)
                .quantiteRestante(new BigDecimal("20"))
                .prixUnitaireAchat(new BigDecimal("5"))
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        when(lotStockRepository.findByProduitIdOrderByDateEntreeAsc(1L))
                .thenReturn(List.of(lotStock));

        when(bonSortieMapper.toResponseDTO(any())).thenReturn(new BonSortieResponseDTO());

        BonSortieResponseDTO responseDTO = bonSortieService.performActualValidation(bonSortie);

        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        verify(mouvementStockRepository, times(1)).save(mouvementCaptor.capture());
        
        assertThat(responseDTO).isNotNull();
        assertThat(mouvementCaptor.getValue().getTypeMouvement()).isEqualTo(TypeMouvement.SORTIE);
        verify(lotStockRepository,times(1)).save(any(LotStock.class));
        verify(bonSortieRepository,times(1)).save(any(BonSortie.class));

        assertThat(lotStock.getQuantiteRestante()).isEqualTo(new BigDecimal("10"));
        assertThat(bonSortie.getMontantTotal()).isEqualTo(new BigDecimal("50"));
        assertThat(produit.getStockActuel()).isEqualTo(new BigDecimal("20"));



    }



    @Test
    @DisplayName("Scénario 2 : Sortie nécessitant la consommation de plusieurs lots successifs")
    public void testConsumationPlusieursLots() {

        LotStock lotStock1 = LotStock.builder()
                .id(1L)
                .quantiteRestante(new BigDecimal("2"))
                .prixUnitaireAchat(new BigDecimal("5"))
                .dateEntree(LocalDateTime.now().minusDays(10))
                .build();

        LotStock lotStock2 = LotStock.builder()
                .id(2L)
                .quantiteRestante(new BigDecimal("2"))
                .prixUnitaireAchat(new BigDecimal("6"))
                .dateEntree(LocalDateTime.now().minusDays(6))
                .build();

        LotStock lotStock3 = LotStock.builder()
                .id(3L)
                .quantiteRestante(new BigDecimal("10"))
                .prixUnitaireAchat(new BigDecimal("7"))
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        when(lotStockRepository.findByProduitIdOrderByDateEntreeAsc(1L))
                .thenReturn(List.of(lotStock1, lotStock2 , lotStock3));


        when(bonSortieMapper.toResponseDTO(any())).thenReturn(new BonSortieResponseDTO());

        BonSortieResponseDTO responseDTO = bonSortieService.performActualValidation(bonSortie);

        assertThat(responseDTO).isNotNull();
        verify(mouvementStockRepository, times(3)).save(any(MouvementStock.class));
        verify(lotStockRepository,times(3)).save(any(LotStock.class));
        verify(bonSortieRepository,times(1)).save(any(BonSortie.class));

        assertThat(lotStock1.getQuantiteRestante()).isEqualTo(BigDecimal.ZERO);
        assertThat(lotStock2.getQuantiteRestante()).isEqualTo(BigDecimal.ZERO);
        assertThat(lotStock3.getQuantiteRestante()).isEqualTo(new BigDecimal("4"));
        assertThat(bonSortie.getMontantTotal()).isEqualTo(new BigDecimal("64"));
        assertThat(produit.getStockActuel()).isEqualTo(new BigDecimal("20"));



    }


    @Test
    @DisplayName("Scénario 3 : Sortie avec stock insuffisant (gestion d'erreur)")
    public void testStockInsuffisant() {
        LotStock lotStock1 = LotStock.builder()
                .id(1L)
                .quantiteRestante(new BigDecimal("5"))
                .prixUnitaireAchat(new BigDecimal("5"))
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        LotStock lotStock2 = LotStock.builder()
                .id(2L)
                .quantiteRestante(new BigDecimal("3"))
                .prixUnitaireAchat(new BigDecimal("6"))
                .dateEntree(LocalDateTime.now().minusDays(3))
                .build();


        when(lotStockRepository.findByProduitIdOrderByDateEntreeAsc(1L))
                .thenReturn(List.of(lotStock1, lotStock2));

        assertThatThrownBy(() -> bonSortieService.performActualValidation(bonSortie))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock insuffisant pour le produit");

        verify(mouvementStockRepository, never()).save(any(MouvementStock.class));
        verify(lotStockRepository, never()).save(any(LotStock.class));
        verify(bonSortieRepository, never()).save(any(BonSortie.class));
        assertThat(produit.getStockActuel()).isEqualTo(new BigDecimal("30"));

    }


    @Test
    @DisplayName("Scénario 4 : Sortie épuisant exactement le stock disponible")
    public void testConsumationCompletLots() {


        LotStock lotStock1 = LotStock.builder()
                .id(1L)
                .quantiteRestante(new BigDecimal("9"))
                .prixUnitaireAchat(new BigDecimal("5"))
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        LotStock lotStock2 = LotStock.builder()
                .id(2L)
                .quantiteRestante(new BigDecimal("1"))
                .prixUnitaireAchat(new BigDecimal("10"))
                .dateEntree(LocalDateTime.now().minusDays(3))
                .build();

        when(lotStockRepository.findByProduitIdOrderByDateEntreeAsc(1L))
                .thenReturn(List.of(lotStock1, lotStock2));

        when(bonSortieMapper.toResponseDTO(any())).thenReturn(new BonSortieResponseDTO());

        BonSortieResponseDTO responseDTO = bonSortieService.performActualValidation(bonSortie);

        assertThat(responseDTO).isNotNull();
        verify(mouvementStockRepository, times(2)).save(any(MouvementStock.class));
        verify(lotStockRepository,times(2)).save(any(LotStock.class));
        verify(bonSortieRepository,times(1)).save(any(BonSortie.class));

        assertThat(lotStock1.getQuantiteRestante()).isEqualTo(BigDecimal.ZERO);
        assertThat(lotStock2.getQuantiteRestante()).isEqualTo(BigDecimal.ZERO);
        assertThat(bonSortie.getMontantTotal()).isEqualTo(new BigDecimal("55"));
        assertThat(produit.getStockActuel()).isEqualTo(new BigDecimal("20"));


    }


    @Test
    @DisplayName("Vérifier que la validation d'un bon de sortie (passage de BROUILLON à VALIDÉ) déclenche automatiquement")
    public void testStatusChange() {
        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));
        when(gestionStockService.verifyStockPourBonSortie(bonSortie)).thenReturn(List.of());
        when(bonSortieMapper.toResponseDTO(any())).thenReturn(new BonSortieResponseDTO());

        LotStock lotStock = LotStock.builder()
                .id(1L)
                .quantiteRestante(new BigDecimal("20"))
                .prixUnitaireAchat(new BigDecimal("5"))
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        when(lotStockRepository.findByProduitIdOrderByDateEntreeAsc(1L))
                .thenReturn(List.of(lotStock));

        BonSortieResponseDTO response = bonSortieService.validationBonSortie(1L);

        assertThat(response).isNotNull();
        assertThat(bonSortie.getStatut()).isEqualTo(StatutBonSortie.VALIDE);
        verify(mouvementStockRepository, times(1)).save(any(MouvementStock.class));
        verify(lotStockRepository, times(1)).save(any(LotStock.class));
        verify(bonSortieRepository, times(1)).save(any(BonSortie.class));

        assertThat(produit.getStockActuel()).isEqualTo(new BigDecimal("20"));

        assertThat(bonSortie.getMontantTotal()).isEqualTo(new BigDecimal("50"));
    }

    @Test
    @DisplayName("Vérifier la création du MouvementStock lors de la validation d'un bon de sortie")
    public void testCreationMouvementStockOnValidation() {

        LotStock lotStock = LotStock.builder()
                .id(1L)
                .quantiteRestante(new BigDecimal("20"))
                .prixUnitaireAchat(new BigDecimal("5"))
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        when(lotStockRepository.findByProduitIdOrderByDateEntreeAsc(1L))
                .thenReturn(List.of(lotStock));

        when(bonSortieMapper.toResponseDTO(any(BonSortie.class))).thenReturn(new BonSortieResponseDTO());

        bonSortieService.performActualValidation(bonSortie);

        ArgumentCaptor<MouvementStock> mvCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        verify(mouvementStockRepository, times(1)).save(mvCaptor.capture());
        MouvementStock savedMv = mvCaptor.getValue();

        assertThat(savedMv).isNotNull();
        assertThat(savedMv.getTypeMouvement()).isEqualTo(TypeMouvement.SORTIE);
        assertThat(savedMv.getQuantite()).isEqualTo(new BigDecimal("10"));
        assertThat(savedMv.getReference()).isEqualTo(bonSortie.getNumeroBon());
        assertThat(savedMv.getProduit()).isEqualTo(produit);
        assertThat(savedMv.getMotif()).isEqualTo(bonSortie.getMotif().name());
        assertThat(savedMv.getDateMouvement()).isNotNull();
    }

}
