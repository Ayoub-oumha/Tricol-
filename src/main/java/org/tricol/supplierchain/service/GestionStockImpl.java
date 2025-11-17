package org.tricol.supplierchain.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.dto.request.CommandeFournisseurCreateDTO;
import org.tricol.supplierchain.dto.request.LigneCommandeCreateDTO;
import org.tricol.supplierchain.dto.response.*;
import org.tricol.supplierchain.entity.*;
import org.tricol.supplierchain.enums.StatutLot;
import org.tricol.supplierchain.exception.ResourceNotFoundException;
import org.tricol.supplierchain.mapper.LotStockMapper;
import org.tricol.supplierchain.mapper.MouvementStockMapper;
import org.tricol.supplierchain.mapper.StockMapper;
import org.tricol.supplierchain.repository.*;
import org.tricol.supplierchain.service.inter.CommandeFournisseurService;
import org.tricol.supplierchain.service.inter.GestionStockService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class GestionStockImpl implements GestionStockService {


    private final StockMapper stockMapper;
    private final LotStockMapper lotStockMapper;
    private final MouvementStockMapper mouvementStockMapper;
    private final ProduitRepository produitRepository;
    private final LotStockRepository lotStockRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final CommandeFournisseurService commandeFournisseurService;
    private final CommandeFournisseurRepository commandeFournisseurRepository;
    private final FournisseurRepository fournisseurRepository;
    private final LigneCommandeRepository ligneCommandeRepository;


    @Override
    @Transactional(readOnly = true)
    public StockGlobalResponseDTO getStockGlobal() {

        List<Produit> produits = produitRepository.findAll();
        Long nombreProduitEnAlerte = produits
                .stream()
                .filter(this::isEnAlerte)
                .count();

        List<StockProduitResponseDTO> stockProduits = produits
                .stream()
                .map(this::buildStockProduitResponse)
                .toList();

       StockGlobalResponseDTO globaleResponse = StockGlobalResponseDTO
               .builder()
               .produits(stockProduits)
               .valorisationTotale(calculerValorisationTotal())
               .nombreProduitsTotal(stockProduits.size())
               .nombreProduitsEnAlerte(nombreProduitEnAlerte)
               .nombreLotsActifs(lotStockRepository.countLotStockByStatut(StatutLot.ACTIF))
               .build();

        return globaleResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public StockProduitResponseDTO getStockByProduit(Long produitId) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(()->new ResourceNotFoundException("Pas de produit avec cette ID: "+ produitId));
        return buildStockProduitResponse(produit);
    }

    @Override
    public List<MouvementStockResponseDTO> getHistoriqueMouvements() {

        return mouvementStockRepository.findAll()
                .stream()
                .map(mouvementStockMapper::toResponseDTO)
                .sorted(Comparator.comparing(MouvementStockResponseDTO::getDateMouvement))
                .toList();
    }

    @Override
    public List<MouvementStockResponseDTO> getMouvementsByProduit(Long produitId) {
        return mouvementStockRepository.findAll()
                .stream()
                .map(mouvementStockMapper::toResponseDTO)
                .filter(m->m.getProduitId().equals(produitId))
                .sorted(Comparator.comparing(MouvementStockResponseDTO::getDateMouvement))
                .toList();
    }

    @Override
    public BigDecimal getValorisationTotale() {
        return calculerValorisationTotal();
    }

    @Override
    public boolean isStockSuffisant(Long produitId, BigDecimal quantiteRequise) {
        return false;
    }

    @Override
    public BigDecimal getQuantiteDisponible(Long produitId) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(()->new ResourceNotFoundException("Produit non trouve"));
        return produit.getStockActuel();
    }

    @Override
    public List<CommandeFournisseurResponseDTO> createCommandeFournisseurEnCasUrgente(List<DeficitStockResponseDTO> deficits) {

        Map<Long, List<DeficitStockResponseDTO>> deficitsByFournisseur = deficits
                .stream()
                .filter(deficit -> deficit.getFournisseurId() != null)
                .collect(Collectors.groupingBy(DeficitStockResponseDTO::getFournisseurId));

        List<CommandeFournisseurResponseDTO> commandesCree = new ArrayList<>();

        deficitsByFournisseur.forEach( (key, value) -> {
            List<LigneCommandeCreateDTO> lignes = value
                    .stream()
                    .map(deficit -> LigneCommandeCreateDTO
                            .builder()
                            .prixUnitaire(getDernierePrixAchat(key, deficit.getProduitId()))
                            .produitId(deficit.getProduitId())
                            .quantite(deficit.getQuantiteManquante().add(new BigDecimal("100.00")))
                            .build()
                    ).toList();
            CommandeFournisseurCreateDTO commandeDTO = CommandeFournisseurCreateDTO
                    .builder()
                    .fournisseurId(key)
                    .dateLivraisonPrevue(LocalDate.now().plusDays(1))
                    .lignes(lignes)
                    .build();
            CommandeFournisseurResponseDTO commande = commandeFournisseurService.createCommande(commandeDTO);
            commandesCree.add(commande);
        });
        return commandesCree;
    }


    @Override
    public List<DeficitStockResponseDTO> verifyStockPourBonSortie(BonSortie bonSortie){
        List<DeficitStockResponseDTO> deficits = new ArrayList<>();

        for (LigneBonSortie ligne : bonSortie.getLigneBonSorties()){
            BigDecimal stockDisponible = getQuantiteDisponible(ligne.getProduit().getId());
            if (stockDisponible.compareTo(ligne.getQuantite()) < 0){
                BigDecimal quantiteManquante = ligne.getQuantite().subtract(stockDisponible);
                DeficitStockResponseDTO deficit = DeficitStockResponseDTO
                        .builder()
                        .produitId(ligne.getProduit().getId())
                        .nomProduit(ligne.getProduit().getNom())
                        .referenceProduit(ligne.getProduit().getReference())
                        .quantiteDemandee(ligne.getQuantite())
                        .quantiteDisponible(stockDisponible)
                        .quantiteManquante(quantiteManquante)
                        .fournisseurId(getFournisseursSuggeresPourProduit(ligne.getProduit().getId()).getId())
                        .build();
                deficits.add(deficit);
            }
        }
        return deficits;
    }

    @Override
    public Fournisseur getFournisseursSuggeresPourProduit(Long produitId){
        List<CommandeFournisseur> commandePassees = commandeFournisseurRepository.findCommandesAvecProduit(produitId);
        return commandePassees
                .stream()
                .map(CommandeFournisseur::getFournisseur)
                .distinct()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Pas de Fournisseur Pour ce produit"));
    }

    private StockProduitResponseDTO buildStockProduitResponse(Produit produit){

        StockProduitResponseDTO produitResponse = stockMapper.toStockProduitDto(produit);

        List<LotStock> actifsLots = lotStockRepository.findByProduitIdAndStatutOrderByDateEntreeAsc(produit.getId(), StatutLot.ACTIF);

        produitResponse.setLots(actifsLots
                .stream()
                .map(lotStockMapper::toResponseDTO)
                .toList()
        );

        BigDecimal valorisation = actifsLots
                .stream()
                .map(lot -> lot.getQuantiteRestante().multiply(lot.getPrixUnitaireAchat()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        produitResponse.setValorisationTotale(valorisation);
        produitResponse.setAlerteSeuil(isEnAlerte(produit));
        produitResponse.setStockTotal(getQuantiteDisponible(produit.getId()));


        return produitResponse;
    }

    private boolean isEnAlerte(Produit produit){
        return produit.getStockActuel().compareTo(produit.getPointCommande()) <= 0;
    }

    private BigDecimal calculerValorisationTotal(){
        return lotStockRepository.findByStatut(StatutLot.ACTIF)
                .stream()
                .map(lot->lot.getQuantiteRestante().multiply(lot.getPrixUnitaireAchat()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getDernierePrixAchat(Long produitId,Long fournisseurId){
        return ligneCommandeRepository
                .findDernierPrixAchat(produitId, fournisseurId)
                .orElse(BigDecimal.ZERO);
    }
}
