package org.tricol.supplierchain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.FournisseurRequestDTO;
import org.tricol.supplierchain.dto.request.FournisseurUpdateDTO;
import org.tricol.supplierchain.dto.response.FournisseurResponseDTO;
import org.tricol.supplierchain.entity.Fournisseur;
import org.tricol.supplierchain.service.inter.FournisseurService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fournisseurs")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;


    @PostMapping
    @PreAuthorize("hasAuthority('FOURNISSEUR_CREATE')")
    public ResponseEntity<FournisseurResponseDTO> createFournisseur(@Valid @RequestBody FournisseurRequestDTO fournisseurRequestDTO) {
        FournisseurResponseDTO response = fournisseurService.crerateFournisseur(fournisseurRequestDTO);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('FOURNISSEUR_READ')")
    public ResponseEntity<List<FournisseurResponseDTO>> getAllFournisseurs() {
        List<FournisseurResponseDTO> fournisseurs = fournisseurService.getAllFournisseurs();
        return ResponseEntity.ok(fournisseurs);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_DELETE')")
    public ResponseEntity<String> deleteFournisseur(@PathVariable Long id) {
        fournisseurService.deleteFournisseur(id);
        return ResponseEntity.ok("Fournisseur avec id " +id +" est supprimé" );
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_READ')")
    public ResponseEntity<FournisseurResponseDTO> getFournisseur(@PathVariable Long id) {
        FournisseurResponseDTO fournisseur = fournisseurService.getFournisseur(id);
        return ResponseEntity.ok(fournisseur);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_UPDATE')")
    public ResponseEntity<FournisseurResponseDTO> updateFournisseur(@PathVariable Long id, @Valid @RequestBody FournisseurUpdateDTO fournisseurUpdateDTO) {
        FournisseurResponseDTO updatedFournisseur = fournisseurService.modifieFournisseur(id,fournisseurUpdateDTO );
        return ResponseEntity.ok(updatedFournisseur);
    }




}
