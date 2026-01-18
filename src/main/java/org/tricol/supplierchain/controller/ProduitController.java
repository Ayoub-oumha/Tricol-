package org.tricol.supplierchain.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.PointCommandeRequest;
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
    @PreAuthorize("hasAuthority('PRODUIT_READ')")
    public ResponseEntity<List<ProduitResponseDTO>> getAllProduits() {
        List<ProduitResponseDTO> produits = produitservice.getAllProduits();
        return ResponseEntity.ok(produits);
    }


    @PostMapping
    @PreAuthorize("hasAuthority('PRODUIT_CREATE')")
    public ResponseEntity<ProduitResponseDTO> createProduit(@Valid @RequestBody ProduitRequestDTO produitRequestDTO) {
        ProduitResponseDTO produit = produitservice.createProduit(produitRequestDTO);
        return ResponseEntity.ok(produit);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUIT_READ')")
    public ResponseEntity<ProduitResponseDTO> getProduitById(@PathVariable Long id){
        ProduitResponseDTO produit = produitservice.getProduitById(id);
        return ResponseEntity.ok(produit);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUIT_DELETE')")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id){
        produitservice.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUIT_UPDATE')")
    public ResponseEntity<ProduitResponseDTO> updateProduit(@PathVariable Long id, @Valid @RequestBody ProduitUpdatDTO produitUpdatDTO) {
        ProduitResponseDTO updatedProduit = produitservice.modifierProduit(id, produitUpdatDTO);
        return ResponseEntity.ok(updatedProduit);
    }



    @GetMapping("/{id}/stock")
    @PreAuthorize("hasAuthority('PRODUIT_READ')")
    public ResponseEntity<StockProduitResponseDTO> getStockByProduit(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockByProduit(id));
    }

    @PostMapping("/seuil/{id}")
    @PreAuthorize("hasAuthority('PRODUIT_CONFIGURE_SEUIL')")
    public ResponseEntity<ProduitResponseDTO> changeSeuil(@PathVariable Long id, @Valid @RequestBody PointCommandeRequest request){
        ProduitResponseDTO produitResponseDTO = produitservice.updatePointCommande(id,request);
        return ResponseEntity.ok(produitResponseDTO);
    }


}
