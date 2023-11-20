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

    @Override
    public boolean equals(Object c) {
        return this.x == ((Coords)c).getX() && this.y == ((Coords)c).getY();
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
}
