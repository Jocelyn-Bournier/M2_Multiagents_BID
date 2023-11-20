package StellarMining;
import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;

public class MiningAgent extends Agent {
    Coords posAgent;
    Coords PosBase;
    int bag = 0;
    int bagSize = 5;
    int fuel = 100;
    String name;

    public void setup() {
        posAgent = new Coords(0,0);
        PosBase = new Coords(0,0);
        name = getAID().getName();
        System.out.println("Hello! My name is " + name);
        System.out.println("My position is : (x:" + posAgent.getX() + ", y:" + posAgent.getY() + ")");
        System.out.println("My bag is empty : " + bag + "/" + bagSize);
        System.out.println("My fuel : " + fuel + "%");
        addBehaviour(new TickerBehaviour(this, 10000) {
            
            private static final long serialVersionUID = 1L;
            
            protected void onTick() {
                while(true){
                    addBehaviour(new Wandering());
                    System.out.println("I'm going back to base");
                    addBehaviour(new GoToBase());
                    System.out.println("I'm back to base");
                    addBehaviour(new refuelAndEmptyBag());
                    System.out.println("I'm going back to wandering");
                }
            }
        });

    }

    private void moveToCoords(Coords coords) {
        fuel -= 5;
        posAgent.updateCoords(coords.getX(), coords.getY());
    }

    private void moveToDirection(int direction) {
        fuel -= 5;
        Coords dest = new Coords(posAgent);
        switch (direction) {
            case 0:
                dest.updateCoords(posAgent.getX() + 1, posAgent.getY());
                break;
            case 1:
                dest.updateCoords(posAgent.getX(), posAgent.getY() + 1);
                break;
            case 2:
                dest.updateCoords(posAgent.getX() - 1, posAgent.getY());
                break;
            case 3:
                dest.updateCoords(posAgent.getX(), posAgent.getY() - 1);
                break;
        }
        moveToCoords(dest);
    }

    private class GoToBase extends Behaviour {
        public void action() {
            System.out.println("Going back to base");
            System.out.println("Fuel : " + fuel + "%");
            //update position
            moveToCoords(PosBase);
        }

        public boolean done() {
            return posAgent.equals(PosBase);
        }
    }

    private class Wandering extends Behaviour {
        public void action() {
            //random walk
            int direction = (int) (Math.random() * 4);
            moveToDirection(direction);
            System.out.println("Wandering");
            System.out.println("Fuel : " + fuel + "%");
            //update position
            //if position contains ore, mine it
        }

		public boolean done() {
            return fuel <= 20 || bag >= bagSize;
        }
    }

    private class refuelAndEmptyBag extends Behaviour {
        public void action() {
            fuel = 100;
            bag = 0;
            System.out.println("Refueled and emptied bag");
        }

        public boolean done() {
            return true;
        }
    }
}