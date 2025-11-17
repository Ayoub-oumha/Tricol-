package org.tricol.supplierchain.service;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.tricol.supplierchain.dto.request.FournisseurRequestDTO;
import org.tricol.supplierchain.dto.request.FournisseurUpdateDTO;
import org.tricol.supplierchain.dto.response.FournisseurResponseDTO;
import org.tricol.supplierchain.entity.Fournisseur;
import org.tricol.supplierchain.exception.DuplicateResourceException;
import org.tricol.supplierchain.exception.ResourceNotFoundException;
import org.tricol.supplierchain.mapper.FournisseurMapper;
import org.tricol.supplierchain.repository.FournisseurRepository;
import org.tricol.supplierchain.service.inter.FournisseurService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FournisseurServiceImpl implements FournisseurService {


    private final FournisseurMapper fournisseurMapper;
    private final FournisseurRepository fournisseurRepository;

    public FournisseurResponseDTO crerateFournisseur(FournisseurRequestDTO fournisseurRequest) {
        Fournisseur fournisseur = fournisseurMapper.toEntity(fournisseurRequest);
        if(fournisseurRepository.existsByEmail(fournisseur.getEmail())) {
            throw new DuplicateResourceException("Fournisseur avec Email " + fournisseur.getEmail() + " existe déjà.");
        }
        if(fournisseurRepository.existsByIce(fournisseur.getIce())) {
            throw new DuplicateResourceException("Fournisseur avec ICE " + fournisseur.getIce() + " existe déjà.");
        }
        return fournisseurMapper.toResponseDTO(
                fournisseurRepository.save(fournisseur)
        );
    }

    public List<FournisseurResponseDTO> getAllFournisseurs() {
        List<Fournisseur> fournisseurs = fournisseurRepository.findAll();
        return fournisseurs.stream()
                .map(fournisseurMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void deleteFournisseur(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur avec l'id " + id + " non trouvé."));
        fournisseurRepository.delete(fournisseur);
    }


    @Override
    public FournisseurResponseDTO getFournisseur(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Fournisseur avec l'id " + id + " non trouvé."));
        return fournisseurMapper.toResponseDTO(fournisseur);
    }

    @Override
    public FournisseurResponseDTO modifieFournisseur(Long id, FournisseurUpdateDTO updateDTO) {


        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur avec l'id " + id + " non trouvé."));

        if (updateDTO.getIce() != null && !updateDTO.getIce().equals(fournisseur.getIce())
                && fournisseurRepository.existsByIce(updateDTO.getIce())) {
            throw new DuplicateResourceException("Fournisseur avec ICE " + updateDTO.getIce() + " existe déjà.");
        }

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(fournisseur.getEmail())
                && fournisseurRepository.existsByEmail(updateDTO.getEmail())) {
            throw new DuplicateResourceException("Fournisseur avec email " + updateDTO.getEmail() + " existe déjà.");
        }

        fournisseurMapper.updateEntityFromDto(updateDTO, fournisseur);

        Fournisseur updated = fournisseurRepository.save(fournisseur);

        return fournisseurMapper.toResponseDTO(updated);
    }


}
