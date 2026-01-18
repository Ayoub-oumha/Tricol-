package org.tricol.supplierchain.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.MouvementStockSearchCriteria;
import org.tricol.supplierchain.dto.response.AlerteStockResponseDTO;
import org.tricol.supplierchain.dto.response.MouvementStockResponseDTO;
import org.tricol.supplierchain.dto.response.StockGlobalResponseDTO;
import org.tricol.supplierchain.dto.response.StockProduitResponseDTO;
import org.tricol.supplierchain.enums.TypeMouvement;
import org.tricol.supplierchain.security.RequirePermission;
import org.tricol.supplierchain.service.MouvementStockSearchService;
import org.tricol.supplierchain.service.inter.GestionStockService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {
    private final GestionStockService stockService;
    private final GestionStockService gestionStockService;
    private final MouvementStockSearchService mouvementStockSearchService;


    @GetMapping
    @RequirePermission("STOCK_READ")
    public ResponseEntity<StockGlobalResponseDTO> getStockGlobal() {
        return ResponseEntity.ok(stockService.getStockGlobal());
    }

    @GetMapping("/produit/{id}")
    @RequirePermission("STOCK_READ")
    public ResponseEntity<StockProduitResponseDTO> getStockByProduit(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(stockService.getStockByProduit(id));
    }

    @GetMapping("/mouvements")
    @RequirePermission("STOCK_HISTORY_READ")
    public ResponseEntity<List<MouvementStockResponseDTO>> getMouvementsHistorique(){
        return ResponseEntity.ok(stockService.getHistoriqueMouvements());
    }

    @GetMapping("/mouvements/produit/{id}")
    @RequirePermission("STOCK_HISTORY_READ")
    public ResponseEntity<List<MouvementStockResponseDTO>> getMouvementsByProduit(@PathVariable Long id){
        return ResponseEntity.ok(stockService.getMouvementsByProduit(id));
    }

    @GetMapping("/valorisation")
    @RequirePermission("STOCK_VALUATION_READ")
    public ResponseEntity<BigDecimal> getValorisationTotale(){
        return ResponseEntity.ok(stockService.getValorisationTotale());
    }

    @GetMapping("/mouvements/search")
    @RequirePermission("STOCK_HISTORY_READ")
    public ResponseEntity<Page<MouvementStockResponseDTO>> searchMouvements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long produitId,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) TypeMouvement type,
            @RequestParam(required = false) String numeroLot,
            @PageableDefault(size = 20, sort = "dateMouvement", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        MouvementStockSearchCriteria criteria = MouvementStockSearchCriteria
                .builder()
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .produitId(produitId)
                .reference(reference)
                .type(type)
                .numeroLot(numeroLot)
                .build();
        
        Page<MouvementStockResponseDTO> result = mouvementStockSearchService.searchMouvements(criteria, pageable);
        return ResponseEntity.ok(result);
    }

}
