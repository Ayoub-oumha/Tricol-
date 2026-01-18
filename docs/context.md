# Gestion de Stock Tricol - Documentation Technique

## Contexte du Projet

Tricol, entreprise spécialisée dans la fabrication de vêtements professionnels, a développé un système de gestion de stock basé sur la méthode **FIFO** (First-In, First-Out). Ce système gère :

- Les réceptions de commandes fournisseurs
- Les lots de stock
- Les sorties de stock via des bons de sortie

### Problématique Actuelle
Le module de gestion de stock fonctionne mais **manque de tests unitaires** pour garantir la fiabilité de la logique métier critique. L'équipe de direction souhaite également disposer de **fonctionnalités d'audit avancées** pour tracer et analyser les mouvements de stock.

---

## Partie 1 : Tests Unitaires de la Gestion de Stock

### Tâche 1.1 : Tests du service de Stock et FIFO

Développer des tests unitaires (JUnit + Mockito) pour la couche Service.

#### A. Mécanisme de Sortie de Stock FIFO

**Scénarios de test :**

- **Scénario 1** : Sortie simple consommant partiellement un seul lot
- **Scénario 2** : Sortie nécessitant la consommation de plusieurs lots successifs
- **Scénario 3** : Sortie avec stock insuffisant (gestion d'erreur)
- **Scénario 4** : Sortie épuisant exactement le stock disponible

#### B. Création Automatique de Lot

**Points de vérification :**

- Création automatique d'un lot de stock traçable lors de la validation d'une réception de commande fournisseur
- Contrôle de la génération :
    - Numéro de lot
    - Date d'entrée
    - Prix d'achat unitaire
- Vérification du lien entre le lot créé et la réception fournisseur

#### C. Calcul de Valorisation du Stock

**Tests à implémenter :**

- Calcul de la valeur totale du stock (quantités restantes × prix d'achat unitaires)
- Vérification selon la méthode FIFO (valorisation basée sur les lots les plus anciens)
- Tests avec plusieurs lots à prix différents

### Tâche 1.2 : Tests des Transitions de Statut

**Workflows de validation à tester :**

Vérifier que la validation d'un bon de sortie (BROUILLON → VALIDÉ) déclenche automatiquement :

- Création des mouvements de stock correspondants
- Mise à jour des quantités restantes dans les lots
- Enregistrement des informations de validation (utilisateur, date)

---

## Partie 2 : Recherche Avancée sur les Mouvements de Stock

### Tâche 2.1 : Implémentation avec Spring Data JPA Specifications

**Approche recommandée :** Utiliser Spring Data JPA Specifications ou la Criteria API de JPA pour construire dynamiquement des requêtes complexes basées sur plusieurs critères de recherche.

### Tâche 2.2 : Critères de Recherche à Implémenter

L'endpoint `GET /api/v1/stock/mouvements` doit accepter les paramètres de requête :

| Paramètre | Description |
|-----------|-------------|
| `dateDebut`, `dateFin` | Filtrage sur la date du mouvement (intervalle) |
| `produitId`, `reference` | Filtrage par produit |
| `type` | Type de mouvement (ENTREE/SORTIE) |
| `numeroLot` | Recherche par numéro de lot |

### Tâche 2.3 : Intégration de la Pagination

**Paramètres obligatoires :**
- `page` : numéro de page (débute à 0)
- `size` : nombre d'éléments par page

### Exemples d'Appels API

#### Recherche par produit et type avec pagination
```http
GET /api/v1/stock/mouvements?produitId=123&type=SORTIE&page=0&size=10
```

#### Recherche par période
```http
GET /api/v1/stock/mouvements?dateDebut=2025-01-01&dateFin=2025-03-31
```

#### Recherche par numéro de lot
```http
GET /api/v1/stock/mouvements?numeroLot=LOT-2025-001
```

#### Recherche combinée multi-critères
```http
GET /api/v1/stock/mouvements?reference=PROD001&type=ENTREE&dateDebut=2025-01-01&page=0&size=20
```

---

## Objectifs d'Apprentissage

1. **Développer une suite de tests unitaires** couvrant la logique métier critique :
    - Algorithme FIFO
    - Création de lots
    - Valorisation du stock

2. **Implémenter une API de recherche avancée** multi-critères pour faciliter l'audit des mouvements de stock