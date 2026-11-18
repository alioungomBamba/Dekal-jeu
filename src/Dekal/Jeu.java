package Dekal;

import java.util.Scanner;

public class Jeu {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1. Nouvelle partie (Humains, mode console)");
            System.out.println("2. Nouvelle partie (Humains, mode TUI)");
            System.out.println("3. Nouvelle partie contre l'ordinateur (console)");
            System.out.println("4. Nouvelle partie contre l'ordinateur (TUI)");
            System.out.println("5. Quitter");
            System.out.print("Votre choix : ");

            String choix = sc.next();

            // -------------------------
            // 1. Humains + Console
            // -------------------------
            if (choix.equals("1")) {
                Partie parti = new Partie(sc, false, false); // console, pas IA
                parti.demarrer();
            }

            // -------------------------
            // 2. Humains + TUI
            // -------------------------
            else if (choix.equals("2")) {
                Partie partie = new Partie(sc, false, true); // TUI, pas IA
                partie.demarrer();
            }

            // -------------------------
            // 3. IA + Console
            // -------------------------
            else if (choix.equals("3")) {
                Partie partie = new Partie(sc, true, false); // console, IA
                partie.demarrer();
            }

            // -------------------------
            // 4. IA + TUI
            // -------------------------
            else if (choix.equals("4")) {
                Partie partie = new Partie(sc, true, true); // TUI, IA
                partie.demarrer();
            }

            // -------------------------
            // 5. Quitter
            // -------------------------
            else if (choix.equals("5")) {
                System.out.println("Au revoir !");
                return;
            }

            else {
                System.out.println("Choix invalide.");
            }
        }
    }
}
