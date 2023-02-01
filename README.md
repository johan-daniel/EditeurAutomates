# AutomatesLab

Version 1.0 (01/02/2022)

Ce git contient le code source et la documentation de l'application AutomatesLab.
Il s'agit du sujet numéro 8 des projets S7 proposés.

## Sujet

Créer une application permettant de définir des automates finis et de les sauvegarder dans un format xml adapté. 
Le fichier xml doit bien évidemment permettre de recharger l’automate dans l’éditeur. 
Au moins deux méthodes d’édition (donc deux vues) devront être disponibles : l’une graphique et l’autre sous le format xml. 
D’autres vues (matricielle, algébrique, ...) peuvent être programmées si le temps le permet. 
Le langage utilisé pour la réalisation de ce projet sera JAVA. 
Le programme sera conçu selon une architecture modèle – vue - contrôleur (MVC).

## Exécution

L'application nécéssite **Java 19.0.1** ou une version plus récente.

**TODO** 
Pour l'exécuter, télécharger les fichiers compilés de Release en fonction de votre plateforme.

## Compilation

**TODO** tester depuis un pc neuf
**TODO** pour compiler et débugger les fichiers sources: intelliJ

## Bugs connus

- Si le numéro d'un état dépasse 99999999 (édité manuellement dans la vue XML), essayer de charger l'affichage graphique fait lagger l'application et le chargement échoue (le fichier courant est fermé)
- Charger un fichier XML **invalide** depuis la vue graphique ouvre la vue XML vide plutôt qu'avec l'automate. Il faut l'ouvrir depuis la vue XML pour que cela fonctionne.
