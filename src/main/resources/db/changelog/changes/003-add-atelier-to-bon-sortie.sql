-- liquibase formatted sql

-- changeset ayoub:003-add-atelier-to-bon-sortie
ALTER TABLE bon_sortie ADD COLUMN atelier VARCHAR(100);


-- rollback ALTER TABLE bon_sortie DROP COLUMN atelier;


