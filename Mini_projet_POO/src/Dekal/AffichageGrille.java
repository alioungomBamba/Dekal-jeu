package Dekal;
// Affichage de la grille
public class AffichageGrille {
    
    public static void afficher(Grille g) {
        String RESET = "\u001b[0m";
        String BOLD = "\u001b[1m";
        String CYAN = "\u001b[36m";
        String YELLOW = "\u001b[33m";
        String RED = "\u001b[31m";

        System.out.println("\n      " + BOLD + "1      2      3      4" + RESET);
        System.out.println("   ┌──────┬──────┬──────┬──────┐");

        char lettreLigne = 'A';
        for (int i = 0; i < 4; i++) {
            System.out.print(" " + BOLD + lettreLigne + RESET + " │");
            for (int j = 0; j < 4; j++) {
                Case cell = g.getCase(i, j);
                Carte c = cell.getCarte();
                String rendu;

                if (c == null) {
                    rendu = "[vide]";
                } else if (!c.estVisible()) {
                    rendu = CYAN + " ???  " + RESET; 
                } else {
                    int val = c.getValeur();
                    if (g.aVoisinIdentique(i, j)) {
                        rendu = RED + String.format("(%2d) ", val) + RESET;
                    } else {
                        rendu = YELLOW + String.format("  %2d  ", val) + RESET;
                    }
                }
                System.out.print(rendu + "│");
            }
            System.out.println();
            if (i < 3) System.out.println("   ├──────┼──────┼──────┼──────┤");
            else       System.out.println("   └──────┴──────┴──────┴──────┘");
            lettreLigne++;
        }
    }
}