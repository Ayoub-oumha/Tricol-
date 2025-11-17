package org.tricol.supplierchain.service.inter;


import org.tricol.supplierchain.dto.request.FournisseurRequestDTO;
import org.tricol.supplierchain.dto.request.FournisseurUpdateDTO;
import org.tricol.supplierchain.dto.response.FournisseurResponseDTO;

import java.util.List;

public interface FournisseurService {

    FournisseurResponseDTO crerateFournisseur(FournisseurRequestDTO fournisseurRequest);
    List<FournisseurResponseDTO> getAllFournisseurs();
    void deleteFournisseur(Long id);
    FournisseurResponseDTO getFournisseur(Long id);
    FournisseurResponseDTO modifieFournisseur(Long id , FournisseurUpdateDTO requestDTO);
}
