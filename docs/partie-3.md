### Objectifs DevOps
Mettre en place une infrastructure permettant de :

- Conteneuriser l’application
- Orchestrer l’ensemble des services
- Automatiser le cycle de développement :
    - Intégration continue (build, tests, analyse)
    - Déploiement automatisé
    - Validation de la qualité du code
- Garantir la qualité logicielle :
    - Analyse statique du code
    - Détection des vulnérabilités et bugs
    - Mesure de la couverture de tests

---

## Exigences Techniques — Infrastructure

### 1. Conteneurisation avec Docker
L’application et tous ses composants doivent être **conteneurisés** afin de garantir :
- La portabilité
- La reproductibilité
- L’isolation des services

### 2. Orchestration avec Docker Compose
L’infrastructure complète doit être déployable à l’aide d’un **unique fichier** :
- `docker-compose.yml`

### 3. Pipeline CI/CD avec Jenkins
Un pipeline d’**intégration et de déploiement continus** doit être mis en place pour automatiser :
- Le build
- Les tests
- L’analyse de qualité
- Le déploiement

### 4. Analyse de Qualité avec SonarQube
Une plateforme **SonarQube** doit être configurée pour :
- Évaluer la maintenabilité
- Détecter les vulnérabilités
- Identifier les code smells
- Suivre la dette technique

