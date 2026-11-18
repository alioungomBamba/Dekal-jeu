package Dekal;
public class Carte {
    private final int valeur;
    private boolean estVisible;

    public Carte(int valeur) {
        if(valeur <1 || valeur>10){
            throw new IllegalArgumentException("La valeur de la carte doit etre entre 1 et 10.");
        } 
        this.valeur = valeur;
        estVisible = false;
    }
    //On retoure la valeur de la carte
    public int getValeur(){
        return valeur;
    }
    //On retoure la visibilité de la carte
    public boolean estVisible(){
        return estVisible;
    }
    //On rend la carte visible
    public void rendreVisible(){
        estVisible = true;
    }
    @Override
    public String toString() {
        return "Carte{" + "valeur=" + valeur + ", estVisible=" + estVisible + '}';
    }
}
