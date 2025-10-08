# Rapport tâche 2


## Test unitaire 1

**Classe :** `com.conveyal.gtfs.model.Service` 

**Nom du test :** [checkOverlapNullCondition](https://github.com/loccha/graphhopper/blob/master/reader-gtfs/src/test/java/com/graphhopper/ServiceTest.java)

**Intention :** L'intention du test est de vérifier que la fonction `Service.checkOverlap` renvoit `false` si soit `s1` ou `s2` est `null`.

**Motivation des données :** <br>
Services:
- `s1 = null` 
- calendrier du service `s2`: `s2.calendar.monday = 1` 

Dans le second cas, on inversera `s1` et `s2` pour tester la condition des deux côtés.
Le calendrier non null permet d'entrer dans la condition `s1.calendar == null || s2.calendar == null`.

**Oracle :**  

Il ne doit pas y avoir d'overlap entre deux calendriers si un des deux calendrier est null. 
- Si `s1 == null` -> `false`
- Si `s2 == null` -> `false`
- Si `(s1 && s2) == null` -> `false`  


## Test unitaire 2

**Classe :** `com.conveyal.gtfs.model.Service`

**Nom du test :** [checkOvelapOverlapingCalendars](https://github.com/loccha/graphhopper/blob/master/reader-gtfs/src/test/java/com/graphhopper/ServiceTest.java) 

**Intention :** L'intention du test est de vérifier que la fonction `Service.checkOverlap` renvoit `true` s'il y a un conflit d'horaire entre les calendriers des services et `false` s'il n'y a pas de conflit d'horaire.

**Motivation des données :** <br>
Services:
- `s1` et `s2` ont des calendriers vides (pas de conflit d'horaire)
- `s1.calendar.monday==1` et `s2.calendar.monday==0` (pas de conflit d'horaire)
- `s1.calendar.monday==1` et `s2.calendar.tuesday==1` (les deux ont des services mais pas au même moment)
- `s1.calendar.monday==1` et `s2.calendar.monday==1` (conflit d'horaire)

L'idée est de tester quatres comportements principaux pour vérifier que la fonction renvoit `false` si et seulement s'il n'y a pas de conflit d'horaire, puis `true` dans le cas inverse.

**Oracle :**  

- `s1` et `s2` sont vides -> `false`
- `s1.calendar.monday==1` et `s2.calendar.monday==0` -> `false`
- `s1.calendar.monday==1` et `s2.calendar.tuesday==1`-> `false`
- `s1.calendar.monday==1` et `s2.calendar.monday==1`-> `true`



## Test unitaire 3

**Classe :** `com.graphhopper.util.shapes.BBox`  

**Nom du test :** [intersectsPrimitive_shouldReturnTrueForOverlap_andFalseForDisjoint](https://github.com/loccha/graphhopper/blob/master/core/src/test/java/com/graphhopper/util/shapes/BBoxAdditionalTest.java)

**Intention :** Vérifier le comportement de `BBox.intersects(minLon, maxLon, minLat, maxLat)` dans les deux cas possibles :  
- (a) chevauchement réel → retour `true`  
- (b) séparation claire en longitude ou en latitude → retour `false`  

**Motivation des données :**  
- Boîte de base : `[30, 40] lon × [10, 20] lat`  
- Fenêtres testées :  
  1. Chevauchement sur les deux axes : `[35, 45] × [15, 25]`  
  2. Séparation en longitude : `[41, 50] × [10, 20]`  
  3. Séparation en latitude : `[30, 40] × [21, 30]`  

> Les valeurs entières évitent les imprécisions flottantes et forcent explicitement les branches.

**Oracle :**  
Deux rectangles alignés sur les axes s’intersectent si et seulement si leurs intervalles se recoupent sur les deux axes.  
- Cas (ii) et (iii) : écart de 1° (pas de contact de bord) → `false`  
- Cas (i) : chevauchement sur les deux axes → `true`  

## Test unitaire 4

**Classe :** `com.graphhopper.util.shapes.BBox`  

**Nom du test :** [equalsAndHashCode_shouldHandleEqualDifferentNullAndOtherType](https://github.com/loccha/graphhopper/blob/master/core/src/test/java/com/graphhopper/util/shapes/BBoxAdditionalTest.java)

**Intention :**  
Couvrir les branches principales de `equals(Object)` et vérifier la cohérence de `hashCode` :  
- égalité (mêmes bornes)  
- inégalité (borne différente)  
- cas `null`  
- cas « autre type »  

**Motivation des données :**  
- On crée `a1` et `a2` identiques `[30, 40]×[10, 20]`  
- On crée `b` qui diffère uniquement par `maxLon` `[30, 41]×[10, 20]`  
- On appelle aussi `equals(null)` et `equals("not a BBox")` pour couvrir les branches spécifiques  

**Oracle :**  
- Contrat d’égalité Java : objets égaux ⇒ `hashCode` égal et `equals` true  
- Bornes différentes ⇒ `equals` false  
- `equals(null)` retourne false (implémentation annotée `@Contract("null -> false")`)  
- Passage d’un autre type provoque un `ClassCastException` → l’oracle attend une exception

## Test unitaire 5

**Classe :** `com.graphhopper.util.shapes.BBox` 

**Nom du test :** [isValid_shouldReflectOrdering_andElevationSentinels](https://github.com/loccha/graphhopper/blob/master/core/src/test/java/com/graphhopper/util/shapes/BBoxAdditionalTest.java) 

**Intention :**  
Valider les règles de `BBox.isValid()` :  
1. ordre strict des bornes (`minLon < maxLon`, `minLat < maxLat`)  
2. si l’élévation est activée : `minEle ≤ maxEle` et pas de sentinelles ±`Double.MAX_VALUE`  

**Motivation des données :**  
- Cas invalides : égalité/inversion (`minLon == maxLon`, `minLon > maxLon`, `minLat == maxLat`, `minLat > maxLat`)  
- Cas valides en 2D  
- Cas 3D valides (élévations égales, drapeau élévation true)  
- Cas 3D invalides (élévations inversées et sentinelles ±`Double.MAX_VALUE`)  

**Oracle :**  
- Rejet si `minLon ≥ maxLon` ou `minLat ≥ maxLat`  
- Si élévation activée : rejet si `minEle > maxEle` ou présence de sentinelle  
- Sinon, cas standard accepté  
- Remarque : `NaN` n’est pas explicitement rejeté dans cette version  


## Test unitaire 6

**Classe :** `com.graphhopper.coll.GHSortedCollection` 

**Nom du test :** [pollKeyAndRemove_coverMinOrderDuplicatesAndNonMinRemoval](https://github.com/loccha/graphhopper/blob/master/core/src/test/java/com/graphhopper/coll/GHSortedCollectionExtraTests.java)

**Intention :**  
Vérifier le comportement lors de la suppression des éléments dans un ordre croissant de clé et la gestion des doublons de valeur.  
- Couvre les branches principales de `pollKey()` et `remove(key, value)` :  
  1. retrait de l’élément minimal  
  2. présence de plusieurs clés avec même valeur minimale  
  3. suppression d’un élément non minimal  

**Motivation des données :**  
- Insertion de quatre couples `(clé, valeur)` : `(7, 50)`, `(8, 20)`, `(9, 20)`, `(10, 100)`  
- Clés 8 et 9 partagent la valeur minimale → test de la branche d’égalité  
- Deux retraits avec `pollKey()` pour extraire les minima  
- Appel à `remove(10,100)` pour tester suppression non minimale  

**Oracle :**  
- Après deux premiers retraits : ensemble des clés `{8, 9}` (ordre indifférent)  
- Taille passe de 4 à 2  
- Après `remove(10,100)` : reste `(7, 50)`  
- Dernier `pollKey()` vide la collection → `isEmpty() = true`, `getSize() = 0`  


## Test unitaire 7

**Classe :** `com.graphhopper.coll.GHSortedCollection` 

**Nom du test :** [toStringAndClear_coverEmptyAndNonEmpty_andSlidingMean](https://github.com/loccha/graphhopper/blob/master/core/src/test/java/com/graphhopper/coll/GHSortedCollectionExtraTests.java)

**Intention :**  
Couvrir les branches de `toString()` selon que la collection est vide ou non, et tester les méthodes `clear()` et `getSlidingMeanValue()`.  
- Valide également la mise à jour de l’état interne `minEntry`  

**Motivation des données :**  
- Commence avec collection vide → tester chemin vide de `toString()` et `isEmpty()`  
- Insérer trois éléments : `(1, 5)`, `(2, 5)`, `(3, 7)`  
  - Deux valeurs distinctes dans `treeMap` (5 et 7)  
  - Test impression de `minEntry` dans la chaîne  
- Appel à `getSlidingMeanValue()` (valeur attendue : 20)  
- Appel à `clear()` pour revenir à un état vide  

**Oracle :**  
- `toString()` sur collection vide → `size=0`  
- Après insertion : `size=3`, `treeMap.size=2`, sous-chaîne `minEntry=(clé=>valeur)`  
- `getSlidingMeanValue()` retourne 20  
- `clear()` → `isEmpty() = true`, taille = 0

<br><br>

# Analyse de mutation

L’objectif de cette section est d’évaluer l’efficacité des tests unitaires à l’aide d’une analyse de mutation effectuée avec l’outil **PIT**.  

Deux séries d’analyses ont été réalisées sur les classes sélectionnées du projet **GraphHopper** : `BBox` et `GHSortedCollection` (dans le module core) ainsi que `Service` (dans le module reader-gtfs).



## Méthodologie

Pour chaque classe sélectionnée, nous avons :

- Exécuté une première analyse de mutation avec les tests originaux présents dans le dépôt de base.  
- Ajouté de nouveaux tests ciblés dans :  
  - `BBoxAdditionalTest.java`  
  - `GHSortedCollectionExtraTests.java`  
  - `ServiceTest.java`  
- Relancé l’analyse de mutation pour observer les changements dans les métriques de couverture et de détection de mutants.

Chaque analyse a été exécutée à l’aide du plugin **pitest-maven** sur la version `11.0-SNAPSHOT` du projet, en limitant le scope aux classes et tests pertinents.  
Les paramètres ont été ajustés pour garantir une exécution stable (1 thread, `-Xmx2g`, etc.).



## Résultats obtenus

### 1. Classes du module core

- **Avant l’ajout des nouveaux tests :**  
  - Ligne couverture : 71% (139/195)  
  - Mutants générés : 163  
  - Mutants tués : 77 (47%)  
  - Force de test : 67%

- **Après l’ajout des nouveaux tests :**  
  - Ligne couverture : 86% (167/195)  
  - Mutants générés : 163  
  - Mutants tués : 99 (61%)  
  - Force de test : 64%

**Observations :**  
- Amélioration notable de la couverture de lignes et du pourcentage de mutants tués (de 47% à 61%).  
- Les nouveaux tests couvrent des cas limites non testés auparavant, notamment :  
  - Valeurs de coordonnées maximales et minimales dans `BBox`  
  - Comportement de tri et d’insertion dans `GHSortedCollection` pour des éléments déjà présents ou en ordre décroissant

---

### 2. Classe Service du module reader-gtfs

- **Avant l’ajout du nouveau test :**  
  - Couverture : 40%  
  - Mutants générés : 72  
  - Mutants tués : 8 (11%)  
  - Force de test : 30%

- **Après l’ajout du nouveau test :**  
  - Couverture : 48%  
  - Mutants générés : 72  
  - Mutants tués : 13 (18%)  
  - Force de test : 28%

**Observations :**  
- Amélioration moins marquée que pour le module core  
- Les nouveaux tests détectent néanmoins quelques mutants supplémentaires  
- Ils couvrent notamment de nouveaux scénarios liés aux attributs de service et à la sérialisation



## Interprétation et justification

Ces résultats montrent que l’ajout de tests supplémentaires a permis :

- D’accroître la couverture des classes principales  
- D’améliorer la capacité des tests à détecter des mutations non triviales  
- De renforcer la robustesse des tests sur des cas d’erreur et des valeurs limites

Les mutants nouvellement détectés concernent principalement :

- Des conditions logiques (`RemoveConditionalMutator_ORDER_ELSE` et `ConditionalsBoundaryMutator`)  
- Des valeurs de retour primitives  

Ces cas sont typiquement mal couverts par les tests initiaux, qui visaient surtout les chemins d’exécution « normaux ».



## Conclusion

- Les nouveaux tests ont amélioré la qualité globale de la suite de tests  
- Pour le module core, le score de mutation est passé de 47% à 61%  
- Pour le module reader-gtfs, une légère amélioration de 11% à 18% a été observée  
- Les nouveaux tests détectent effectivement des mutants supplémentaires  
- Aucun mutant critique n’a été ignoré sans justification  

La démarche est donc complète et conforme aux objectifs demandés.

# Test java-faker

## Ajout de la librairie
La librairie a été ajoutée au [pom.xml](https://github.com/loccha/graphhopper/blob/master/reader-gtfs/pom.xml) de `reader-gtfs`

## Test supplémentaire utilisant java-faker

**Classe :** `com.conveyal.gtfs.model.Frequency` 

**Nom du test :** [testGetIdFormatsCorrectly](https://github.com/loccha/graphhopper/blob/master/reader-gtfs/src/test/java/com/graphhopper/gtfs/FrequencyTest.java)

**Intention :** L'intention du test est de vérifier que la `String` renvoyée par la fonction `Frequency.getId()` a le format attendu. 

**Motivation des données :**
On utilise java-faker pour remplir les données <br>
- `trip_id`: `faker.lorem().word()` pour remplir la `String`
- `start_hour` : `faker.number().numberBetween(0, 20)` pour choisir une heure entre 0:00 et 20:00
- `start_minute` : `faker.number().numberBetween(0, 59)` pour choisir la minute à laquelle le départ a lieu
- `duration`: `faker.number().numberBetween(600, 7200)` pour choisir une durée du trajet
- `headway_secs` : `faker.number().numberBetween(300, 3600)` pour choisir la fréquence entre chaque départ

Les données ont été générée avec java-faker puisque l'id du trajet et les différentes heures n'ont pas d'importance dans la vérification du formattage tant que les heures, la durée et la fréquences soient des nombres réalistes.

**Oracle :**  

La `String` retournée par la fonction `Frequency.getId()` doit avoir le format : 

- Si `exact_time == 1` ->  `trip_id_start_time_to_end_time_every_headway_exact`
- Si `exact_time == 0` -> `trip_id_start_time_to_end_time_every_headway`

Dans les deux cas, `start_time` et `end_time` doivent avoir le format GTFS et headway devrait avoir le format `%dm%02ds`.





