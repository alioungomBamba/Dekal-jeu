package Dekal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Partie {

    // Liste des joueurs de la partie
    private final ArrayList<Joueur> joueurs;

    // Cartes placées au centre pendant les phases
    private final ArrayList<Carte> centre;

    // Scanner pour lire les entrées utilisateur
    private final Scanner sc;

    // ===== AJOUT TUI =====
    private boolean utiliserTUI = false;
    private TUIJeu tui;
    

    // Constructeur pour la console 
    public Partie(Scanner sc, boolean mode_Ordinateur) {
        this.sc = sc;
        joueurs = new ArrayList<>();
        centre = new ArrayList<>();

        if (!mode_Ordinateur) {
            // Mode normal
            System.out.print("Nombre de joueurs humains : ");
            int nbHumains = lireEntierValide(1, 10);

            for (int i = 0; i < nbHumains; i++) {
                System.out.print("Nom du joueur " + (i + 1) + " : ");
                joueurs.add(new Joueur(sc.next(), false));
            }
        } else {
            // Mode inteligence artificielle
            System.out.print("Nom du joueur humain : ");
            joueurs.add(new Joueur(sc.next(), false));

            System.out.println("Ajout d'un joueur IA...");
            joueurs.add(new Joueur("Ordinateur", true));
        }
    }
    // =====  Constructeur TUI =====
    public Partie(Scanner sc, boolean modeIA, boolean utiliserTUI) {
        this(sc, modeIA); // réutilise ton ancien constructeur
        this.utiliserTUI = utiliserTUI;

        if (utiliserTUI) {
            tui = new TUIJeu();
        }
    }
        // la methode lireEntierValide() permet de lire un entier valide
    private int lireEntierValide(int min, int max) {
        while (true) {
            String input = sc.next();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) return val;
                System.out.println("Valeur invalide. Elle doit être entre " + min + " et " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre entier.");
            }
        }
    }
    // la methode lireDirectionValide() permet de lire une direction valide
    private Direction lireDirectionValide() {
        while (true) {
            System.out.print("→ Direction (Gauche/Droite/Haut/Bas) : ");
            String input = sc.next().trim();

            try {
                return Direction.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Direction invalide. Choisissez parmi : Gauche, Droite, Haut, Bas.");
            }
        }
    }
    // la methode lireCoordonneeValide() permet de lire une coordonnée valide
    private int[] lireCoordonneeValide() {
        while (true) {
            String s = sc.next().toUpperCase();
            if (s.length() == 2 &&
                s.charAt(0) >= 'A' && s.charAt(0) <= 'D' &&
                s.charAt(1) >= '1' && s.charAt(1) <= '4') {
                int ligne = s.charAt(0) - 'A';
                int col = s.charAt(1) - '1';
                return new int[]{ligne, col};
            }
            System.out.println("Format invalide. Exemple correct : A1, C4.");
        }
    }
    // la methode choisirCarteCentre() permet de choisir une carte au centre
    private Carte choisirCarteCentre() {
        while (true) {
            int choix = lireEntierValide(0, centre.size() - 1);
            try {
                return centre.remove(choix);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Aucune carte à cet index.");
            }
        }
    }
    // la methode afficherCentre() permet d'afficher le centre
    private void afficherCentre() {
        if (centre.isEmpty()) {
            System.out.println("  (aucune carte)");
            return;
        }
        for (int i = 0; i < centre.size(); i++) {
            Carte c = centre.get(i);
            System.out.println("  " + i + " : [" + c.getValeur() + "]");
        }
    }

    /* ===================
       PHASE 1 — RETRAIT
       ====================*/
    /*  Cette méthode coordonne l'extraction d'une carte pour chaque joueur, en alternant entre la sélection interactive pour l'humain et la sélection aléatoire pour l'ordinateur.
    Elle assure la mise à jour du centre de la table en y ajoutant les cartes retirées, préparant ainsi le terrain pour la phase d'insertion suivante. */
    private void phaseRetrait() {

        centre.clear();
        
        // On n'affiche ces messages console que si on n'est PAS en mode TUI
        if (!utiliserTUI) {
            System.out.println("\n==============================");
            System.out.println("      PHASE 1 — RETRAIT");
            System.out.println("==============================\n");
        }

        for (Joueur j : joueurs) {
            if (!utiliserTUI) System.out.println("Tour de " + j.getNom() + " :");

            if (j.estIA()) {
                Carte carte = retraitIA(j);
                centre.add(carte);
                continue;
            }
            // ===== Mode TUI  =====
            if (utiliserTUI) {
                int[] pos = tui.choisirCaseCurseur(j.getGrille(), "CHOISISSEZ UNE CASE A RETIRER");
                
                int l = pos[0];
                int c = pos[1];

                Case caseChoisie = j.getGrille().getCase(l, c);
                Carte carte = caseChoisie.getCarte();

                if (carte != null) {
                    caseChoisie.setCarte(null);
                    centre.add(carte);
                    tui.afficherMessage("Succès : Carte [" + carte.getValeur() + "] retirée !");
                } else {
                    tui.afficherMessage("Erreur : La case est déjà vide !");
                }
            } else {
                // ===== MODE CONSOLE CLASSIQUE =====
                AffichageGrille.afficher(j.getGrille());
                Carte carte = null;

                while (carte == null) {
                    System.out.println("\nChoisissez une case à retirer (ex : A1, B3) : ");
                    int[] pos = lireCoordonneeValide();
                    int l = pos[0];
                    int c = pos[1];

                    carte = j.getGrille().getCase(l, c).getCarte();

                    if (carte == null) {
                        System.out.println("❌ Cette case est vide.");
                    } else {
                        j.getGrille().getCase(l, c).setCarte(null);
                        System.out.println("Vous avez retiré la carte [" + carte.getValeur() + "].");
                        centre.add(carte);
                    }
                }
            }
        }

        if (!utiliserTUI) {
            System.out.println("\nCartes au centre :");
            afficherCentre();
        }
    }
    // la methode retraitIA() permet à l'IA de retirer une carte
    private Carte retraitIA(Joueur j) {
        List<int[]> casesNonVides = new ArrayList<>();

        for (int l = 0; l < 4; l++) {
            for (int c = 0; c < 4; c++) {
                if (j.getGrille().getCase(l, c).getCarte() != null) {
                    casesNonVides.add(new int[]{l, c});
                }
            }
        }
        Random r = new Random();
        int[] pos = casesNonVides.get(r.nextInt(casesNonVides.size()));

        int l = pos[0];
        int c = pos[1];

        Carte carte = j.getGrille().getCase(l, c).getCarte();
        j.getGrille().getCase(l, c).setCarte(null);

        String msg = "🤖 " + j.getNom() + " retire la case " + (char)('A' + l) + (c + 1);

        if (utiliserTUI) {
            tui.afficherMessage(msg);
        } else {
            System.out.println(msg);
        }

        return carte;
    }


    /* ====================
       PHASE 2 — INSERTION
       ====================*/
