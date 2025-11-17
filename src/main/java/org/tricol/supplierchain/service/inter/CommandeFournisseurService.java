package org.tricol.supplierchain.service.inter;

import org.tricol.supplierchain.dto.request.CommandeFournisseurCreateDTO;
import org.tricol.supplierchain.dto.request.CommandeFournisseurUpdateDTO;
import org.tricol.supplierchain.dto.response.CommandeFournisseurResponseDTO;

import java.util.List;

public interface CommandeFournisseurService {

    List<CommandeFournisseurResponseDTO> getAllCommandes();

    CommandeFournisseurResponseDTO createCommande(CommandeFournisseurCreateDTO requestDTO);

    CommandeFournisseurResponseDTO updateCommande(Long id, CommandeFournisseurUpdateDTO requestDTO);

    CommandeFournisseurResponseDTO getCommandeById(Long id);

    void deleteCommande(Long id);

    List<CommandeFournisseurResponseDTO> getCommandesBySupplierId(Long id);

    CommandeFournisseurResponseDTO receiveCommande(Long id);

    CommandeFournisseurResponseDTO validerCommande(Long id);


}
