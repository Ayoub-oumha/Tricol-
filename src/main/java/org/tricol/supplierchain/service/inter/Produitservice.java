package org.tricol.supplierchain.service.inter;

import org.springframework.stereotype.Service;
import org.tricol.supplierchain.dto.request.ProduitRequestDTO;
import org.tricol.supplierchain.dto.request.ProduitUpdatDTO;
import org.tricol.supplierchain.dto.response.ProduitResponseDTO;

import java.util.List;


public interface Produitservice {

    ProduitResponseDTO createProduit(ProduitRequestDTO produitRequestDTO);
    ProduitResponseDTO modifierProduit(Long id, ProduitUpdatDTO produitUpdatDTO);
    ProduitResponseDTO getProduitById(Long id);
    void deleteProduit(Long id);
    List<ProduitResponseDTO> getAllProduits();
    
}