/* Cette méthode gère la réintroduction des cartes du centre dans les grilles des joueurs en respectant les règles de décalage et de comblement des cases vides.
    Elle traite séquentiellement les choix de chaque participant, mettant à jour l'état visuel du plateau après chaque mouvement pour refléter l'évolution de la partie. */
    private void phaseInsertion() {

        System.out.println("\n==============================");
        System.out.println("    PHASE 2 — INSERTION");
        System.out.println("==============================\n");

        for (Joueur j : joueurs) {

            System.out.println("Tour de " + j.getNom() + " :");

            if (j.estIA()) {
                insertionIA(j);
                if (!utiliserTUI) {
                    // MODE CONSOLE
                    System.out.println("\nNouvelle grille de " + j.getNom() + " :");
                    AffichageGrille.afficher(j.getGrille());
                } else {
                    // MODE TUI → afficher la grille dans Lanterna
                    tui.afficherGrille(j.getGrille(), -1, -1);
                }

                continue;
            }

      // ===== MODE TUI =====
            if (utiliserTUI) {
                try {
                    //  Choisir une carte du centre
                    String rep = tui.demanderTexte(
                        "Étape 1/3 : SÉLECTION", 
                        "Quelle carte du centre voulez-vous utiliser ? (Index 0, 1...)", 
                        j.getGrille(), 
                        centre 
                    );
                    
                    int idx;
                    try {
                        idx = Integer.parseInt(rep);
                        if (idx < 0 || idx >= centre.size()) idx = 0; 
                    } catch (Exception e) {
                        idx = 0; 
                    }

                    Carte carteChoisie = centre.remove(idx);

                    // Choisir la direction (Flèches + ENTER)
                    Direction dir = tui.choisirDirectionInsertion(j.getGrille(), centre);

                    //  Choisir l'index (Sécurisé pour accepter A, B, C, D ou 1, 2, 3, 4)
                    String question = (dir == Direction.Haut || dir == Direction.Bas) 
                                    ? "Quelle COLONNE ? (1, 2, 3 ou 4)" 
                                    : "Quelle LIGNE ? (A, B, C ou D)";

                    String sIdx = tui.demanderTexte("Étape 3/3 : DESTINATION", question, j.getGrille(), centre);
                    
                    // --- PROTECTION CONTRE LE CRASH ---
                    int index;
                    try {
                        index = transformerEntreeEnIndex(sIdx); 
                    } catch (Exception e) {
                        index = 0; 
                    }
                    Carte exp = j.getGrille().insererCarte(index, dir, carteChoisie, tui, j.getGrille());

                    // Gestion du résultat
                    if (exp == carteChoisie) { 
                        tui.afficherMessage("❌ Insertion impossible : pas de case vide sur cette ligne/colonne !");
                        centre.add(carteChoisie); // On remet la carte au centre puisqu'elle n'a pas bougé
                    } else if (exp == null) {
                        tui.afficherMessage("✅ Réussi : Le vide a été comblé.");
                    } else {
                        tui.afficherMessage("✅ Réussi : Carte expulsée " + exp.getValeur());
                        centre.add(exp);
                    }
                } catch (Exception e) {
                    // En cas de bug, on ferme Lanterna proprement pour voir l'erreur dans la console
                    try { 
                        tui.getScreen().stopScreen(); 
                    } catch (Exception ex) {}
                }
                continue; 
            }
            // ===== MODE CONSOLE  =====
            System.out.println("\nCartes disponibles au centre :");
            afficherCentre();

            System.out.print("\nChoisissez une carte : ");
            Carte carteChoisie = choisirCarteCentre();

            centre.remove(carteChoisie);

            System.out.println("\nDirection d'insertion : Gauche / Droite / Haut / Bas");
            Direction direction = lireDirectionValide();

            System.out.print("Index (0 à 3) : ");
            int index = lireEntierValide(0, 3);
           
            // On vérifie AVANT si c'est possible
            boolean possible = j.getGrille().insertionPossible(index, direction);
            // On lance l'insertion
            Carte expulsee = j.getGrille().insererCarte(index, direction, carteChoisie, null, null);

            // LOGIQUE DE TEST
            if (possible) {
                // Si c'était possible, on considère que c'est réussi, 
                System.out.println("✅ Insertion réussie.");
            } else {
                // Si ce n'était pas possible dès le départ
                System.out.println("❌ Insertion interdite.");
                centre.add(carteChoisie);
            }

            centre.add(expulsee);

            System.out.println("\nNouvelle grille de " + j.getNom() + " :");
            AffichageGrille.afficher(j.getGrille());
            System.out.println("\nLégende :");
            System.out.println("  - Les carte isolée (compte dans le score) sont reprensentées par leur valeur, ex :  7");
            System.out.println("  - Carte neutralisée (ne compte pas) en parenthèse, ex : ( 7 )");
            System.out.println("  - Carte cachée : ???");
            System.out.println("  - Case vide : [vide]");

        }
    }
