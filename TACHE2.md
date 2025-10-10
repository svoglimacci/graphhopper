# IFT3913 - Tâche 2 - Rapport
### Simon Voglimacci & Célina Zhang

## Classe Choisie : 
- com.graphhopper.storage.BaseGraph
- com.graphhopper.storage.CHStorage

## Analyse de Mutation - Tests Originaux

- **Line Coverage (for mutated classes only):** 320/480 (67%)
- **Generated Mutations:** 381
- **Killed Mutations:** 196 (51%)
- **Mutations with No Coverage:** 116
- **Test Strength:** 74%
- **Total Tests Run:** 801 (2.1 tests per mutation)

## Nouveaux Tests Ajoutés

### Test 1: CHStorageTest.testIsClosed()

**Intention du test :**
Ce test vérifie que la méthode `isClosed()` de CHStorage retourne correctement l'état de fermeture du stockage en testant les transitions d'état.

**Données de test :**
- Un `CHStorage` créé avec des paramètres valides (5 nœuds, 3 shortcuts)
- Test des états avant et après avoir appelé `close()`

**Oracle:**
1. **État initial** : `isClosed()` doit retourner `false`
3. **Après fermeture** : `isClosed()` doit retourner `true`

### Test 2: CHStorageTest.testCreateThrowsExceptions()

**Intention du test :**
Ce test vérifie que la méthode `create()` de la classe CHStorage gère correctement les cas invalides en lançant une exception lorsqu’un nœud négatif est fourni alors que le nombre de nœuds (nodeCount) est valide.
L’objectif est de s’assurer que la méthode empêche la création d’un stockage incohérent et respecte les contraintes de validité sur les paramètres d’entrée.

**Données de test :**
- `nodeCount` = 5 : valeur valide et positive (cas normal)
- `node` = -1 : aleur négative, choisie pour tester la borne inférieure
Ces valeurs permettent d’isoler le comportement fautif sur le paramètre node tout en gardant nodeCount cohérent.

**Oracle:**
- La méthode doit lever une exception de type `IllegalArgumentException`.
- Le message de l’exception doit indiquer que le paramètre `node` ou `nodeCount` est invalide.
- Si aucune exception n’est levée, le test échoue (car cela signifierait que la méthode ne valide pas correctement ses entrées).

### Test 3: BaseGraphTest.testFreezeThrowsExceptions()

**Intention du test :**
Ce test vérifie que la méthode `freeze()` de la classe BaseGraph respecte les contraintes de cohérence internes en empêchant un double gel du graphe.
L’objectif est de s’assurer qu’un graphe déjà gelé ne puisse pas être gelé à nouveau et que la méthode signale correctement cette erreur via une exception.

**Données de test :**
- Un objet `BaseGraph` créé avec un `RAMDirectory` et un `EncodingManager` valides.
- Premier appel à `freeze()` pour passer le graphe dans l’état “gelé”.
- Second appel à `freeze()` immédiatement après, simulant une tentative de gel multiple.
Ces données testent explicitement la condition if (`isFrozen()`) présente dans la méthode.

**Oracle:**
- Premier appel : `freeze()` s’exécute sans erreur, et `isFrozen()` retourne true.
- Deuxième appel : `freeze()` doit lever une `IllegalStateException` avec le message `"base graph already frozen"`.
- Si aucune exception n’est levée au deuxième appel, le test est considéré comme échoué, car le comportement attendu n’est pas respecté.

### Test 4: CHStorageTest.testMaxShortcutAndLowWeight()
**Intention du test :**
Ce test vérifie que la méthode `shortcutNodeBased()` appelle correctement le consommateur (`LowShortcutWeightConsumer`) lorsqu’un raccourci est créé avec un poids inférieur à la valeur minimale autorisée (`MIN_WEIGHT`).

**Données de test :**
- Un objet `CHStorage` est initialisé avec des paramètres valides (`10` nœuds, `5` raccourcis).
- Un consommateur (`Consumer<LowWeightShortcut>`) est assigné à `CHStorage` afin de détecter si la méthode `.accept()` est appelée.
- Un raccourci est ensuite ajouté avec un poids très faible (`0.0001`), inférieur au seuil minimal.

**Oracle:**
- Lorsque le raccourci est ajouté avec un poids inférieur à `MIN_WEIGHT`, le consommateur doit être appelé.
- L’appel du consommateur est confirmé par la variable `consumerCalled[0]`, qui doit passer à `true`.

### Test 5: BaseGraphTest.testGetCapacity()
**Intention du test :**
Ce test vérifie que la méthode `getCapacity()` de la classe `BaseGraph` calcule correctement la capacité totale du graphe, en tenant compte de la présence ou non d’un stockage de coûts de virage (turn cost storage).

**Données de test :**
- Deux instances de BaseGraph sont créées à l’aide du Builder :
    - l’une avec `withTurnCosts(true)`
    - l’autre avec `withTurnCosts(false)`
- Dans les deux graphes, une arête simple est ajoutée entre les nœuds `0` et `1`.

**Oracle:**
- La capacité du graphe avec coûts de virage (`capacityWith`) doit être supérieure à celle du graphe sans (`capacityWithout`).
- La différence entre les deux capacités doit correspondre exactement à la capacité du `TurnCostStorage` du graphe avec coûts de virage.

### Test 6: BaseGraphTest.testLoadExistingFailsWhenStoreNotInitialized()
**Intention du test :**
Ce test vérifie le comportement de la méthode `loadExisting()` lorsqu’aucune donnée n’a encore été créée ni sauvegardée. Il s’assure que le graphe réagit correctement à l’absence de fichiers existants en retournant false sans provoquer d’exception.

