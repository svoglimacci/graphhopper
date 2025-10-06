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
Ce test vérifie que la méthode `isClosed()` de CHStorage retourne correctement l'état de fermeture du stockage en testant les transitions d'état (ouvert → fermé) et en s'assurant que la méthode retourne bien l'état des `DataAccess` internes.

**Données de test :**
- Un `CHStorage` créé avec des paramètres valides (5 nœuds, 3 shortcuts attendus)
- Ajout d'un shortcut pour s'assurer que le storage contient des données
- Test des états avant et après fermeture du storage

**Oracle:**
1. **État initial (ouvert)** : `isClosed()` doit retourner `false`
2. **Après ajout de données** : `isClosed()` doit continuer à retourner `false`
3. **Après fermeture** : `isClosed()` doit retourner `true`
4. **Cohérence** : Plusieurs appels consécutifs à `isClosed()` doivent donner le même résultat

**Justification :**
La méthode `isClosed()` dans CHStorage est critique car elle contient une assertion qui vérifie la cohérence entre les deux `DataAccess` internes (`nodesCH` et `shortcuts`) et retourne l'état de `nodesCH`. Un mutant qui modifierait cette logique pourrait :
- Cacher des états incohérents entre les composants internes
- Retourner un état incorrect, causant des erreurs dans le code client
- Ignorer l'état réel des ressources sous-jacentes

## Analyse de Mutation - Tests Ajoutés

- **Line Coverage (for mutated classes only):** ###/### (##%)
- **Generated Mutations:** ###
- **Killed Mutations:** ### (##%)
- **Mutations with No Coverage:** ##
- **Test Strength:** ##%
- **Total Tests Run:** ### (#.## tests per mutation)

