package org.tricol.supplierchain.service.inter;

import org.tricol.supplierchain.dto.request.BonSortieRequestDTO;
import org.tricol.supplierchain.dto.request.BonSortieUpdateDTO;
import org.tricol.supplierchain.dto.response.BonSortieResponseDTO;
import org.tricol.supplierchain.entity.BonSortie;
import org.tricol.supplierchain.enums.Atelier;

import java.util.List;

public interface BonSortieService {

    BonSortieResponseDTO createBonSortie(BonSortieRequestDTO requestDTO);

    List<BonSortieResponseDTO> getBonSorties();

    BonSortieResponseDTO getBonSortieById(Long id);

    void deleteBonSortie(Long id);

    List<BonSortieResponseDTO> getBonSortiesByAtelier(Atelier atelier);

    BonSortieResponseDTO updateBonSortie(Long id, BonSortieUpdateDTO requestDTO);

    void annulationBonSortie(Long id);

    BonSortieResponseDTO validationBonSortie(Long id);

    BonSortieResponseDTO performActualValidation(BonSortie bonSortie);
}
