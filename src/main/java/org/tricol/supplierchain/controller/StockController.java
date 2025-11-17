package org.tricol.supplierchain.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tricol.supplierchain.dto.response.AlerteStockResponseDTO;
import org.tricol.supplierchain.dto.response.MouvementStockResponseDTO;
import org.tricol.supplierchain.dto.response.StockGlobalResponseDTO;
import org.tricol.supplierchain.dto.response.StockProduitResponseDTO;
import org.tricol.supplierchain.service.inter.GestionStockService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {
    private final GestionStockService stockService;
    private final GestionStockService gestionStockService;


    @GetMapping
    public ResponseEntity<StockGlobalResponseDTO> getStockGlobal() {
        return ResponseEntity.ok(stockService.getStockGlobal());
    }

    @GetMapping("/produit/{id}")
    public ResponseEntity<StockProduitResponseDTO> getStockByProduit(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(stockService.getStockByProduit(id));
    }

    @GetMapping("/mouvements")
    public ResponseEntity<List<MouvementStockResponseDTO>> getMouvementsHistorique(){
        return ResponseEntity.ok(stockService.getHistoriqueMouvements());
    }

    @GetMapping("/mouvements/produit/{id}")
    public ResponseEntity<List<MouvementStockResponseDTO>> getMouvementsByProduit(@PathVariable Long id){
        return ResponseEntity.ok(stockService.getMouvementsByProduit(id));
    }

    @GetMapping("/valorisation")
    public ResponseEntity<BigDecimal> getValorisationTotale(){
        return ResponseEntity.ok(stockService.getValorisationTotale());
    }

}
