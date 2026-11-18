package Dekal;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.List;

public class TUIJeu {
    private Screen screen;

    public TUIJeu() {
        try {
            // Initialisation du terminal Lanterna
            this.screen = new DefaultTerminalFactory().createScreen();
            this.screen.startScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la grille sur le terminal Lanterna.
     * @param g La grille à afficher.
     * @param curL Ligne du curseur (-1 si pas de curseur).
     * @param curC Colonne du curseur (-1 si pas de curseur).
     */
    // Affiche la grille de lanterna
    public void afficherGrille(Grille g, int curL, int curC) {
        try {
            screen.clear(); // Nettoie l'écran avant de redessiner
            TextGraphics tg = screen.newTextGraphics();

            // Affichage des index de colonnes
            tg.putString(8, 1, "1   2   3   4");

            char ligneChar = 'A';
            int y = 3;

            for (int i = 0; i < 4; i++) {
                // Affichage des lettres de lignes
                tg.putString(5, y, String.valueOf(ligneChar));

                int x = 8;
                for (int j = 0; j < 4; j++) {
                    boolean estCurseur = (i == curL && j == curC);
                    Case currentCase = g.getCase(i, j);
                    Carte c = currentCase.getCarte();

                    if (estCurseur) {
                        tg.setForegroundColor(TextColor.ANSI.GREEN);
                    }

                    if (c == null) {
                        tg.putString(x, y, " . ");
                    } else if (!c.estVisible()) {
                        tg.putString(x, y, "???");
                    } else {
                        int val = c.getValeur();
                        // Vérifie si la carte est neutralisée (doublon voisin)
                        boolean neutre = g.aVoisinIdentique(i, j);

                        if (neutre) {
                            tg.setForegroundColor(TextColor.ANSI.RED);
                            tg.putString(x, y, "(" + val + ")");
                        } else {
                            tg.setForegroundColor(TextColor.ANSI.YELLOW);
                            tg.putString(x, y, " " + val + " ");
                        }
                    }

                    tg.setForegroundColor(TextColor.ANSI.WHITE); // Reset couleur
                    x += 4;
                }
                ligneChar++;
                y += 2;
            }
            screen.refresh(); // Envoie les changements au terminal
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage de la grille.");
        }
    }
    
    public Screen getScreen() {
        return this.screen; 
    }
    //Cette méthode gère la navigation interactive dans la grille en utilisant un curseur visuel piloté par les touches directionnelles.
    //Elle assure le rafraîchissement en temps réel de l'affichage et retourne les coordonnées de la case sélectionnée dès que l'utilisateur valide avec la touche Entrée.
    public int[] choisirCaseCurseur(Grille g, String instruction) {
        int curL = 0;
        int curC = 0;
        try {
            while (true) {
                screen.clear();
                afficherGrille(g, curL, curC); // Affiche la grille avec le curseur vert

                TextGraphics tg = screen.newTextGraphics();
                tg.setForegroundColor(TextColor.ANSI.CYAN);
                tg.putString(2, 18, "ACTION : " + instruction);
                tg.putString(2, 20, "Utilisez les FLECHES pour naviguer et ENTER pour valider.");
                screen.refresh();

                KeyStroke ks = screen.readInput();
                if (ks.getKeyType() == KeyType.ArrowUp && curL > 0) curL--;
                if (ks.getKeyType() == KeyType.ArrowDown && curL < 3) curL++;
                if (ks.getKeyType() == KeyType.ArrowLeft && curC > 0) curC--;
                if (ks.getKeyType() == KeyType.ArrowRight && curC < 3) curC++;
                
                if (ks.getKeyType() == KeyType.Enter) return new int[]{curL, curC};
            }
        } catch (IOException e) {
            return new int[]{0, 0};
        }
    }
    /**
     * Demande une saisie de texte à l'utilisateur tout en maintenant la grille visible.
     */
    public String demanderTexte(String etape, String instruction, Grille g, List<Carte> centre) {
        try {
            StringBuilder sb = new StringBuilder();
            while (true) {
                screen.clear();
                
                // Affichage de la Grille
                afficherGrille(g, -1, -1); 

                TextGraphics tg = screen.newTextGraphics();
                
                //  Affichage du Centre (Munitions disponibles)
                dessinerCentre(centre, tg);

                // Barre de séparation / En-tête d'étape
                tg.setForegroundColor(TextColor.ANSI.BLACK);
                tg.setBackgroundColor(TextColor.ANSI.CYAN);
                // On crée une ligne pleine pour séparer visuellement
                tg.putString(0, 17, " STEP: " + etape.toUpperCase() + " ".repeat(50));
                
                // Instruction et saisie
                tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
                tg.setForegroundColor(TextColor.ANSI.WHITE);
                tg.putString(2, 19, "👉 " + instruction);
                
                tg.setForegroundColor(TextColor.ANSI.YELLOW);
                tg.putString(2, 21, "> " + sb.toString() + "_");
                
                screen.refresh();

                KeyStroke ks = screen.readInput();
                if (ks.getKeyType() == KeyType.Enter) return sb.toString().trim();
                
                if (ks.getKeyType() == KeyType.Backspace && sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                } else if (ks.getKeyType() == KeyType.Character) {
                    sb.append(ks.getCharacter());
                }
            }
        } catch (IOException e) {
            return "";
        }
    }
    // Methode permettant de dessiner le centre
    public void dessinerCentre(List<Carte> centre, TextGraphics tg) {
        tg.setForegroundColor(TextColor.ANSI.GREEN);
        tg.putString(2, 14, "CARTES AU CENTRE :");
        
        int x = 2;
        for (int i = 0; i < centre.size(); i++) {
            Carte c = centre.get(i);
            String txt = i + ":[" + c.getValeur() + "]";
            tg.setForegroundColor(TextColor.ANSI.WHITE);
            tg.putString(x, 15, txt);
            x += txt.length() + 2;
         }
     }
    /**
     * Permet de choisir une direction à l'aide des flèches du clavier.
     */
    public Direction choisirDirectionInsertion(Grille g, List<Carte> centre) {
        try {
            while (true) {
                screen.clear();
                afficherGrille(g, -1, -1);
                TextGraphics tg = screen.newTextGraphics();
                dessinerCentre(centre, tg);

                tg.setForegroundColor(TextColor.ANSI.BLACK);
                tg.setBackgroundColor(TextColor.ANSI.CYAN);
                tg.putString(0, 17, " Étape 2/3 : DIRECTION " + " ".repeat(50));
                
                tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
                tg.setForegroundColor(TextColor.ANSI.WHITE);
                tg.putString(2, 19, "👉 Choisissez le SENS d'insertion avec les FLÈCHES :");
                tg.putString(2, 21, "(HAUT, BAS, GAUCHE ou DROITE) puis appuyez sur ENTER");

                screen.refresh();

                KeyStroke ks = screen.readInput();
                if (ks.getKeyType() == KeyType.ArrowUp) return Direction.Haut;
                if (ks.getKeyType() == KeyType.ArrowDown) return Direction.Bas;
                if (ks.getKeyType() == KeyType.ArrowLeft) return Direction.Gauche;
                if (ks.getKeyType() == KeyType.ArrowRight) return Direction.Droite;
                
                // On ne sort de la boucle que si une direction est choisie (ou Enter pour valider une direction par défaut)
                if (ks.getKeyType() == KeyType.Enter) return Direction.Droite; // Par défaut
            }
        } catch (IOException e) {
            return Direction.Droite;
        }
    }
     // Affiche un message temporaire en bas de l'écran.
    public void afficherMessage(String msg) {
        try {
        TextGraphics tg = screen.newTextGraphics();
        // Efface la ligne du message pour éviter les superpositions
        tg.putString(2, 22, "                                                               "); 
        tg.setForegroundColor(TextColor.ANSI.MAGENTA);
        tg.putString(2, 22, ">>> " + msg);
            screen.refresh();
            Thread.sleep(1000); // Laisse le temps de lire
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     //Ferme proprement le terminal Lanterna pour revenir à la console normale.
    public void quitter() {
        try {
            if (screen != null) {
                screen.clear();
                screen.stopScreen(); 
                screen.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur stopScreen: " + e.getMessage());
        }
    }
}