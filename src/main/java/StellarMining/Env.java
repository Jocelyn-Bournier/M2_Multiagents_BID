package StellarMining;

public class Env {
    private Coords bornes;
    private Coords posBase;
    private int nbOres;
    private Ores[] ores;

    public Env(Coords bornes, Coords posBase, int nbOres) {
        this.bornes = bornes;
        this.nbOres = nbOres;
        this.posBase = posBase;
        generateOres();
    }

    public Env(){
        //on part du principe que l'envrionnement est carré
        this.bornes = new Coords(0,9);
        this.posBase = new Coords(0,0);
        this.nbOres = 10;
        generateOres();
    }

    public void generateOres() {
        this.ores = new Ores[this.nbOres];
        for (int i = 0; i < this.nbOres; i++) {
            int x = (int) (Math.random() * (this.bornes.getY() + 1));
            int y = (int) (Math.random() * (this.bornes.getY() + 1));
            //taille tas comprise entre 2 et 4
            int taille_tas = (int) (Math.random() * (4 - 2) + 2);
            //pour pas que 2 tas soient sur la même case
            boolean is_available = false;
            while(!is_available || (x == 0 && y == 0)){
                is_available = true;
                for (int j = 0; j < i; j++) {
                    if (ores[j].getCoords().equals(new Coords(x,y))){
                        is_available = false;
                        x = (int) (Math.random() * (this.bornes.getY() + 1));
                        y = (int) (Math.random() * (this.bornes.getY() + 1));
                    }
                }
            }
            this.ores[i] = new Ores(x, y, taille_tas);
        }        
    }

    public Coords getBornes() {
        return bornes;
    }
    
    public Coords getPosBase() {
        return posBase;
    }

    public int checkOres(Coords pos){
        for (int i = 0; i < this.nbOres; i++) {
            if (ores[i].getCoords().equals(pos)){
                return ores[i].getTaille_tas();
            }
        }
        return 0;
    }

    public void mineOres(Coords pos, int nbOres){
        for (int i = 0; i < this.nbOres; i++) {
            if (ores[i].getCoords().equals(pos)){
                ores[i].mine(nbOres);
            }
        }
    }
    
}
