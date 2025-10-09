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

### Test 4: CHStorage.testShortcuts()
**Intention du test :**
Ce test vérifie le comportement normal de la méthode `shortcutNodeBased()` de la classe `CHStorage`.
L’objectif est de s’assurer que l’ajout de raccourcis dans un stockage node-based fonctionne correctement, que les index sont attribués de manière séquentielle et que les informations stockées (nœuds et poids) sont cohérentes.

**Données de test :**
- Un objet `CHStorage` créé avec un `RAMDirectory`, `edgeBased = false`, et initialisé avec 5 nœuds et 3 raccourcis attendus.
- Deux appels successifs à `shortcutNodeBased()` avec des paramètres valides :
    - Premier raccourci entre les nœuds 0 et 1 avec un poids de 10.5.
    - Deuxième raccourci entre les nœuds 1 et 2 avec un poids de 12.3.
- Lecture du premier raccourci via `toShortcutPointer()` pour valider les données stockées.

**Oracle:**
- Avant tout ajout : `getShortcuts()` retourne 0.
- Après le premier ajout : `getShortcuts()` retourne 1 et l’index retourné vaut 0.
- Après le second ajout : `getShortcuts()` retourne 2 et l’index retourné vaut 1.
- Pour le premier raccourci :
    - `getNodeA(ptr)` doit retourner 0.
    - `getNodeB(ptr)` doit retourner 1.
    - `getWeight(ptr)` doit retourner 10.5.

Ces vérifications confirment que la méthode `shortcutNodeBased()` ajoute correctement les raccourcis, incrémente le compteur interne et stocke fidèlement les informations des nœuds et du poids associé.

### Test 5: BaseGraph.testGetCapacityIncreasesWhenGraphGrows()
**Intention du test :**
Ce test vérifie que la méthode `getCapacity()` de la classe `BaseGraph` retourne une capacité positive après la création du graphe et que cette capacité augmente (ou reste stable) lorsque de nouvelles données sont ajoutées.
L’objectif est de s’assurer que la capacité du stockage reflète bien l’allocation mémoire réelle utilisée par le graphe, et qu’elle ne diminue jamais au cours de son utilisation.

**Données de test :**
- Un objet `BaseGraph` créé avec un `RAMDirectory` et initialisé avec `create(100)`.
- Ajout de plusieurs arêtes via `graph.edge(a, b).setDistance(x)` entre des nœuds successifs.
- Comparaison des capacités avant et après ces ajouts.

**Oracle:**
- Avant tout ajout, `getCapacity()` doit retourner une valeur strictement positive.
- Après l’ajout de plusieurs arêtes, `getCapacity()` doit retourner une valeur supérieure ou égale à la capacité initiale.
- Des appels consécutifs à `getCapacity()` doivent produire la même valeur, garantissant que la méthode n’a pas d’effet de bord.
Ces vérifications confirment que la capacité est calculée correctement et reste cohérente lors de la croissance du graphe.

## Analyse de Mutation - Tests Ajoutés

- **Line Coverage (for mutated classes only):** ###/### (##%)
- **Generated Mutations:** ###
- **Killed Mutations:** ### (##%)
- **Mutations with No Coverage:** ##
- **Test Strength:** ##%
- **Total Tests Run:** ### (#.## tests per mutation)

