package Dekal;

public enum Direction {
    Gauche, Droite, Haut, Bas;
   
    public  Direction getOppose() {
        switch (this) {
            case Gauche:
                return Droite;
            case Droite:
                return Gauche;
            case Haut:
                return Bas;
            case Bas:
                return Haut;
            default:
                return null;
        }
    }
}