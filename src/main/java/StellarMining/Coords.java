package StellarMining;

public class Coords {
    private int x;
    private int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coords(Coords coords) {
        this.x = coords.getX();
        this.y = coords.getY();
    }

    public void updateCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }

    public boolean equals(Coords coords) {
        return (this.x == coords.getX() && this.y == coords.getY());
    }
}
