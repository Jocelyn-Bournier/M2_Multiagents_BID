package StellarMining;

public class Coords {
    private int x;
    private int y;

    private int borne = 10;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coords(Coords coords) {
        this.x = coords.getX();
        this.y = coords.getY();
    }

    public void updateCoords(int x, int y) {
        if(x >= 0 && x < this.borne){
            this.x = x;
        }
        if(y >= 0 && y < this.borne){
            this.y = y;
        }
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }

    public boolean equals(Coords coords) {
        return (this.x == coords.getX() && this.y == coords.getY());
    }
}
