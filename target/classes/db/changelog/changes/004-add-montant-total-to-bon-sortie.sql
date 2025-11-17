-- liquibase formatted sql

-- changeset ayoub:004-add-montant-total-to-bon-sortie
ALTER TABLE bon_sortie ADD COLUMN montant_total DECIMAL(19,2);


-- rollback ALTER TABLE bon_sortie DROP COLUMN montant_total;