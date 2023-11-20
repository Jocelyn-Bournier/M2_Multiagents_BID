package StellarMining;

public class Ores {

    private Coords coords;
    private int taille_tas;

    Ores(int x, int y, int taille_tas) {
        this.coords = new Coords(x, y);
        this.taille_tas = taille_tas;
    }

    Ores(Coords coords, int taille_tas) {
        this.coords = coords;
        this.taille_tas = taille_tas;
    }

    public Coords getCoords() {
        return coords;
    }

    public int getTaille_tas() {
        return taille_tas;
    }

    public void mine(int nbOres) {
        this.taille_tas -= nbOres;
    }
}
