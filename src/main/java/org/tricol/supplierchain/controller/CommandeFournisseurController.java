package org.tricol.supplierchain.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<CommandeFournisseurResponseDTO>> getAllCommandes(){
        List<CommandeFournisseurResponseDTO> commandes = commandeFournisseurService.getAllCommandes();
        if (commandes == null || commandes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(commandes);
    }

    @PostMapping
    public ResponseEntity<CommandeFournisseurResponseDTO> createCommande(@Valid @RequestBody CommandeFournisseurCreateDTO createDTO){
        CommandeFournisseurResponseDTO response = commandeFournisseurService.createCommande(createDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{commandeId}")
    public ResponseEntity<CommandeFournisseurResponseDTO> getCommande(@PathVariable(name = "commandeId") @Positive Long id){
        CommandeFournisseurResponseDTO commande = commandeFournisseurService.getCommandeById(id);
        return ResponseEntity.ok(commande);
    }

    @PutMapping("/{commandeId}")
    public ResponseEntity<CommandeFournisseurResponseDTO> updateCommande(
            @PathVariable("commandeId") Long id,
            @Valid @RequestBody CommandeFournisseurUpdateDTO requestDTO
            ){
        CommandeFournisseurResponseDTO commande = commandeFournisseurService.updateCommande(id, requestDTO);
        return ResponseEntity.ok(commande);
    }

    @DeleteMapping("/{commandeId}")
    public ResponseEntity<Void> deleteCommande(@PathVariable("commandeId") Long id){
        commandeFournisseurService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    public ResponseEntity<List<CommandeFournisseurResponseDTO>> getCommandeBySupplierId(@PathVariable("fournisseurId") Long id){
        List<CommandeFournisseurResponseDTO> commandes = commandeFournisseurService.getCommandesBySupplierId(id);
        return ResponseEntity.ok(commandes);
    }

    @PutMapping("/{commandeId}/reception")
    public ResponseEntity<CommandeFournisseurResponseDTO> receiveCommande(@PathVariable("commandeId") Long id){
        CommandeFournisseurResponseDTO commande = commandeFournisseurService.receiveCommande(id);
        return ResponseEntity.ok(commande);
    }

    @PutMapping("/{commandeId}/valider")
    public ResponseEntity<CommandeFournisseurResponseDTO> validerCommande(@PathVariable("commandeId") Long id){
        CommandeFournisseurResponseDTO commande = commandeFournisseurService.validerCommande(id);
        return ResponseEntity.ok(commande);
    }


}
