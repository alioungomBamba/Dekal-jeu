package Dekal;
import java.util.EnumMap;
import java.util.Map;
public class Case {
    
    private Carte carte;
    private final  Map<Direction, Case> voisins;

    public Case() {
        this.carte = null;
        this.voisins = new EnumMap<>(Direction.class);
    }

    public Carte getCarte() {
        return carte;
    }
    public void setCarte(Carte carte) {
        this.carte = carte;
    }
    public boolean estVide() {
        return this.carte == null;
    }

    //setVoisin : Établit un lien bidirectionnel entre deux cases.
    public void setVoisin(Direction direction, Case voisin) {
    // 1. On enregistre le voisin dans notre propre Map
        this.voisins.put(direction, voisin);

        // 2. Si le voisin existe et qu'il ne nous pointe pas encore, 
        //    on lui demande de nous ajouter en face de lui.
        if (voisin != null && voisin.getVoisin(direction.getOppose()) != this) {
            voisin.setVoisin(direction.getOppose(), this);
            }
    }
    // getVoisin : Récupère la case adjacente dans une direction donnée.
    public Case getVoisin(Direction direction) {
        return voisins.get(direction);
    }

    public String afficher() {
        if (carte == null) return " . ";
        return carte.estVisible() ? " " + carte.getValeur() + " " : " ?";
    }
        //Question 3
    /*Cette méthode implémente un décalage récursif qui déplace les cartes une par une vers 
    la case voisine jusqu'à rencontrer un bord ou une case vide.
    Si un vide est trouvé sur le chemin, la récursion s'arrête préventivement pour combler le trou, 
    évitant ainsi l'expulsion inutile d'une carte hors de la grille.  */  
    public Carte shift(Direction dir, Carte nouvelleCarte, TUIJeu tui, Grille g) {
        // 1. Déterminer le voisin 
        Direction dirVoisin;
        switch (dir) {
            case Gauche:  dirVoisin = Direction.Droite; break;
            case Droite:  dirVoisin = Direction.Gauche; break;
            case Haut:    dirVoisin = Direction.Bas;    break;
            case Bas:     dirVoisin = Direction.Haut;   break;
            default: return null;
        }

        // 2. Sauvegarder la carte actuelle
        Carte carteActuelle = this.carte;

        // 3. SI LA CASE EST VIDE : On pose la carte et on s'arrête !
        if (carteActuelle == null) {
            this.carte = nouvelleCarte;
            if (this.carte != null) this.carte.rendreVisible();
            return null; // Rien n'est expulsé car on a rempli un trou
        }

        // 4. SI LA CASE N'EST PAS VIDE : On continue la propagation
        Carte carteExpulsee;
        Case voisin = getVoisin(dirVoisin);

        if (voisin != null) {
            carteExpulsee = voisin.shift(dir, carteActuelle, tui, g);
        } else {
            carteExpulsee = carteActuelle; // Bord atteint
        }

        // On place la nouvelle carte ici
        this.carte = nouvelleCarte;
        if (this.carte != null) this.carte.rendreVisible();

        try {
            if (tui != null && g != null) {
                tui.afficherGrille(g, -1, -1);
                Thread.sleep(80);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return carteExpulsee;
    }
    // Question 4
    // La fonction RetournerValeurScore calcule les points de la grille en demandant à chaque case sa valeur
    public int RetournerValeurScore() {

    // 1. Si la case est vide, on compte 0
        if (this.carte == null) return 0;

        // 2. Si la carte est cachée → elle ne compte pas
        if (!this.carte.estVisible()) return 0;

        int valeur = this.carte.getValeur();

        // 3.On vérifie les voisins pour neutralisation
        for (Direction d : Direction.values()) {
            Case voisin = getVoisin(d);

            if (voisin != null && voisin.getCarte() != null) {

                // Si le voisin est visible et a la même valeur → neutralisée
                if (voisin.getCarte().estVisible() &&
                    voisin.getCarte().getValeur() == valeur) {
                    return 0;
                }
            }
        }

        // 4. Si la carte visible et isolée, on compte sa valeur
        return valeur;
    }

    @Override
    public String toString() {
        if (carte == null) return " . ";
        return carte.estVisible() ? " " + carte.getValeur() + " " : " ?";
    }
}
