# Big Data
## IMPORTANT IL FAUT OUVRIR QUE LE DOSSIER 06-BigData et non tout le dossier rattrappages avec vscode pour que le venv kernel de vscode fonctionne
### Prérequis

- Python v3.7
- VSCode avec extension Jupyter

### Installation

1. Créer un environnement virtuel:

```bash
python -m venv venv
#Windows
venv\Scripts\activate
#Mac et Linux
source venv/bin/activate
```

3. Installer les librairies

```bash
pip install -r requirements.txt
```

### Lancer les analyses

0. Ouvrir VScode
1. Supprimer le dossier `outputs` si vous voulez tous regénérer
2. Ouvrir `netflix_analysis.ipynb`
3. Séléctionner l'environnement Virtuel en tant que le kernel
4. Lancer toutes les cellules (Shift+Entrer ou "Run All" ou appuyer sur les boutons ► un par un)

### Fichiers générés

Les analyses vont créer un dossier `output/` qui va contenir:

- `netflix_cleaned.csv` - Données nettoyées
- `*.png` - Visualisations de données
- `report.md` - Résumé d'analyses