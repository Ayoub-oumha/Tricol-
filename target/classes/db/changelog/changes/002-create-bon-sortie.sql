-- liquibase formatted sql

-- changeset ayoub:002-create-bon-sortie-table
CREATE TABLE bon_sortie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_bon VARCHAR(100) NOT NULL UNIQUE,
    date_sortie DATE NOT NULL,
    statut VARCHAR(50),
    motif VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- changeset ayoub:002-create-ligne-bon-sortie-table
CREATE TABLE ligne_bon_sortie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantite DECIMAL(19,2) NOT NULL,
    bon_sortie_id BIGINT NOT NULL,
    produit_id BIGINT NOT NULL,
    CONSTRAINT fk_ligne_bon_sortie_bon_sortie FOREIGN KEY (bon_sortie_id)
        REFERENCES bon_sortie(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ligne_bon_sortie_produit FOREIGN KEY (produit_id)
        REFERENCES produits(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- changeset ayoub:002-create-indexes-bon-sortie
CREATE INDEX idx_bon_sortie_numero ON bon_sortie(numero_bon);
CREATE INDEX idx_bon_sortie_date ON bon_sortie(date_sortie);
CREATE INDEX idx_ligne_bon_sortie_bon ON ligne_bon_sortie(bon_sortie_id);
CREATE INDEX idx_ligne_bon_sortie_produit ON ligne_bon_sortie(produit_id);

-- rollback DROP TABLE IF EXISTS ligne_bon_sortie;
-- rollback DROP TABLE IF EXISTS bon_sortie;

