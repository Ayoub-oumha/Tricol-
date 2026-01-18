package org.tricol.supplierchain.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.enums.TypeMouvement;
import org.tricol.supplierchain.dto.response.AlerteStockResponseDTO;
import org.tricol.supplierchain.dto.response.MouvementStockResponseDTO;
import org.tricol.supplierchain.dto.response.StockGlobalResponseDTO;
import org.tricol.supplierchain.dto.response.StockProduitResponseDTO;
import org.tricol.supplierchain.service.inter.GestionStockService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {
    private final GestionStockService stockService;
    private final GestionStockService gestionStockService;


    @GetMapping
    @PreAuthorize("hasAuthority('STOCK_READ')")
    public ResponseEntity<StockGlobalResponseDTO> getStockGlobal() {
        return ResponseEntity.ok(stockService.getStockGlobal());
    }

    @GetMapping("/produit/{id}")
    @PreAuthorize("hasAuthority('STOCK_READ')")
    public ResponseEntity<StockProduitResponseDTO> getStockByProduit(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(stockService.getStockByProduit(id));
    }

    @GetMapping("/mouvements")
    @PreAuthorize("hasAuthority('STOCK_READ')")
    public ResponseEntity<List<MouvementStockResponseDTO>> getMouvementsHistorique(){
        return ResponseEntity.ok(stockService.getHistoriqueMouvements());
    }

    @GetMapping("/mouvements/filter")
    @PreAuthorize("hasAuthority('STOCK_HISTORIQUE')")
    public ResponseEntity<?> getMouvementsHistoriqueWithFilter(
            @RequestParam(required = false) Long produitId,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String numeroLot,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        TypeMouvement typeMouvement = type != null ? TypeMouvement.valueOf(type) : null;
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateMouvement").descending());
        return ResponseEntity.ok(stockService.searchMouvements(produitId, reference, typeMouvement, numeroLot, dateDebut, dateFin, pageable));
    }

    @GetMapping("/mouvements/produit/{id}")
    @PreAuthorize("hasAuthority('STOCK_READ')")
    public ResponseEntity<List<MouvementStockResponseDTO>> getMouvementsByProduit(@PathVariable Long id){
        return ResponseEntity.ok(stockService.getMouvementsByProduit(id));
    }

    @GetMapping("/valorisation")
    @PreAuthorize("hasAuthority('STOCK_VALORISATION')")
    public ResponseEntity<BigDecimal> getValorisationTotale(){
        return ResponseEntity.ok(stockService.getValorisationTotale());
    }

}
