package org.tricol.supplierchain.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.entity.Produit;
import org.tricol.supplierchain.repository.ProduitRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSeeder implements CommandLineRunner {

    private final ProduitRepository produitRepository;

    @Override
    public void run(String... args) {
        log.info("Starting product seeding...");

        seedProduits();

        log.info("Product seeding completed.");
    }

    private void seedProduits() {
        if (produitRepository.count() > 0) {
            log.info("Produits already seeded, skipping...");
            return;
        }

        List<Produit> samples = Arrays.asList(
                buildProduit("PRD-1001", "Vis M8", "Vis M8 zincée", new BigDecimal("150"), new BigDecimal("20"), "pcs", "Fixation"),
                buildProduit("PRD-1002", "Écrou M8", "Écrou hexagonal M8", new BigDecimal("300"), new BigDecimal("50"), "pcs", "Fixation"),
                buildProduit("PRD-2001", "Plaque acier 200x100", "Plaque en acier galvanisé", new BigDecimal("25"), new BigDecimal("5"), "pcs", "Matériaux"),
                buildProduit("PRD-3001", "Bidon huile 5L", "Huile hydraulique 5 litres", new BigDecimal("40"), new BigDecimal("10"), "L", "Consommables"),
                buildProduit("PRD-4001", "Fil électrique 2.5mm²", "Rouleau de fil électrique 100m", new BigDecimal("10"), new BigDecimal("2"), "roll", "Électrique")
        );

        for (Produit p : samples) {
            if (!produitRepository.existsByReference(p.getReference())) {
                produitRepository.save(p);
                log.info("Seeded produit: {} ({})", p.getNom(), p.getReference());
            } else {
                log.info("Produit {} already exists, skipping", p.getReference());
            }
        }
    }

    private Produit buildProduit(String reference, String nom, String description,
                                  BigDecimal stockActuel, BigDecimal pointCommande,
                                  String uniteMesure, String categorie) {
        return Produit.builder()
                .reference(reference)
                .nom(nom)
                .description(description)
                .stockActuel(stockActuel)
                .pointCommande(pointCommande)
                .uniteMesure(uniteMesure)
                .categorie(categorie)
                .build();
    }
}

