package org.tricol.supplierchain.service.inter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tricol.supplierchain.dto.response.*;
import org.tricol.supplierchain.entity.BonSortie;
import org.tricol.supplierchain.entity.Fournisseur;
import org.tricol.supplierchain.entity.Produit;
import org.tricol.supplierchain.enums.TypeMouvement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public interface GestionStockService {

    StockGlobalResponseDTO getStockGlobal();
    StockProduitResponseDTO getStockByProduit(Long produitId);
    List<MouvementStockResponseDTO> getHistoriqueMouvements();
    List<MouvementStockResponseDTO> getMouvementsByProduit(Long produitId);
    Page<MouvementStockResponseDTO> searchMouvements(Long produitId, String reference, TypeMouvement type, String numeroLot, LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);
    BigDecimal getValorisationTotale();

    boolean isStockSuffisant(Long produitId, BigDecimal quantiteRequise);
    BigDecimal getQuantiteDisponible(Long produitId);

    List<CommandeFournisseurResponseDTO> createCommandeFournisseurEnCasUrgente(List<DeficitStockResponseDTO> deficits);

    List<DeficitStockResponseDTO> verifyStockPourBonSortie(BonSortie bonSortie);

    Fournisseur getFournisseursSuggeresPourProduit(Long produitId);




}
