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
- **Total Tests Run:** 837 (2.12 tests per mutation)

## Nouveaux Tests Ajoutés

### Test 1: CHStorage.testIsClosed()

**Intention du test :**
Ce test vérifie que la méthode `isClosed()` de CHStorage retourne correctement l'état de fermeture du stockage en testant les transitions d'état.

**Données de test :**
- Un `CHStorage` créé avec des paramètres valides (5 nœuds, 3 shortcuts)
- Test des états avant et après avoir appelé `close()`

**Oracle:**
1. **État initial** : `isClosed()` doit retourner `false`
3. **Après fermeture** : `isClosed()` doit retourner `true`

### Test 2: CHStorage.testCreateThrowsExceptions()

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

### Test 3: BaseGraph.testFreezeThrowsExceptions()

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

### Test 4: CHStorage.testMaxShortcutAndLowWeight()
**Intention du test :**
Ce test vérifie que la méthode `shortcutNodeBased()` appelle correctement le consommateur (`LowShortcutWeightConsumer`) lorsqu’un raccourci est créé avec un poids inférieur à la valeur minimale autorisée (`MIN_WEIGHT`).

**Données de test :**
- Un objet `CHStorage` est initialisé avec des paramètres valides (`10` nœuds, `5` raccourcis).
- Un consommateur (`Consumer<LowWeightShortcut>`) est assigné à `CHStorage` afin de détecter si la méthode `.accept()` est appelée.
- Un raccourci est ensuite ajouté avec un poids très faible (`0.0001`), inférieur au seuil minimal.

**Oracle:**
- Lorsque le raccourci est ajouté avec un poids inférieur à `MIN_WEIGHT`, le consommateur doit être appelé.
- L’appel du consommateur est confirmé par la variable `consumerCalled[0]`, qui doit passer à `true`.

### Test 5: BaseGraph.testGetCapacity()
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

### Test 6: BaseGraph.testLoadExistingFailsWhenStoreNotInitialized()
**Intention du test :**
Ce test vérifie le comportement de la méthode `loadExisting()` lorsqu’aucune donnée n’a encore été créée ni sauvegardée. Il s’assure que le graphe réagit correctement à l’absence de fichiers existants en retournant false sans provoquer d’exception.

**Données de test :**
- Un objet `BaseGraph` est créé à partir d’un répertoire mémoire vide (`RAMDirectory`), sans appel préalable à `.create()` ni `.flush()`.
- Aucune donnée de graphe n’est donc disponible à charger.

**Oracle:**
- L’appel à `LoadExisting()` doit retourner `false`, puisque `store.loadExisting()` échoue naturellement dans un répertoire vide.
- Aucune exception ne doit être levée, ce qui valide la gestion propre des cas d’absence de données.

## Analyse de Mutation - Tests Ajoutés

- **Line Coverage (for mutated classes only):** ###/### (##%)
- **Generated Mutations:** ###
- **Killed Mutations:** ### (##%)
- **Mutations with No Coverage:** ##
- **Test Strength:** ##%
- **Total Tests Run:** ### (#.## tests per mutation)

