# Rapport Tâche 3


## Justification des choix de test

Pour la tâche 3, nous avons choisi de tester les classes 
[RoutingAlgorithmFactorySimple](https://github.com/loccha/graphhopper/blob/master/core/src/main/java/com/graphhopper/routing/RoutingAlgorithmFactorySimple.java) et [RouteResource](https://github.com/loccha/graphhopper/blob/master/web-bundle/src/main/java/com/graphhopper/resources/RouteResource.java)
- **RoutingAlgorithmFactorySimple** : 
Cette classe joue un rôle central dans l’architecture de GraphHopper, car elle
est responsable de la création des différentes instances d’algorithmes de routage
en fonction des options fournies par l’utilisateur. Tester cette classe permet
donc de valider une partie critique du système : la correspondance entre les
paramètres d’entrée et les implémentations concrètes d’algorithmes.

- **RouteResource** :
Nous avons choisi cette classe puisqu'elle a comme attribut plusieurs dépendances externes importantes telles que les classes [GraphHopperConfig](https://github.com/loccha/graphhopper/blob/master/core/src/main/java/com/graphhopper/GraphHopperConfig.java), [GraphHopper](https://github.com/loccha/graphhopper/blob/master/core/src/main/java/com/graphhopper/GraphHopper.java) et [ProfileResolver](https://github.com/loccha/graphhopper/blob/master/web-bundle/src/main/java/com/graphhopper/http/ProfileResolver.java). Il est donc nécessaire d'utiliser des Mocks pour tester adéquatement afin de les générer automatiquement et d'isosler le comportement à tester.

## [Tests unitaires de la classe RoutingAlgorithmFactorySimple](https://github.com/loccha/graphhopper/blob/master/core/src/test/java/com/graphhopper/routing/RoutingAlgorithmFactorySimpleTest.java)

### Choix des classes simulées (mocks)

Afin d’isoler le comportement de la fabrique, plusieurs dépendances ont été simulées à l’aide de la bibliothèque `Mockito`.

- **Graph** : représente la structure de graphe sous-jacente. Elle est simulée pour éviter d’avoir à charger des données cartographiques réelles.

- **Weighting** : définit la logique de pondération des arêtes (par exemple distance ou temps). Son comportement est sans importance pour le test, donc il est simplement moqué.

- **AlgorithmOptions** : encapsule les paramètres de l’algorithme. Cette classe est au cœur de la logique testée, car ses valeurs déterminent quel algorithme sera instancié.

- **NodeAccess** : dépendance interne du graphe, utilisée par l’algorithme créé ; elle est également moquée.

L’utilisation de ces mocks garantit que le test se concentre uniquement sur la logique de sélection et d’instanciation de l’algorithme, sans dépendre d’autres composants complexes du système.

---

### Description des cas de test

Deux nouveaux cas de test ont été implémentés :

1. **createsAStarBidirection whenAlgoIsAStarBi()** :  
   Ce test vérifie que la fabrique crée correctement une instance de `AStarBidirection` lorsque l’option ASTAR BI est spécifiée. Plusieurs méthodes de `AlgorithmOptions` et de `Graph` sont simulées pour reproduire un environnement d’exécution typique. Des vérifications avec `verify()` assurent que toutes les dépendances ont été utilisées comme prévu.

2. **throwsForUnknownAlgorithm()** :  
   Ce test valide le comportement d’erreur de la fabrique lorsqu’un nom d’algorithme inconnu est fourni. On s’attend à une exception `IllegalArgumentException`, garantissant ainsi la robustesse et la sécurité du code.


## [Tests unitaire de la classe RouteResource](https://github.com/loccha/graphhopper/blob/master/web-bundle/src/test/java/com/graphhopper/RouteResourceTest.java)

### Choix des classes simulées (mocks)
Dans cette classe, nous avons choisi de *mocker* les classes suivantes: 


 - **GraphHopperConfig**: Afin de renvoyer une *string* spécifique lors de l'appel à sa méthode `getString`, appelée lors de l'initialisation de l'attribut `snapPreventionDefault`.

 - **GraphHopper**: Pour paramétrer le retour de sa fonction `getProperties` lors de l'initialisation de l'attribut `osmDate`. 

 - **profileResolver et ghRequest Transformer**: Afin de pouvoir les utiliser dans le constructeur (sans avoir a réellement créer les deux objets). Puisqu'il n'y a pas d'interaction avec eux lors de la construction d'un objet RouteResource, ils ne seront pas paramétrés.

 - **storableProperties** : Pour pouvoir paramétrer le retour de la fonction `getAll`lorsqu'elle est appelée lors de l'initialisation de l'attribut osmDate

 - **PMap** : Pour pouvoir confirmer que les appel nécessaire à la fonction `remove` ont bien eu lieu


### Description des cas de test

Dans cette classe, trois cas de tests sont implémentés:

1. **constructorInitializesFieldsCorrectly** :  
Vérifie que l'objet `RouteResource` a été créé avec des valeurs conformes aux attentes dans le cas ou il n'y a pas d'erreur dans les valeurs passées au constructeur. Le test passe si les attributs `osmDate` et `snapPreventionsDefault` sont adéquatement formattés.

2. **constructorHandlesDirtyEntriesWhenFillingSnapPreventionsDefaultField**: 
Vérifie que l'objet `RouteResource`a été créé avec des valeurs confirmes aux attentes dans le cas ou il y aurait des valeurs manquantes entre les virgules lors de l'appel à `getString("routing.snap_preventions_default", "")` de l'attribut `ghConfig`. Le test renvoit vrai si la valeur de l'attribut `storableProperties` est *pasré* avec le format attendu.


3. **verifyKeysAreSuccessfullyRemovedWithRemoveLegacyParameters**:
Le test passe si que lorsque `removeLegacyParameters` est appelé sur un objet RouteResource, il y a bien eu un appel de la fonction `remove` effectué sur les paramètres `weighting`, `vehicule`, `edge_based` et `turn_cost`.


## Justifications additionnelles
### Désactivation d’un test existant

Durant l’exécution de `mvn clean verify`, un test préexistant du module `web-bundle`, `GraphHopperConfigModuleTest`, échouait systématiquement avec une erreur de type **NoSuchMethodError**.  
Après analyse, nous avons identifié que ce test dépend d’une version plus récente de la bibliothèque `SnakeYAML` que celle résolue par la configuration Maven fournie dans le projet de départ.  

Plus précisément, le test appelle un constructeur de `ParserImpl` utilisant `LoaderOptions`, constructeur absent dans la version 2.4 de SnakeYAML référencée transitivement par `jackson-dataformat-yaml`.  

Cette incompatibilité n’est pas due à nos ajouts dans le module core, ni aux modifications apportées dans le cadre de la tâche 3. Il s’agit d’un problème structurel déjà présent dans le projet initial. Nous avons exploré différentes solutions, incluant la mise à jour des dépendances dans le module web-bundle, mais celles-ci créaient des incohérences supplémentaires dans la chaîne de construction multi-modules de GraphHopper.

Afin de respecter le cadre de la tâche, qui exige de concentrer nos efforts sur l’écriture de nouveaux tests dans le module core et sur l’intégration des tests de mutation dans l’outil d’intégration continue, nous avons décidé de désactiver uniquement ce test défaillant.  

Cette décision est justifiée par le fait que le test en question ne relève pas de notre travail, et que sa désactivation permet d’exécuter correctement la suite de tests, d’obtenir un bilan de mutation cohérent et de préserver l’intégrité de notre contribution sans modifier du code existant hors du périmètre demandé.

### Ajout de *Getters* dans la classe `RouteResource`

Les deux tests unitaires visant à vérifier que les objets `RouteResource` soient construits conformément aux attentes nécessitent de renvoyer les valeurs de deux champ `private` dans la classe. Afin de réaliser ces tests, nous avons ajouté deux nouveaux  *getters* : `getOsmDate` et `getSnapPreventionsDefault`.
