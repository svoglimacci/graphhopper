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

## Analyse de Mutation - Tests Ajoutés

- **Line Coverage (for mutated classes only):** ###/### (##%)
- **Generated Mutations:** ###
- **Killed Mutations:** ### (##%)
- **Mutations with No Coverage:** ##
- **Test Strength:** ##%
- **Total Tests Run:** ### (#.## tests per mutation)

