package Dekal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Grille {
    // Création de la grille
    private final Case[][] casesTableau = new Case[4][4];

    public Grille() {
        // Création des 16 cases
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                casesTableau[i][j] = new Case();
            }
        }
        // Tissage des voisins
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (j > 0)
                    casesTableau[i][j].setVoisin(Direction.Gauche, casesTableau[i][j - 1]);
                if (j < 3)
                    casesTableau[i][j].setVoisin(Direction.Droite, casesTableau[i][j + 1]);
                if (i > 0)
                    casesTableau[i][j].setVoisin(Direction.Haut, casesTableau[i - 1][j]);
                if (i < 3)
                    casesTableau[i][j].setVoisin(Direction.Bas, casesTableau[i + 1][j]);
            }
        }
    }
    public Case getCase(int l, int c) {
        return casesTableau[l][c];
    }
    // la fonction trouverCaseVide permet de trouver une case vide
    private Case trouverCaseVide() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (casesTableau[i][j].getCarte() == null) {
                    return casesTableau[i][j];
                }
            }
        }
        return null;
    }
    // la fonction getCoordonnees permet de trouver les coordonnées d'une case
    private int[] getCoordonnees(Case c) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (casesTableau[i][j] == c)
                    return new int[]{i, j};
            }
        }
        return null;
    }
    // la fonction insererCarte permet d'inserer une carte dans la grille
    public Carte insererCarte(int index, Direction direction, Carte carte, TUIJeu tui, Grille g) {
        if (index < 0 || index > 3) {
            System.out.println("Index invalide : il doit être entre 0 et 3.");
            return null;
        }
        // 1. On repère la case vide AVANT le mouvement
        Case caseVideInitiale = trouverCaseVide();
        int[] posVide = getCoordonnees(caseVideInitiale);

        // 2. On détermine si le mouvement va combler le vide directement ou éjecter
        boolean videSurLeChemin = switch (direction) {
            case Gauche, Droite -> (posVide[0] == index);
            case Haut, Bas -> (posVide[1] == index);
        };

        // 3. On définit la case d'entrée
        Case caseDepart = switch (direction) {
            case Gauche -> casesTableau[index][0];
            case Droite -> casesTableau[index][3];
            case Haut -> casesTableau[0][index];
            case Bas -> casesTableau[3][index];
        };

        // 4. On effectue le décalage (shift)
        // On récupère la carte qui sort (sera null si le vide était sur le chemin)
        Carte carteEjectee = caseDepart.shift(direction, carte, tui, g);

        if (carteEjectee != null && !videSurLeChemin && caseVideInitiale != null) {
            // La carte éjectée va remplir le trou distant
            caseVideInitiale.setCarte(carteEjectee);
            return null; // On renvoie null car la carte a fini par être placée
        }

        return carteEjectee;
    }
// fonction distribuerCartes permet de distribuer les cartes au hasard
    public void distribuerCartes() {
        List<Carte> paquet = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            for (int j = 0; j < 10; j++)
                paquet.add(new Carte(i));
        }
        Collections.shuffle(paquet);

        int index = 0;
        for (int l = 0; l < 4; l++) {
            for (int c = 0; c < 4; c++) {
                casesTableau[l][c].setCarte(paquet.get(index++));
            }
        }
    }
// la fonction calculerScoreFinal permet de calculer le score de la grille
    public int calculerScoreFinal() {
        int score = 0;
        for (int l = 0; l < 4; l++) {
            for (int c = 0; c < 4; c++) {
                Carte carte = casesTableau[l][c].getCarte();
                if (carte != null) {
                    carte.estVisible(); // On force la visibilité
                }
            }
        }
        //  Maintenant on calcule le score
        for (int l = 0; l < 4; l++) {
            for (int c = 0; c < 4; c++) {
                score += casesTableau[l][c].RetournerValeurScore();
            }
        }
        return score;
    }
// la fonction insertionPossible permet de savoir si une insertion est possible
    public boolean insertionPossible(int index, Direction dir) {
    // On parcourt la ligne ou la colonne de 0 à 3
        for (int k = 0; k < 4; k++) {
            Case c = null;
            
            if (dir == Direction.Haut || dir == Direction.Bas) {
                // Pour Haut/Bas, on fixe la COLONNE (index) et on fait varier la LIGNE (k)
                c = casesTableau[k][index]; 
            } else {
                // Pour Gauche/Droite, on fixe la LIGNE (index) et on fait varier la COLONNE (k)
                c = casesTableau[index][k];
            }
            // Si on trouve une case sans carte, l'insertion est possible !
            if (c != null && c.getCarte() == null) {
                return true;
            }
        }
        // Si on a parcouru toute la ligne/colonne sans trouver de null
        return false;
    }
    
    // la fonction aVoisinIdentique permet de savoir si une carte a un voisin identique
    public boolean aVoisinIdentique(int i, int j) {

        Carte c = casesTableau[i][j].getCarte();
        if (c == null || !c.estVisible()) return false;
        int valeur = c.getValeur();
        // Haut
        if (i > 0) {
            Carte v = casesTableau[i-1][j].getCarte();
            if (v != null && v.estVisible() && v.getValeur() == valeur)
                return true;
        }
        // Bas
        if (i < 3) {
            Carte v = casesTableau[i+1][j].getCarte();
            if (v != null && v.estVisible() && v.getValeur() == valeur)
                return true;
        }
        // Gauche
        if (j > 0) {
            Carte v = casesTableau[i][j-1].getCarte();
            if (v != null && v.estVisible() && v.getValeur() == valeur)
                return true;
        }
        // Droite
        if (j < 3) {
            Carte v = casesTableau[i][j+1].getCarte();
            if (v != null && v.estVisible() && v.getValeur() == valeur)
                return true;
        }
        return false;
    }
}