**Données de test :**
- Un objet `BaseGraph` est créé à partir d’un répertoire mémoire vide (`RAMDirectory`), sans appel préalable à `.create()` ni `.flush()`.
- Aucune donnée de graphe n’est donc disponible à charger.

**Oracle:**
- L’appel à `LoadExisting()` doit retourner `false`, puisque `store.loadExisting()` échoue naturellement dans un répertoire vide.
- Aucune exception ne doit être levée, ce qui valide la gestion propre des cas d’absence de données.

### Test 7: BaseGraphTest.testGetAllEdges()
**Intention du test :**
Ce test vérifie que la méthode `getAllEdges()` de la classe `BaseGraph` retourne correctement toutes les arêtes du graphe, en s’assurant que le nombre d’arêtes retournées correspond au nombre d’arêtes ajoutées.

**Données de test :**
- Un objet `BaseGraph` est créé avec un `RAMDirectory` et un `EncodingManager` valides.
- 5 arêtes sont ajoutées au noeud 0.
- Un itérateur `AllEdgesIterator' obtenu via `getAllEdges()`.

**Oracle:**
- La méthode `getAllEdges()` doit retourner un itérateur contenant exactement 5 arêtes.
- Le nombre d’arêtes retournées par l’itérateur doit être égal au nombre d’arêtes ajoutées.

### Test Java Faker : CHStorage.testToDetailsString()
**Intention du test :**
Ce test vérifie que la méthode `toDetailsString()` de la classe `CHStorage` retourne la chaîne de caractères attendue, en utilisant des données générées aléatoirement.
Cela permet de s’assurer que la méthode formate correctement les détails du stockage, même avec des valeurs dynamiques.

**Données de test :**
- Un objet `CHStorage` est créé avec des valeurs aléatoires pour `nodeCount` et `shortcutCount` en utilisant la bibliothèque Java Faker.
- Un appel à `toDetailsString()` est effectué pour obtenir la chaîne de détails.

**Oracle:**
- La chaîne retournée par `toDetailsString()` doit correspondre au format attendu, incluant les valeurs aléatoires générées pour `nodeCount` et `shortcutCount`.
- Le test vérifie que la chaîne contient les sous-chaînes "shortcuts: ", "nodesCH: " et "MB" suivies des valeurs correctes.
- Le test échoue si la chaîne ne correspond pas au format attendu ou si les valeurs ne sont pas correctement incluses.




## Analyse de Mutation - Tests Ajoutés

- **Line Coverage (for mutated classes only):** 336/480 (70%)
- **Generated Mutations:** 381
- **Killed Mutations:** 211 (55%)
- **Mutations with No Coverage:** 101
- **Test Strength:** 75%
- **Total Tests Run:** 914 (2.4 tests per mutation)

## Nouveaux mutants détectés
La nouvelle analyse de mutation a permis de détecter 15 mutants supplémentaires qui n'étaient pas détectés par les tests originaux:

### CHStorage - Mutants détectés:
- **[180] Removed call to DataAccess::close** (SURVIVED → KILLED)
    - Détecté par: `testIsClosed()` - vérifie l'état après fermeture

- **[205] Negated conditional** (NO_COVERAGE → KILLED)
    - Détecté par: `testCreateThrowsExceptions()` - teste les validations de paramètres

- **[206] Removed call to Consumer::accept** (NO_COVERAGE → KILLED)
    - Détecté par: `testMaxShortcutAndLowWeight()` - vérifie l'appel du consommateur

- **[393] Replaced long division with multiplication** (NO_COVERAGE → KILLED)
    - Détecté par: `testToDetailsString()` - teste le formatage des détails

-  **[393b] Replaced return value with "" in toDetailsString** (NO_COVERAGE → KILLED)
    - Détecté par: `testToDetailsString()` - vérifie le contenu de la chaîne retournée

-  **[394] Replaced long division with multiplication** (NO_COVERAGE → KILLED)
    - Détecté par: `testToDetailsString()` - teste les calculs dans les détails

- **[399] Replaced boolean return with false in isClosed** (NO_COVERAGE → KILLED)
    - Détecté par: `testIsClosed()` - vérifie l'état de fermeture

- **[399b] Replaced boolean return with true in isClosed** (NO_COVERAGE → KILLED)
    - Détecté par: `testIsClosed()` - teste les transitions d'état

- **[402] Replaced long addition with subtraction in getNodes** (NO_COVERAGE → KILLED)
    - Détecté par: `testToDetailsString()` - vérifie les calculs de nœuds

-  **[407] Removed call to DataAccess::setHeader** (NO_COVERAGE → KILLED)
    - Détecté par: `testCreateThrowsExceptions()` - teste la création avec paramètres invalides

### BaseGraph - Mutants détectés:

- **[74] Negated conditional** (SURVIVED → KILLED)
    - Détecté par: `testFreezeThrowsExceptions()` - vérifie la double congélation

- **[240] Replaced long return with 0 in getCapacity** (NO_COVERAGE → KILLED)
    - Détecté par: `testGetCapacity()` - teste le calcul de capacité

- **[241] Replaced long addition with subtraction (3rd)** (NO_COVERAGE → KILLED)
    - Détecté par: `testGetCapacity()` - vérifie les calculs de capacité avec turn costs

- **[252] Replaced boolean return with true in loadExisting** (NO_COVERAGE → KILLED)
    - Détecté par: `testLoadExistingFailsWhenStoreNotInitialized()` - teste le chargement sans données

- **[426] Replaced return value with null in getTurnCostStorage** (NO_COVERAGE → KILLED)
    - Détecté par: `testGetAllEdges()` - vérifie l'itération sur toutes les arêtes