//la fonction insertionIA permet à l'IA de jouer son tour
    private void insertionIA(Joueur j) {
        Random r = new Random();
        Carte carte = centre.remove(0);

        // Lister tous les coups possibles
        List<Coup> coupsPossibles = new ArrayList<>();
        for (Direction d : Direction.values()) {
            for (int i = 0; i < 4; i++) {
                if (j.getGrille().insertionPossible(i, d)) {
                    coupsPossibles.add(new Coup(i, d));
                }
            }
        }
        //  Aucun coup possible
        if (coupsPossibles.isEmpty()) {
            String msg = "🤖 Aucun coup valide possible (ne devrait pas arriver).";
            if (utiliserTUI) {
                tui.afficherMessage(msg);
            } else {
                System.out.println(msg);
            }
            centre.add(0, carte);
            return;
        }
        //  Choisir un coup au hasard
        Coup choix = coupsPossibles.get(r.nextInt(coupsPossibles.size()));

        String msg = "🤖 " + j.getNom() + " insère [" + carte.getValeur() +
                    "] en " + choix.dir + " à l'index " + choix.index;

        if (utiliserTUI) {
            tui.afficherMessage(msg);
            j.getGrille().insererCarte(choix.index, choix.dir, carte, tui, j.getGrille());
        } else {
            System.out.println(msg);
            j.getGrille().insererCarte(choix.index, choix.dir, carte, null, j.getGrille());
        }
    }
    // La fonction transformerEntreeEnIndex permet de transformer une entree de l'utilisateur en un index
    private int transformerEntreeEnIndex(String s) {
        s = s.toUpperCase().trim();
        // Gère les lettres
        if (s.contains("A")) return 0;
        if (s.contains("B")) return 1;
        if (s.contains("C")) return 2;
        if (s.contains("D")) return 3;
        // Gère les chiffres (si le joueur tape 1 pour la colonne 1, on veut l'index 0)
        try {
            int val = Integer.parseInt(s);
            if (val >= 1 && val <= 4) return val - 1; // Convertit 1->0, 2->1...
            return val; // Si déjà 0-3
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    /* ====================
       BOUCLE PRINCIPALE
       ==================== */
// La fonction demarrerConsole permet de jouer en console
    public void demarrerConsole() {

        System.out.println("\n==============================");
        System.out.println("        DÉBUT DE LA PARTIE");
        System.out.println("==============================\n");

        int tour = 1;
        int meilleurScore = Integer.MAX_VALUE;

        while (true) {

            System.out.println("\n========== TOUR " + tour + " ==========\n");

            phaseRetrait();
            phaseInsertion();

            System.out.println("----------------------------------------\n");

            String rep = "";
            while (true) {
                System.out.print("Voulez-vous continuer ? (o/n) : ");
                rep = sc.next().trim().toLowerCase();
                if (rep.equals("o") || rep.equals("n")) break;
                System.out.println("Réponse invalide. Tapez uniquement 'o' ou 'n'.");
            }

            if (rep.equals("n")) {
                System.out.println("\n===== SCORES FINAUX =====");
                Joueur gagnant = null;
                for (Joueur joueur : joueurs) {
                    int score = joueur.getGrille().calculerScoreFinal();
                    System.out.println(joueur.getNom() + " : " + score);
                    if (score < meilleurScore) {
                        meilleurScore = score;
                        gagnant = joueur;
                    }
                }
                System.out.println("\n🏆 Le gagnant est : " + gagnant.getNom() + " !");
                System.out.println("\nVous avez quitté la partie.");
                return;
            }

            tour++;
        }
    }
    // La fonction demarrerTUI permet de jouer en TUI
    private void demarrerTUI() {
        
        tui.afficherMessage("DÉBUT DE LA PARTIE");

        int tour = 1;

        while (true) {
            tui.afficherMessage("TOUR " + tour);

            phaseRetrait();   // Utilise tui.choisirCaseCurseur ou tui.demanderTexte
            phaseInsertion(); // Utilise tui.demanderTexte(action, question, grille, centre)

            Grille grilleAffichage = joueurs.get(0).getGrille(); 
            
            // On passe "FIN DE TOUR" comme action et on inclut 'centre'
            String rep = tui.demanderTexte(
                "FIN DU TOUR " + tour, 
                "Voulez-vous continuer ? (o/n)", 
                grilleAffichage, 
                centre
            );

            if (rep.equalsIgnoreCase("n")) {
                StringBuilder sb = new StringBuilder();
                sb.append("SCORES : ");

                Joueur gagnant = null;
                int meilleurScore = Integer.MAX_VALUE;

                for (Joueur joueur : joueurs) {
                    int score = joueur.getGrille().calculerScoreFinal();
                    sb.append(joueur.getNom()).append("(").append(score).append(") ");

                    if (score < meilleurScore) {
                        meilleurScore = score;
                        gagnant = joueur;
                    }
                }

                if (gagnant != null) {
                    sb.append("| GAGNANT : ").append(gagnant.getNom());
                }

                // Affiche le bilan final avant de fermer
                tui.afficherMessage(sb.toString());
                
                // On quitte proprement le terminal Lanterna
                tui.quitter(); 
                return;
            }

            tour++;
        }
    }

    public void demarrer() {
        if (utiliserTUI) {
            demarrerTUI();
        } else {
            demarrerConsole();
        }
    }

    // Petite classe interne pour stocker le couple (index, direction)
    class Coup {
        int index;
        Direction dir;
        Coup(int i, Direction d) { this.index = i; this.dir = d; }
    }
}
