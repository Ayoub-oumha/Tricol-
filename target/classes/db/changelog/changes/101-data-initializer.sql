-- liquibase formatted sql

-- changeset ayoub:004-insert-sample-fournisseurs
INSERT INTO fournisseurs (raison_sociale, adresse, ville, personne_contact, email, telephone, ice, created_at, updated_at) VALUES
('TechSupply Morocco', '123 Rue Hassan II', 'Casablanca', 'Ahmed Benali', 'ahmed.benali@techsupply.ma', '+212522123456', 'ICE001234567890123', NOW(), NOW()),
('ElectroDistrib', '45 Avenue Mohammed V', 'Rabat', 'Fatima Alaoui', 'fatima.alaoui@electrodistrib.ma', '+212537987654', 'ICE001234567890124', NOW(), NOW()),
('IndustrialParts SA', '78 Boulevard Zerktouni', 'Marrakech', 'Omar Tazi', 'omar.tazi@industrialparts.ma', '+212524456789', 'ICE001234567890125', NOW(), NOW());

-- changeset ayoub:004-insert-sample-produits
INSERT INTO produits (reference, nom, description, categorie, stock_actuel, point_commande, unite_mesure, created_at, updated_at) VALUES
('PROC001', 'Processeur Intel i7', 'Processeur Intel Core i7 dernière génération pour ordinateurs haute performance',  'Informatique', 50.00, 10, 'Unité', NOW(), NOW()),
('RAM002', 'Mémoire RAM 16GB DDR4', 'Barrette mémoire RAM 16GB DDR4 3200MHz pour serveurs et workstations',  'Informatique', 75.00, 15, 'Unité', NOW(), NOW()),
('CABLE003', 'Câble Ethernet Cat6', 'Câble réseau Ethernet catégorie 6 blindé pour installations professionnelles',  'Réseau', 200.00, 50, 'Mètre', NOW(), NOW()),
('SWITCH004', 'Switch 24 ports Gigabit', 'Commutateur réseau 24 ports Gigabit Ethernet manageable',  'Réseau', 30.00, 5, 'Unité', NOW(), NOW()),
('DISK005', 'Disque SSD 1TB', 'Disque dur SSD 1TB SATA III haute vitesse pour stockage professionnel',  'Stockage', 40.00, 8, 'Unité', NOW(), NOW());



