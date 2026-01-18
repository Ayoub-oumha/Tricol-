// java
package org.tricol.supplierchain.service;

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
import org.tricol.supplierchain.mapper.CommandeFournisseurMapper;
import org.tricol.supplierchain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeFournisseurServiceTest {

    @Mock
    CommandeFournisseurRepository commandeFournisseurRepository;
    @Mock
    LotStockRepository lotStockRepository;
    @Mock
    MouvementStockRepository mouvementStockRepository;
    @Mock
    ProduitRepository produitRepository;
    @Mock
    CommandeFournisseurMapper commandeFournisseurMapper;

    @InjectMocks
    CommandeFournisseurServiceimpl service;

    @Test
    void receiveCommande() {
        LocalDateTime before = LocalDateTime.now();

        Produit produit = Produit.builder()
                .id(1L)
                .stockActuel(new BigDecimal("20"))
                .build();


        LigneCommande ligne = LigneCommande.builder()
                .produit(produit)
                .quantite(new BigDecimal("50"))
                .prixUnitaire(new BigDecimal("12"))
                .build();


        CommandeFournisseur commande = CommandeFournisseur.builder()
                .id(1L)
                .numeroCommande("CMD-001")
                .statut(StatutCommande.VALIDEE)
                .lignesCommande(List.of(ligne))
                .build();

        when(commandeFournisseurRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeFournisseurMapper.toResponseDto(any())).thenReturn(new CommandeFournisseurResponseDTO());

        CommandeFournisseurResponseDTO dto = service.receiveCommande(1L);

        ArgumentCaptor<LotStock> lotCaptor = ArgumentCaptor.forClass(LotStock.class);
        verify(lotStockRepository, times(1)).save(lotCaptor.capture());
        LotStock savedLot = lotCaptor.getValue();

        assertThat(savedLot.getNumeroLot()).isNotNull().startsWith("LOT-");
        assertThat(savedLot.getProduit()).isEqualTo(produit);
        assertThat(savedLot.getCommande()).isEqualTo(commande);
        assertThat(savedLot.getQuantiteRestante()).isEqualByComparingTo(ligne.getQuantite());
        assertThat(savedLot.getPrixUnitaireAchat()).isEqualByComparingTo(ligne.getPrixUnitaire());
        assertThat(savedLot.getStatut()).isEqualTo(StatutLot.ACTIF);
        assertThat(savedLot.getDateEntree()).isNotNull();
        assertThat(savedLot.getDateEntree()).isAfterOrEqualTo(before.minusSeconds(1));

        ArgumentCaptor<MouvementStock> movCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        verify(mouvementStockRepository, times(1)).save(movCaptor.capture());
        MouvementStock savedMov = movCaptor.getValue();

        assertThat(savedMov.getTypeMouvement()).isEqualTo(TypeMouvement.ENTREE);
        assertThat(savedMov.getLotStock()).isEqualTo(savedLot);
        assertThat(savedMov.getProduit()).isEqualTo(produit);
        assertThat(savedMov.getReference()).isEqualTo(commande.getNumeroCommande());
        assertThat(savedMov.getQuantite()).isEqualByComparingTo(ligne.getQuantite());
        assertThat(savedMov.getDateMouvement()).isNotNull();

        ArgumentCaptor<Produit> prodCaptor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository, times(1)).save(prodCaptor.capture());
        Produit savedProduit = prodCaptor.getValue();
        assertThat(savedProduit.getStockActuel()).isEqualByComparingTo(new BigDecimal("70"));


        assertThat(dto).isNotNull();
    }
}
