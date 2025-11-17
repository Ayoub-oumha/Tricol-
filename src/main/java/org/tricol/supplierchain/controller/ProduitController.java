package org.tricol.supplierchain.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.ProduitRequestDTO;
import org.tricol.supplierchain.dto.request.ProduitUpdatDTO;
import org.tricol.supplierchain.dto.response.ProduitResponseDTO;
import org.tricol.supplierchain.dto.response.StockProduitResponseDTO;
import org.tricol.supplierchain.service.inter.GestionStockService;
import org.tricol.supplierchain.service.inter.Produitservice;

import java.util.List;

@RestController
@RequestMapping("/api/v1/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final Produitservice produitservice;
    private final GestionStockService stockService;

    @GetMapping
    public ResponseEntity<List<ProduitResponseDTO>> getAllProduits() {
        List<ProduitResponseDTO> produits = produitservice.getAllProduits();
        return ResponseEntity.ok(produits);
    }


    @PostMapping
    public ResponseEntity<ProduitResponseDTO> createProduit(@Valid @RequestBody ProduitRequestDTO produitRequestDTO) {
        ProduitResponseDTO produit = produitservice.createProduit(produitRequestDTO);
        return ResponseEntity.ok(produit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponseDTO> getProduitById(@PathVariable Long id){
        ProduitResponseDTO produit = produitservice.getProduitById(id);
        return ResponseEntity.ok(produit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id){
        produitservice.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProduitResponseDTO> updateProduit(@PathVariable Long id, @Valid @RequestBody ProduitUpdatDTO produitUpdatDTO) {
        ProduitResponseDTO updatedProduit = produitservice.modifierProduit(id, produitUpdatDTO);
        return ResponseEntity.ok(updatedProduit);
    }



    @GetMapping("/{id}/stock")
    public ResponseEntity<StockProduitResponseDTO> getStockByProduit(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockByProduit(id));
    }


}
