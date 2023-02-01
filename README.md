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

L'application nécessite **Java 19.0.1** ou une version plus récente.
Pour l'exécuter, télécharger les fichiers compilés de Release sur le git (ou dans le zip fourni) en fonction du système d'exploitation cible. 
Double cliquer sur le .jar ou le lancer depuis un terminal (`java -jar AutomatesLab.jar`)

## Compilation et débuggage

Pour compiler puis exécuter notre code source:
- Installer la JDK 19.0.1 ou supérieur
- Cloner le git ou extraire le zip des codes sources
- Ouvrir le dossier à l'aide d'IntelliJ IDEA 2022
- Cliquer sur la pop-up en bas à droite "Load Maven Project"
- Cliquer sur la configuration de débuggage (en haut à droite): "Edit configurations..."
- Ajouter une configuration de débuggage "Application". La paraméter comme exécutant la `JDK 19.0.1`, et dans "Main class" entrer `EditeurAutomates.Launcher`.
- Cliquer sur "Débugger" pour commencer l'exécution !

Pour compiler en .jar à l'aide de Maven, ouvrir l'onglet Maven dans l'onglet de droite, dossier "Lifecycle", double cliquer sur "package". Le .jar est généré à la racine du dosser target.

## Bugs connus

- Si le numéro d'un état dépasse 99999999 (édité manuellement dans la vue XML), essayer de charger l'affichage graphique fait lagger l'application et le chargement échoue (le fichier courant est fermé)
- Charger un fichier XML **invalide** depuis la vue graphique ouvre la vue XML vide plutôt qu'avec l'automate. Il faut l'ouvrir depuis la vue XML pour que cela fonctionne.
