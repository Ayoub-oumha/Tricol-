package org.tricol.supplierchain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tricol.supplierchain.dto.response.CommandeFournisseurResponseDTO;
import org.tricol.supplierchain.entity.*;
import org.tricol.supplierchain.enums.StatutCommande;
import org.tricol.supplierchain.enums.StatutLot;
import org.tricol.supplierchain.enums.TypeMouvement;
import org.tricol.supplierchain.exception.BusinessException;
import org.tricol.supplierchain.exception.ResourceNotFoundException;
import org.tricol.supplierchain.mapper.CommandeFournisseurMapper;
import org.tricol.supplierchain.mapper.LigneCommandeMapper;
import org.tricol.supplierchain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeFournisseurServiceImplTest {

    @Mock
    private CommandeFournisseurRepository commandeFournisseurRepository;
    
    @Mock
    private FournisseurRepository fournisseurRepository;
    
    @Mock
    private LigneCommandeRepository ligneCommandeRepository;
    
    @Mock
    private ProduitRepository produitRepository;
    
    @Mock
    private LotStockRepository lotStockRepository;
    
    @Mock
    private MouvementStockRepository mouvementStockRepository;
    
    @Mock
    private CommandeFournisseurMapper commandeFournisseurMapper;
    
    @Mock
    private LigneCommandeMapper ligneCommandeMapper;
    
    @InjectMocks
    private CommandeFournisseurServiceimpl commandeFournisseurService;

    private CommandeFournisseur commande;
    private Fournisseur fournisseur;
    private Produit produit1;
    private Produit produit2;
    private LigneCommande ligneCommande1;
    private LigneCommande ligneCommande2;

    @BeforeEach
    void setUp() {
        fournisseur = new Fournisseur();
        fournisseur.setId(1L);
        fournisseur.setRaisonSociale("Fournisseur Test");

        produit1 = new Produit();
        produit1.setId(1L);
        produit1.setNom("Produit 1");
        produit1.setReference("PROD-001");
        produit1.setStockActuel(BigDecimal.ZERO);

        produit2 = new Produit();
        produit2.setId(2L);
        produit2.setNom("Produit 2");
        produit2.setReference("PROD-002");
        produit2.setStockActuel(BigDecimal.ZERO);

        commande = new CommandeFournisseur();
        commande.setId(1L);
        commande.setNumeroCommande("CMD-001");
        commande.setFournisseur(fournisseur);
        commande.setStatut(StatutCommande.VALIDEE);
        commande.setDateLivraisonPrevue(LocalDate.now().plusDays(7));

        ligneCommande1 = LigneCommande.builder()
                .id(1L)
                .commande(commande)
                .produit(produit1)
                .quantite(new BigDecimal("50.00"))
                .prixUnitaire(new BigDecimal("10.00"))
                .montantLigneTotal(new BigDecimal("500.00"))
                .build();

        ligneCommande2 = LigneCommande.builder()
                .id(2L)
                .commande(commande)
                .produit(produit2)
                .quantite(new BigDecimal("30.00"))
                .prixUnitaire(new BigDecimal("15.00"))
                .montantLigneTotal(new BigDecimal("450.00"))
                .build();

        commande.setLignesCommande(Arrays.asList(ligneCommande1, ligneCommande2));
    }

    @Test
    @DisplayName("Creation automatique de lot lors de la reception d une commande")
    void testReceiveCommande_CreationAutomatiqueLot() {
        when(commandeFournisseurRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeFournisseurRepository.save(any(CommandeFournisseur.class))).thenReturn(commande);
        when(lotStockRepository.save(any(LotStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mouvementStockRepository.save(any(MouvementStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(produitRepository.save(any(Produit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commandeFournisseurMapper.toResponseDto(any(CommandeFournisseur.class))).thenReturn(new CommandeFournisseurResponseDTO());

        CommandeFournisseurResponseDTO result = commandeFournisseurService.receiveCommande(1L);

        assertNotNull(result);

        ArgumentCaptor<LotStock> lotCaptor = ArgumentCaptor.forClass(LotStock.class);
        verify(lotStockRepository, times(2)).save(lotCaptor.capture());
        
        List<LotStock> lots = lotCaptor.getAllValues();
        assertEquals(2, lots.size());

        LotStock lot1 = lots.get(0);
        assertNotNull(lot1.getNumeroLot());
        assertTrue(lot1.getNumeroLot().startsWith("LOT-"));
        assertEquals(produit1, lot1.getProduit());
        assertEquals(commande, lot1.getCommande());
        assertEquals(new BigDecimal("50.00"), lot1.getQuantiteInitiale());
        assertEquals(new BigDecimal("50.00"), lot1.getQuantiteRestante());
        assertEquals(new BigDecimal("10.00"), lot1.getPrixUnitaireAchat());
        assertEquals(StatutLot.ACTIF, lot1.getStatut());
        assertNotNull(lot1.getDateEntree());

        LotStock lot2 = lots.get(1);
        assertNotNull(lot2.getNumeroLot());
        assertTrue(lot2.getNumeroLot().startsWith("LOT-"));
        assertEquals(produit2, lot2.getProduit());
        assertEquals(new BigDecimal("30.00"), lot2.getQuantiteInitiale());
        assertEquals(new BigDecimal("30.00"), lot2.getQuantiteRestante());
        assertEquals(new BigDecimal("15.00"), lot2.getPrixUnitaireAchat());
        assertEquals(StatutLot.ACTIF, lot2.getStatut());
        assertNotNull(lot1.getDateEntree());
    }

    @Test
    @DisplayName("Verification du lien entre lot cree et commande fournisseur")
    void testReceiveCommande_LienLotCommande() {
        when(commandeFournisseurRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeFournisseurRepository.save(any(CommandeFournisseur.class))).thenReturn(commande);
        when(lotStockRepository.save(any(LotStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mouvementStockRepository.save(any(MouvementStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(produitRepository.save(any(Produit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commandeFournisseurMapper.toResponseDto(any(CommandeFournisseur.class)))
                .thenReturn(new CommandeFournisseurResponseDTO());

        commandeFournisseurService.receiveCommande(1L);

        ArgumentCaptor<LotStock> lotCaptor = ArgumentCaptor.forClass(LotStock.class);
        verify(lotStockRepository, times(2)).save(lotCaptor.capture());

        lotCaptor.getAllValues().forEach(lot -> {
            assertNotNull(lot.getCommande());
            assertEquals(commande.getId(), lot.getCommande().getId());
            assertEquals(commande.getNumeroCommande(), lot.getCommande().getNumeroCommande());
        });
    }

    @Test
    @DisplayName("Reception commande - Validation avec creation automatique lots et mouvements")
    void testReceiveCommande_ValidationCompleteAvecLotsEtMouvements() {

        commande.setStatut(StatutCommande.VALIDEE);

        when(commandeFournisseurRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeFournisseurRepository.save(any(CommandeFournisseur.class))).thenReturn(commande);
        when(lotStockRepository.save(any(LotStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mouvementStockRepository.save(any(MouvementStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(produitRepository.save(any(Produit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commandeFournisseurMapper.toResponseDto(any(CommandeFournisseur.class))).thenReturn(new CommandeFournisseurResponseDTO());

        CommandeFournisseurResponseDTO result = commandeFournisseurService.receiveCommande(1L);

        ArgumentCaptor<CommandeFournisseur> commandeCaptor = ArgumentCaptor.forClass(CommandeFournisseur.class);
        verify(commandeFournisseurRepository).save(commandeCaptor.capture());
        assertEquals(StatutCommande.LIVREE, commandeCaptor.getValue().getStatut());
        assertNotNull(commandeCaptor.getValue().getDateLivraisonEffective());


        ArgumentCaptor<LotStock> lotCaptor = ArgumentCaptor.forClass(LotStock.class);
        verify(lotStockRepository, times(2)).save(lotCaptor.capture());

        List<LotStock> lots = lotCaptor.getAllValues();
        assertEquals(2, lots.size());


        LotStock lot1 = lots.stream()
                .filter(lot -> lot.getProduit().equals(produit1))
                .findFirst().orElseThrow();
        assertTrue(lot1.getNumeroLot().startsWith("LOT-"));
        assertEquals(new BigDecimal("50.00"), lot1.getQuantiteInitiale());
        assertEquals(StatutLot.ACTIF, lot1.getStatut());
        assertEquals(commande, lot1.getCommande());

        LotStock lot2 = lots.stream()
                .filter(lot -> lot.getProduit().equals(produit2))
                .findFirst().orElseThrow();
        assertTrue(lot2.getNumeroLot().startsWith("LOT-"));
        assertEquals(new BigDecimal("30.00"), lot2.getQuantiteInitiale());
        assertEquals(StatutLot.ACTIF, lot2.getStatut());
        assertEquals(commande, lot2.getCommande());

        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        verify(mouvementStockRepository, times(2)).save(mouvementCaptor.capture());

        List<MouvementStock> mouvements = mouvementCaptor.getAllValues();
        assertEquals(2, mouvements.size());

        MouvementStock mouvement1 = mouvements.stream()
                .filter(mvt -> mvt.getProduit().equals(produit1))
                .findFirst().orElseThrow();
        assertEquals(TypeMouvement.ENTREE, mouvement1.getTypeMouvement());
        assertEquals("RECEPTION_COMMANDE", mouvement1.getMotif());
        assertEquals(commande.getNumeroCommande(), mouvement1.getReference());
        assertEquals(new BigDecimal("50.00"), mouvement1.getQuantite());

        MouvementStock mouvement2 = mouvements.stream()
                .filter(mvt -> mvt.getProduit().equals(produit2))
                .findFirst().orElseThrow();
        assertEquals(TypeMouvement.ENTREE, mouvement2.getTypeMouvement());
        assertEquals("RECEPTION_COMMANDE", mouvement2.getMotif());
        assertEquals(commande.getNumeroCommande(), mouvement2.getReference());
        assertEquals(new BigDecimal("30.00"), mouvement2.getQuantite());


        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository, times(2)).save(produitCaptor.capture());

        assertEquals(lot1.getProduit().getStockActuel(), new BigDecimal("50.00"));
        assertEquals(lot2.getProduit().getStockActuel(), new BigDecimal("30.00"));

        assertNotNull(result);
    }

}
