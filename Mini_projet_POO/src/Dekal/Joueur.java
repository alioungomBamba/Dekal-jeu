package Dekal;
public class Joueur {
    private String nom;
    private Grille grille;
    private int score;
    // pour la question 6, jouée avec l'IA
    private boolean estIA;

    public Joueur(String nom, boolean estIA) {
        this.nom = nom;
        this.grille = new Grille();
        this.grille.distribuerCartes(); // Prépare les 16 cartes
        this.score = 0;
        this.estIA = estIA;
    }
    // Getters...
    public String getNom(){
        return nom;
    }
    //Retourne le score du joueur
    public int getscore(){
        return score;
    }
    public Grille getGrille(){
        return this.grille;
    }
    public boolean estIA() {
        return estIA;
    }
    //Mise à jour du score
    public void mettreAJourScore() {
        this.score = grille.calculerScoreFinal();
    }
}
