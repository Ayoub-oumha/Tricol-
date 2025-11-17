# Sprint 3 - Brief 2 : Gestion d'approvisionnement

### Travail en binôme



L’entreprise **Tricol**, spécialisée dans la conception et la fabrication de vêtements professionnels, poursuit la digitalisation de ses processus internes.  
Après la mise en place du module de gestion des fournisseurs, la direction souhaite développer un module complémentaire dédié à la gestion des commandes fournisseurs et du stock avec la valorisation **FIFO**.

Ce module constitue une étape stratégique vers un système complet de gestion des approvisionnements et de la production, permettant un suivi rigoureux des matières premières et des équipements.

---

## Exigences fonctionnelles

### 1. Gestion des Fournisseurs

- CRUD complet (Créer, Consulter, Modifier, Supprimer)
- Recherche et filtrage des fournisseurs
- Informations à gérer : raison sociale, adresse complète, personne de contact, email, téléphone, ville, ICE

---

### 2. Gestion des Produits

- CRUD complet des produits
- Consultation du stock disponible par produit
- Système d’alertes sur seuils minimums
- Informations produits à gérer : référence produit, nom, description, prix unitaire, catégorie, stock actuel, point de commande, unité de mesure

---

### 3. Gestion des Commandes Fournisseurs

- Créer une nouvelle commande fournisseur
- Modifier ou annuler une commande existante
- Consulter la liste de toutes les commandes
- Consulter le détail d’une commande spécifique
- Filtrage par fournisseur, statut, période
- Associer une commande à un fournisseur et à une liste de produits
- Calcul automatique du montant total de la commande
- Statut de la commande : **EN_ATTENTE, VALIDÉE, LIVRÉE, ANNULÉE**
- Réception de commande

---

### 4. Gestion de Stock (FIFO)

- **Mouvements d’entrée** : enregistrement automatique lors de la réception des commandes fournisseurs
- **Mouvements de sortie** : consommation du stock selon FIFO (les plus anciens lots sont utilisés en premier)
- **Traçabilité des lots** : chaque entrée de stock est identifiée par :
    - Numéro de lot unique
    - Date d’entrée
    - Quantité
    - Prix d’achat unitaire
    - Commande fournisseur d’origine

- **Consultation du stock** :
    - Stock disponible par produit
    - Valorisation du stock (FIFO)
    - Historique des mouvements

- **Alertes** : notification lorsque le stock d’un produit passe sous le seuil minimum

---

### 5. Gestion des Bons de Sortie

Les **Bons de Sortie** permettent de gérer les sorties de stock vers les ateliers de production de manière traçable.  
Un bon de sortie est un document qui formalise le prélèvement de produits (matières premières, fournitures) du stock central pour les acheminer vers un atelier spécifique.

**Fonctionnalités :**
- Création de bon de sortie atelier
- Ajout multi-produits avec quantités
- Validation déclenchant automatiquement les sorties
- Annulation possible
- Consultation

**Création de bon de sortie pour atelier avec :**
- Numéro unique de bon
- Date de sortie
- Atelier destinataire
- Liste des produits avec quantités
- Motif de sortie (PRODUCTION, MAINTENANCE, AUTRE)
- Statut (BROUILLON, VALIDÉ, ANNULÉ)

La validation du bon déclenche automatiquement les mouvements de stock FIFO.  
**Traçabilité** : lien entre bon de sortie et mouvements de stock.

---

## Règles métier spécifiques (Méthode FIFO)

1. **Réception de commande** : lors de la validation de réception d’une commande fournisseur, création automatique des lots de stock avec numéro unique et date d’entrée.
2. **Sortie de stock** : l’algorithme FIFO doit :
    - Identifier les lots les plus anciens en premier
    - Consommer les quantités dans l’ordre chronologique
    - Gérer le cas où une sortie nécessite plusieurs lots
    - Mettre à jour les quantités restantes de chaque lot
3. **Valorisation** : le calcul de la valeur du stock doit utiliser les prix d’achat des lots selon leur ordre d’entrée.
4. **Traçabilité** : chaque mouvement doit être enregistré avec référence aux lots concernés.

---



## API REST attendue

### Fournisseurs

- `GET /api/v1/fournisseurs`
- `GET /api/v1/fournisseurs/{id}`
- `POST /api/v1/fournisseurs`
- `PUT /api/v1/fournisseurs/{id}`
- `DELETE /api/v1/fournisseurs/{id}`

---

### Produits

- `GET /api/v1/produits`
- `GET /api/v1/produits/{id}`
- `POST /api/v1/produits`
- `PUT /api/v1/produits/{id}`
- `DELETE /api/v1/produits/{id}`
- `GET /api/v1/produits/{id}/stock` → consulter le stock d’un produit

---

### Commandes Fournisseurs

- `GET /api/v1/commandes` → liste des commandes
- `GET /api/v1/commandes/{id}` → détails d’une commande
- `POST /api/v1/commandes` → créer une nouvelle commande
- `PUT /api/v1/commandes/{id}` → modifier une commande
- `DELETE /api/v1/commandes/{id}` → supprimer une commande
- `GET /api/v1/commandes/fournisseur/{id}` → commandes d’un fournisseur
- `PUT /api/v1/commandes/{id}/reception` → réceptionner une commande (génère les entrées de stock)

---

### Gestion de Stock

- `GET /api/v1/stock` → état global du stock
- `GET /api/v1/stock/produit/{id}` → détail du stock par produit avec lots FIFO
- `GET /api/v1/stock/mouvements` → historique des mouvements
- `GET /api/v1/stock/mouvements/produit/{id}` → mouvements d’un produit spécifique
- `GET /api/v1/stock/alertes` → produits sous le seuil minimum
- `GET /api/v1/stock/valorisation` → valorisation totale du stock (méthode FIFO)

---

### Bons de Sortie

- `GET /api/v1/bons-sortie` → liste de tous les bons
- `GET /api/v1/bons-sortie/{id}` → détails d’un bon
- `POST /api/v1/bons-sortie` → créer un bon (statut BROUILLON)
- `PUT /api/v1/bons-sortie/{id}` → modifier un bon brouillon
- `PUT /api/v1/bons-sortie/{id}/valider` → valider un bon (sorties FIFO)
- `PUT /api/v1/bons-sortie/{id}/annuler` → annuler un bon brouillon
- `GET /api/v1/bons-sortie/atelier/{atelier}` → bons par atelier
