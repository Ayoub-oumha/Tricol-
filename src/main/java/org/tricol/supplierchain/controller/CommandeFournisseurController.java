package org.tricol.supplierchain.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.CommandeFournisseurCreateDTO;
import org.tricol.supplierchain.dto.request.CommandeFournisseurUpdateDTO;
import org.tricol.supplierchain.dto.response.CommandeFournisseurResponseDTO;
import org.tricol.supplierchain.service.inter.CommandeFournisseurService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/commandes")
@RequiredArgsConstructor
public class CommandeFournisseurController {

    private final CommandeFournisseurService commandeFournisseurService;

    @GetMapping
    @PreAuthorize("hasAuthority('COMMANDE_READ')")
    public ResponseEntity<List<CommandeFournisseurResponseDTO>> getAllCommandes(){
        List<CommandeFournisseurResponseDTO> commandes = commandeFournisseurService.getAllCommandes();
        if (commandes == null || commandes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(commandes);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMMANDE_CREATE')")
    public ResponseEntity<CommandeFournisseurResponseDTO> createCommande(@Valid @RequestBody CommandeFournisseurCreateDTO createDTO){
        CommandeFournisseurResponseDTO response = commandeFournisseurService.createCommande(createDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{commandeId}")
    @PreAuthorize("hasAuthority('COMMANDE_READ')")
    public ResponseEntity<CommandeFournisseurResponseDTO> getCommande(@PathVariable(name = "commandeId") @Positive Long id){
        CommandeFournisseurResponseDTO commande = commandeFournisseurService.getCommandeById(id);
        return ResponseEntity.ok(commande);
    }

    @PutMapping("/{commandeId}")
    @PreAuthorize("hasAuthority('COMMANDE_UPDATE')")
    public ResponseEntity<CommandeFournisseurResponseDTO> updateCommande(
            @PathVariable("commandeId") Long id,
            @Valid @RequestBody CommandeFournisseurUpdateDTO requestDTO
            ){
        CommandeFournisseurResponseDTO commande = commandeFournisseurService.updateCommande(id, requestDTO);
        return ResponseEntity.ok(commande);
    }

    @DeleteMapping("/{commandeId}")
    @PreAuthorize("hasAuthority('COMMANDE_DELETE')")
    public ResponseEntity<Void> deleteCommande(@PathVariable("commandeId") Long id){
        commandeFournisseurService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    @PreAuthorize("hasAuthority('COMMANDE_READ')")
    public ResponseEntity<List<CommandeFournisseurResponseDTO>> getCommandeBySupplierId(@PathVariable("fournisseurId") Long id){
        List<CommandeFournisseurResponseDTO> commandes = commandeFournisseurService.getCommandesBySupplierId(id);
        return ResponseEntity.ok(commandes);
    }

    @PutMapping("/{commandeId}/reception")
    @PreAuthorize("hasAuthority('COMMANDE_RECEIVE')")
    public ResponseEntity<CommandeFournisseurResponseDTO> receiveCommande(@PathVariable("commandeId") Long id){
        CommandeFournisseurResponseDTO commande = commandeFournisseurService.receiveCommande(id);
        return ResponseEntity.ok(commande);
    }

    @PutMapping("/{commandeId}/valider")
    @PreAuthorize("hasAuthority('COMMANDE_VALIDATE')")
    public ResponseEntity<CommandeFournisseurResponseDTO> validerCommande(@PathVariable("commandeId") Long id){
        CommandeFournisseurResponseDTO commande = commandeFournisseurService.validerCommande(id);
        return ResponseEntity.ok(commande);
    }


}
