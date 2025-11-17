-- liquibase formatted sql

-- changeset ayoub:001-create-fournisseur-table
CREATE TABLE fournisseurs (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              raison_sociale VARCHAR(255) NOT NULL,
                              adresse VARCHAR(500) NOT NULL,
                              ville VARCHAR(100) NOT NULL,
                              personne_contact VARCHAR(255) NOT NULL,
                              email VARCHAR(255) NOT NULL UNIQUE,
                              telephone VARCHAR(255) NOT NULL,
                              ice VARCHAR(255) NOT NULL UNIQUE,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- changeset ayoub:001-create-produit-table
CREATE TABLE produits (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          reference VARCHAR(50) NOT NULL UNIQUE,
                          nom VARCHAR(255) NOT NULL,
                          description LONGTEXT,
                          categorie VARCHAR(100) NOT NULL,
                          stock_actuel DECIMAL(19,2) NOT NULL,
                          point_commande DECIMAL(19,2) NOT NULL,
                          unite_mesure VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- changeset ayoub:001-create-commande-fournisseur-table
CREATE TABLE commande_fournisseur (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      numero_commande VARCHAR(100) NOT NULL UNIQUE,
                                      date_commande TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      date_livraison_prevue DATE,
                                      date_livraison_effective DATE,
                                      date_reception TIMESTAMP NULL DEFAULT NULL,
                                      statut VARCHAR(20) NOT NULL,
                                      montant_total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                                      fournisseur_id BIGINT NOT NULL,
                                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_commande_fournisseur FOREIGN KEY (fournisseur_id)
                                          REFERENCES fournisseurs(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- changeset ayoub:001-create-ligne-commande-table
CREATE TABLE lignes_commande (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 quantite DECIMAL(10,2) NOT NULL,
                                 prix_unitaire DECIMAL(10,2) NOT NULL,
                                 montant_total DECIMAL(12,2) NOT NULL,
                                 commande_id BIGINT NOT NULL,
                                 produit_id BIGINT NOT NULL,
                                 CONSTRAINT fk_ligne_commande FOREIGN KEY (commande_id)
                                     REFERENCES commande_fournisseur(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                 CONSTRAINT fk_ligne_produit FOREIGN KEY (produit_id)
                                     REFERENCES produits(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- changeset ayoub:001-create-lot-stock-table
CREATE TABLE lot_stock (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           numero_lot VARCHAR(255) NOT NULL UNIQUE,
                           quantitee_initiale DECIMAL(19,2) NOT NULL,
                           quantite_restante DECIMAL(19,2) NOT NULL,
                           prix_achat_unitatire DECIMAL(19,2) NOT NULL,
                           date_entree TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           statut VARCHAR(20),
                           produit_id BIGINT NOT NULL,
                           commande_id BIGINT NOT NULL,
                           CONSTRAINT fk_lot_produit FOREIGN KEY (produit_id)
                               REFERENCES produits(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                           CONSTRAINT fk_lot_commande FOREIGN KEY (commande_id)
                               REFERENCES commande_fournisseur(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- changeset ayoub:001-create-mouvements-stock-table
CREATE TABLE mouvements_stock (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  type_mouvement VARCHAR(255) NOT NULL,
                                  quantite DECIMAL(19,2) NOT NULL,
                                  date_mouvement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  code_reference VARCHAR(255) NOT NULL,
                                  motif VARCHAR(255) NOT NULL,
                                  produit_id BIGINT NOT NULL,
                                  lot_stock_id BIGINT,
                                  CONSTRAINT fk_mouvement_produit FOREIGN KEY (produit_id)
                                      REFERENCES produits(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                                  CONSTRAINT fk_mouvement_lot FOREIGN KEY (lot_stock_id)
                                      REFERENCES lot_stock(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- indexes (unchanged)
CREATE INDEX idx_fournisseur_ice ON fournisseurs(ice);
CREATE INDEX idx_fournisseur_ville ON fournisseurs(ville);

CREATE INDEX idx_produit_reference ON produits(reference);
CREATE INDEX idx_produit_categorie ON produits(categorie);
CREATE INDEX idx_produit_stock_actuel ON produits(stock_actuel);

CREATE INDEX idx_commande_numero ON commande_fournisseur(numero_commande);
CREATE INDEX idx_commande_statut ON commande_fournisseur(statut);
CREATE INDEX idx_commande_fournisseur ON commande_fournisseur(fournisseur_id);
CREATE INDEX idx_commande_date ON commande_fournisseur(date_commande);

CREATE INDEX idx_ligne_commande ON lignes_commande(commande_id);
CREATE INDEX idx_ligne_produit ON lignes_commande(produit_id);

CREATE INDEX idx_lot_produit_statut ON lot_stock(produit_id, statut);
CREATE INDEX idx_lot_date_entree ON lot_stock(date_entree);
CREATE INDEX idx_lot_numero ON lot_stock(numero_lot);
CREATE INDEX idx_lot_commande ON lot_stock(commande_id);

CREATE INDEX idx_mouvement_produit ON mouvements_stock(produit_id);
CREATE INDEX idx_mouvement_lot ON mouvements_stock(lot_stock_id);
CREATE INDEX idx_mouvement_reference ON mouvements_stock(code_reference);
CREATE INDEX idx_mouvement_date ON mouvements_stock(date_mouvement);
CREATE INDEX idx_mouvement_type ON mouvements_stock(type_mouvement);
