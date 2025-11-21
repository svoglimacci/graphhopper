# IFT3913 - Tâche 3 - Rapport
### Simon Voglimacci & Célina Zhang

## 1. Modification workflow Github Actions
**1.1 : déclenchement des tests de mutation avec PITEST** <br>

Un test de mutation est déclenché à l’aide de PITEST.
PIT génère un rapport HTML dans lequel figure le score global de mutation.
Pour optimiser le temps d’exécution, cette étape n’est exécutée que pour Java 24 dans la matrice de builds du CI.

**1.2 : extraction du score** <br>

Après l’exécution de PIT, le score de mutation est extrait du rapport HTML généré. <br>
Cela est réalisé à l’aide de commandes UNIX : <br>
- `find` pour localiser index.html
- `sed` pour extraire la section du tableau HTML
- `grep` pour isoler le pourcentage de mutation

Ces outils permettent de récupérer une valeur numérique exploitable par le workflow.

**1.3 : restauration de la baseline précédente** <br>

Le workflow tente ensuite de restaurer un fichier mutation-baseline.txt en utilisant :
actions/cache/restore@v4. <br>
Deux cas se présentent :
- baseline trouvée → elle sert de référence pour le commit courant
- baseline absente → le score actuel devient la nouvelle baseline

Ce mécanisme permet de conserver l’historique du score de mutation d’une exécution à l’autre.

**1.4 : comparaison des scores** <br>

Une comparaison est effectuée entre :
- le score précédent (baseline)
- le score actuel (extrait du rapport PIT)

Si le score de mutation diminue, le workflow échoue immédiatement. Cela empêche toute régression dans la qualité et la pertinence des tests.

**1.5 : mise à jour et sauvegarde du nouveau score** <br>

Lorsque le commit est poussé sur master, le score courant est :
1. Écrit dans mutation-baseline.txt
2. Sauvegardé en cache via actions/cache/save@v4

Cette opération met à jour la baseline pour les prochaines exécutions CI.

**1.6 : validation de la solution** <br>

La fonctionnalité a été validée en créant volontairement un commit dans lequel certains tests étaient désactivés. <br>
Cela a eu pour effet de diminuer le score de mutation et de vérifier que :
- la diminution est détectée
- le workflow échoue correctement

## 2. Ajout des tests Mockito
**2.1 : choix des classes testées et justification** <br>

Pour cette tâche, nous avons choisi de tester deux méthodes de la classe `CHStorage` :
1. `getCapacity()`
2. la méthode statique `CHStorage.fromGraph(mockBaseGraph, mockCHConfig)`

Ces méthodes ont été sélectionnées à partir du rapport Jacoco, car leurs branches internes présentaient un niveau de couverture relativement bas. Les tester avec Mockito permettait d’augmenter significativement la couverture tout en isolant le comportement de CHStorage.

**2.2 : test 1 - `testGetCapacityWithMockito()`** <br>

Classe testée
- `CHStorage` <br>
Méthode : `getCapacity()`

Objectif du test <br>
Vérifier que la méthode `getCapacity()` retourne correctement la somme des capacités de ses deux DataAccess internes :
- `nodesCH`
- `shortcuts`

Classes simulées (mockées)
- `Directory` → simulée pour éviter de manipuler un vrai système de fichiers et intercepter les appels à create.
- `DataAccess` → simulée pour contrôler exactement les valeurs retournées par `getCapacity()`.

Définition des mocks
- Le mock de `Directory` renvoie nos propres mocks de `DataAccess` lorsqu’un `DataAccess` doit être créé.
- Les mocks de `DataAccess` sont configurés pour retourner : <br>
→ 100L pour nodesCH <br>
→ 200L pour shortcuts

Choix des valeurs simulées
- 100L + 200L = 300L <br>
Ce choix permet de vérifier simplement que `CHStorage` additionne correctement les deux capacités internes sans interférences liées à la structure réelle du stockage.

**2.3 : test 2 - `testFromGraphWithMockito()`** <br>

Classe testée
- `CHStorage` <br>
Méthode : `fromGraph(BaseGraph, CHConfig)`

Objectif du test <br>
Valider le comportement de la création d’un objet CHStorage à partir :
- d’un `BaseGraph` existant
- d’une configuration `CHConfig`

Le test couvre :
- le cas normal (graphe frozen et cohérent)
- le cas d’erreur (graphe non frozen → exception attendue)

Classes simulées (mockées)
- `BaseGraph` → simulé pour contrôler son état (frozen/non frozen), son nombre de nœuds et d’arêtes, sans dépendre d’une instance complète.
- `CHConfig` → simulée pour fournir un nom et le mode edge-based de façon déterministe.
- `Directory` → comme pour le test précédent, permet de contrôler totalement la création des DataAccess.
- `DataAccess` → simulé pour la configuration interne du CHStorage.

Définition des mocks
- `BaseGraph` : <br>
→ `isFrozen()` : true pour permettre la création <br>
→ nœuds : 5 <br>
→ arêtes : 10
- `CHConfig` : <br>
→ `getName()` : `"mockGraph"` <br>
→ `isEdgeBased()` : valeur choisie selon le scénario
- `Directory` : renvoie des mocks de DataAccess
- `DataAccess` : comme dans le test précédent, ils sont simulés afin d’éviter tout accès réel au stockage

Choix des valeurs simulées
- `isFrozen = true` : condition obligatoire pour créer un CHStorage valide
- `isFrozen = false` : utilisé pour tester l’exception
- `nodes = 5`, `edges = 10` : valeurs simples et cohérentes pour valider le comportement normal
- `"mockGraph"` : un nom explicite pour vérifier la configuration

Ces valeurs ont été choisies pour garantir :
- la reproductibilité du test
- la couverture des branches principales
- la validation des comportements normaux et anormaux

## 3. Ajout des tests Mockito