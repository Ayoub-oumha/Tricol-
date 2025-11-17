package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.dto.request.BonSortieRequestDTO;
import org.tricol.supplierchain.dto.request.BonSortieUpdateDTO;
import org.tricol.supplierchain.dto.request.LigneBonSortieRequestDTO;
import org.tricol.supplierchain.dto.response.BonSortieResponseDTO;
import org.tricol.supplierchain.dto.response.CommandeFournisseurResponseDTO;
import org.tricol.supplierchain.dto.response.DeficitStockResponseDTO;
import org.tricol.supplierchain.entity.*;
import org.tricol.supplierchain.enums.Atelier;
import org.tricol.supplierchain.enums.StatutBonSortie;
import org.tricol.supplierchain.enums.TypeMouvement;
import org.tricol.supplierchain.exception.BusinessException;
import org.tricol.supplierchain.exception.ResourceNotFoundException;
import org.tricol.supplierchain.exception.StockInsuffisantException;
import org.tricol.supplierchain.mapper.BonSortieMapper;
import org.tricol.supplierchain.repository.BonSortieRepository;
import org.tricol.supplierchain.repository.LotStockRepository;
import org.tricol.supplierchain.repository.MouvementStockRepository;
import org.tricol.supplierchain.repository.ProduitRepository;
import org.tricol.supplierchain.service.inter.BonSortieService;
import org.tricol.supplierchain.service.inter.GestionStockService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BonSortieServiceImpl implements BonSortieService {

    private final BonSortieRepository bonSortieRepository;
    private final ProduitRepository produitRepository;
    private  final LotStockRepository  lotStockRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final BonSortieMapper bonSortieMapper;
    private final GestionStockService gestionStockService;


    @Override
    public BonSortieResponseDTO createBonSortie(BonSortieRequestDTO requestDTO) {
        BonSortie bonSortie = bonSortieMapper.toEntity(requestDTO);
        bonSortie.setNumeroBon(UUID.randomUUID().toString());

        List<LigneBonSortie> ligneBonSortie = new ArrayList<>();
        for(LigneBonSortieRequestDTO lineDto : requestDTO.getLignesBonSortie()) {
            Produit produit = produitRepository.findById(lineDto.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id " + lineDto.getProduitId()));
            LigneBonSortie ligne = LigneBonSortie.builder()
                    .produit(produit)
                    .quantite(lineDto.getQuantite())
                    .bonSortie(bonSortie)
                    .build();

            ligneBonSortie.add(ligne);
        }
        bonSortie.setLigneBonSorties(ligneBonSortie);
        bonSortie.setStatut(StatutBonSortie.BROUILLON);
        bonSortie.setMotif(requestDTO.getMotif());
        bonSortie.setAtelier(requestDTO.getAtelier());

        BonSortie savedBonSortie = bonSortieRepository.save(bonSortie);

        return  bonSortieMapper.toResponseDTO(savedBonSortie);

    }

    @Override
    @Transactional(readOnly = true)
    public List<BonSortieResponseDTO> getBonSorties() {
        List<BonSortieResponseDTO> Bons = bonSortieRepository.findAll()
                .stream()
                .map(bonSortieMapper::toResponseDTO)
                .toList();
        if (Bons.isEmpty()) {
            throw new ResourceNotFoundException("Aucun bon de sortie trouvé.");
        }
        return Bons;
    }

    @Override
    @Transactional(readOnly = true)
    public BonSortieResponseDTO getBonSortieById(Long id) {
        BonSortie bonSortie = bonSortieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de sortie non trouvé avec l'id " + id));
        return bonSortieMapper.toResponseDTO(bonSortie);
    }

    @Override
    @Transactional
    public void deleteBonSortie(Long id) {
        BonSortie bonSortie = bonSortieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de sortie non trouvé avec l'id " + id));
        if(bonSortie.getStatut() != StatutBonSortie.BROUILLON) {
            throw new BusinessException("Seul les bons de sortie en statut BROUILLON peuvent être supprimés.");
        }
        bonSortieRepository.delete(bonSortie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BonSortieResponseDTO> getBonSortiesByAtelier(Atelier atelier) {
        List<BonSortieResponseDTO> Bons = bonSortieRepository.findByAtelier(atelier)
                .stream()
                .map(bonSortieMapper::toResponseDTO)
                .toList();
        if (Bons.isEmpty()) {
            throw new ResourceNotFoundException("Aucun bon de sortie trouvé pour l'atelier " + atelier);
        }

        return Bons;
    }

    @Override
    @Transactional
    public BonSortieResponseDTO updateBonSortie(Long id, BonSortieUpdateDTO requestDTO) {
        BonSortie existingBonSortie = bonSortieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de sortie non trouvé avec l'id " + id));

        if(existingBonSortie.getStatut() != StatutBonSortie.BROUILLON) {
            throw new BusinessException("Seul les bons de sortie en statut BROUILLON peuvent être modifiés.");
        }
        
        if (requestDTO.getLignesBonSortie() != null && !requestDTO.getLignesBonSortie().isEmpty()) {
            existingBonSortie.getLigneBonSorties().clear();

            for (LigneBonSortieRequestDTO lineDto : requestDTO.getLignesBonSortie()) {
                Produit produit = produitRepository.findById(lineDto.getProduitId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id " + lineDto.getProduitId()));
                LigneBonSortie ligne = LigneBonSortie.builder()
                        .produit(produit)
                        .quantite(lineDto.getQuantite())
                        .bonSortie(existingBonSortie)
                        .build();
                existingBonSortie.getLigneBonSorties().add(ligne);
            }
        }
        bonSortieMapper.updateEntityFromDto(requestDTO, existingBonSortie);

        BonSortie savedBonSortie = bonSortieRepository.save(existingBonSortie);

        return bonSortieMapper.toResponseDTO(savedBonSortie);
    }

    @Override
    @Transactional
    public void annulationBonSortie(Long id) {
        BonSortie bonSortie = bonSortieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de sortie non trouvé avec l'id " + id));
        if(bonSortie.getStatut() != StatutBonSortie.BROUILLON) {
            throw new BusinessException("Seul les bons de sortie en statut BROUILLON peuvent être annulés.");
        }
        bonSortie.setStatut(StatutBonSortie.ANNULE);
        bonSortieRepository.save(bonSortie);
    }

    @Override
    public BonSortieResponseDTO validationBonSortie(Long id) {
        BonSortie bonSortie = bonSortieRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Bon de sortie non trouvé avec l'id " + id));

        if(bonSortie.getStatut() != StatutBonSortie.BROUILLON) {
            throw new BusinessException("Seul les bons de sortie en statut BROUILLON peuvent être annulés.");
        }

        List<DeficitStockResponseDTO> deficits = gestionStockService.verifyStockPourBonSortie(bonSortie);

        if (!deficits.isEmpty()){
            List<CommandeFournisseurResponseDTO> commandes =  gestionStockService.createCommandeFournisseurEnCasUrgente(deficits);
            commandes.forEach(commande -> System.err.println(commande.getId()));
            throw new StockInsuffisantException(bonSortie.getNumeroBon(), deficits);
        }

        return performActualValidation(bonSortie);
    }

    @Override
    @Transactional
    public BonSortieResponseDTO performActualValidation(BonSortie bonSortie){
        BigDecimal montantTotal = BigDecimal.ZERO;

        for(LigneBonSortie ligne :  bonSortie.getLigneBonSorties()) {

            List<LotStock> lotStocks = lotStockRepository.findByProduitIdOrderByDateEntreeAsc(ligne.getProduit().getId());

            BigDecimal quantiteRestante = ligne.getQuantite();

            BigDecimal montantLigne = BigDecimal.ZERO;

            for(LotStock lotStock : lotStocks) {

                if (quantiteRestante.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }

                BigDecimal quantiteDisponible = lotStock.getQuantiteRestante();

                if (quantiteDisponible.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                BigDecimal quantiteALever = quantiteDisponible.min(quantiteRestante);

                BigDecimal montantMouvement = quantiteALever.multiply(lotStock.getPrixUnitaireAchat());

                MouvementStock mv = MouvementStock.builder()
                        .produit(ligne.getProduit())
                        .lotStock(lotStock)
                        .typeMouvement(TypeMouvement.SORTIE)
                        .quantite(quantiteALever)
                        .dateMouvement(LocalDateTime.now())
                        .reference(bonSortie.getNumeroBon())
                        .motif(bonSortie.getMotif().name())
                        .build();
                mouvementStockRepository.save(mv);

//                lotStock.setQuantiteRestante(lotStock.getQuantiteRestante().subtract(quantiteALever));
//                lotStock.setQuantiteRestante(lotStock.getQuantiteRestante().subtract(quantiteALever));
                lotStock.consommer(quantiteALever);
                lotStockRepository.save(lotStock);

                quantiteRestante = quantiteRestante.subtract(quantiteALever);

                montantLigne = montantLigne.add(montantMouvement);
            }
            if (quantiteRestante.compareTo(BigDecimal.ZERO) > 0) {
                throw new BusinessException("Stock insuffisant pour le produit : " + ligne.getProduit().getNom());
            }

            Produit produit = ligne.getProduit();
            produit.setStockActuel(produit.getStockActuel().subtract(ligne.getQuantite()));
            produitRepository.save(produit);

            montantTotal = montantTotal.add(montantLigne);
        }
        bonSortie.setMontantTotal(montantTotal);
        bonSortie.setStatut(StatutBonSortie.VALIDE);
        bonSortieRepository.save(bonSortie);
        return  bonSortieMapper.toResponseDTO(bonSortie);
    }
}
