package org.tricol.supplierchain.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.BonSortieRequestDTO;
import org.tricol.supplierchain.dto.request.BonSortieUpdateDTO;
import org.tricol.supplierchain.dto.response.BonSortieResponseDTO;
import org.tricol.supplierchain.enums.Atelier;
import org.tricol.supplierchain.service.inter.BonSortieService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/bonSorties")
public class BonSortieController {

    private final BonSortieService bonSortieService;

    @PostMapping()
    @PreAuthorize("hasAuthority('BONSORTIE_CREATE')")
    public ResponseEntity<BonSortieResponseDTO> createBonSortie(@RequestBody @Valid BonSortieRequestDTO bonSortieRequestDTO) {

        BonSortieResponseDTO responseDTO = bonSortieService.createBonSortie(bonSortieRequestDTO);
        return ResponseEntity.ok(responseDTO);

    }


    @GetMapping()
    @PreAuthorize("hasAuthority('BONSORTIE_READ')")
    public ResponseEntity<List<BonSortieResponseDTO>> getBonSorties() {
        List<BonSortieResponseDTO> bonSorties = bonSortieService.getBonSorties();
        return ResponseEntity.ok(bonSorties);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BONSORTIE_READ')")
    public ResponseEntity<BonSortieResponseDTO> getBonSortieById(@PathVariable Long id) {
        BonSortieResponseDTO bonSortie = bonSortieService.getBonSortieById(id);
        return ResponseEntity.ok(bonSortie);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BONSORTIE_DELETE')")
    public ResponseEntity<String> deleteBonSortie(@PathVariable Long id) {
        bonSortieService.deleteBonSortie(id);
        return ResponseEntity.ok("Bon de sortie avec id " +id +" est supprimé" );
    }

    @GetMapping("/atelier/{atelier}")
    @PreAuthorize("hasAuthority('BONSORTIE_READ')")
    public ResponseEntity<List<BonSortieResponseDTO>> getBonSortiesByAtelier(@PathVariable Atelier atelier) {
        List<BonSortieResponseDTO> bonSorties = bonSortieService.getBonSortiesByAtelier(atelier);
        return ResponseEntity.ok(bonSorties);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BONSORTIE_UPDATE')")
    public ResponseEntity<BonSortieResponseDTO> updateBonSortie(@PathVariable Long id, @Valid @RequestBody BonSortieUpdateDTO bonSortieUpdateDTO) {
        BonSortieResponseDTO updatedBonSortie = bonSortieService.updateBonSortie(id, bonSortieUpdateDTO);
        return ResponseEntity.ok(updatedBonSortie);
    }


    @PutMapping("/annulation/{id}")
    @PreAuthorize("hasAuthority('BONSORTIE_CANCEL')")
    public ResponseEntity<String> annulationBonSortie(@PathVariable Long id) {
        bonSortieService.annulationBonSortie(id);
        return ResponseEntity.ok("Bon de sortie avec id " +id +" est annulé" );
    }

    @PutMapping("/validation/{id}")
    @PreAuthorize("hasAuthority('BONSORTIE_VALIDATE')")
    public ResponseEntity<BonSortieResponseDTO> validationBonSortie(@PathVariable Long id) {
            BonSortieResponseDTO validatedBonSortie = bonSortieService.validationBonSortie(id);
            return ResponseEntity.ok(validatedBonSortie);
        }

}
