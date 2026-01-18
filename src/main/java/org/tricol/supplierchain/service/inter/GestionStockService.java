package org.tricol.supplierchain.service.inter;

import org.tricol.supplierchain.dto.response.*;
import org.tricol.supplierchain.entity.BonSortie;
import org.tricol.supplierchain.entity.Fournisseur;
import org.tricol.supplierchain.entity.Produit;

import java.math.BigDecimal;
import java.util.List;


public interface GestionStockService {

    StockGlobalResponseDTO getStockGlobal();
    StockProduitResponseDTO getStockByProduit(Long produitId);
    List<MouvementStockResponseDTO> getHistoriqueMouvements();
    List<MouvementStockResponseDTO> getMouvementsByProduit(Long produitId);
    BigDecimal getValorisationTotale();

    boolean isStockSuffisant(Long produitId, BigDecimal quantiteRequise);
    BigDecimal getQuantiteDisponible(Long produitId);

    List<CommandeFournisseurResponseDTO> createCommandeFournisseurEnCasUrgente(List<DeficitStockResponseDTO> deficits);

    List<DeficitStockResponseDTO> verifyStockPourBonSortie(BonSortie bonSortie);

    Fournisseur getFournisseursSuggeresPourProduit(Long produitId);




}
