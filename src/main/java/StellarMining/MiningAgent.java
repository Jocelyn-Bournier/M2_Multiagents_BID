package StellarMining;
import java.util.ArrayList;
import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;

public class MiningAgent extends Agent {
    Coords prevPosAgent;
    Coords posAgent;
    Coords posBase;
    int bag = 0;
    int bagSize = 5;
    int fuel = 100;
    String name;
    Env env;
    int mineCost = 4;
    int moveCost = 2;
    Graph envGraph;
    ArrayList<Coords> path;

    public void setup() {
        env = new Env();
        posBase = new Coords(env.getPosBase());
        prevPosAgent = new Coords(posBase);
        posAgent = new Coords(posBase);
        name = getAID().getName();
        envGraph = new Graph();
        envGraph.addCoords(posBase);
        envGraph.addEdge(posBase, posAgent);
        System.out.println("Hello! My name is " + name);
        System.out.println("My position is : (x:" + posAgent.getX() + ", y:" + posAgent.getY() + ")");
        System.out.println("My bag is empty : " + bag + "/" + bagSize);
        System.out.println("My fuel : " + fuel + "%");
        
        addBehaviour(new CyclicBehaviour() { 

            public void action() {
                System.out.println("I'm wandering");
                addBehaviour(new Wandering());
                System.out.println("I'm going back to base");
                addBehaviour(new GoToBase());
                System.out.println("I'm back to base");
                addBehaviour(new refuelAndEmptyBag());
                System.out.println("I refueled and emptied my bag");
            }
            
        });
    }

    private void moveToCoords(Coords coords, int cost) {
        fuel -= cost;
        posAgent.updateCoords(coords.getX(), coords.getY());
    }

    private ArrayList<Integer> checkActionPossible() {
        ArrayList<Integer> possible_actions = new ArrayList<Integer>(); 
        int borneInf = env.getBornes().getX();
        int borneSup = env.getBornes().getY();
        if (posAgent.getX() < borneSup) {
            possible_actions.add(0);
        }
        if (posAgent.getY() < borneSup) {
            possible_actions.add(1);
        }
        if (posAgent.getX() > borneInf) {
            possible_actions.add(2);
        }
        if (posAgent.getY() > borneInf) {
            possible_actions.add(3);
        }
        return possible_actions;
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
        prevPosAgent = new Coords(posAgent);
        posAgent = new Coords(dest);
        moveToCoords(dest, moveCost);
        envGraph.addCoords(posAgent);
        envGraph.addEdge(prevPosAgent, posAgent);
    }

    private class GoToBase extends Behaviour {
        public void action() {
            System.out.println("Going back to base");
            System.out.println("Fuel : " + fuel + "%");
            //find path to base
            //move to base with appropriate cost
            envGraph.shortestPathCost(posAgent,posBase);
        }

        public boolean done() {
            System.out.println("Done going back to base");
            return posAgent.equals(posBase);
        }
    }

    private class Wandering extends Behaviour {
        public void action() {
            //random walk, need to check if the action is possible before doing it
            ArrayList<Integer> actions = checkActionPossible();
            int rd_ind_direction = (int) (Math.random() * actions.size());
            moveToDirection(actions.get(rd_ind_direction));
            
            System.out.println("Wandering");
            System.out.println("Fuel : " + fuel + "%");
            //if position contains ore, mine it
            if (env.checkOres(posAgent) > 0) {
                System.out.println("Found ore");
                addBehaviour(new Mine());
            }
        }

		public boolean done() {
            //si la quantité de fuel est suffisante pour rentrer + 2*moveCost pour avoir une hysteresis
            System.out.println("Wandered to : (x:" + posAgent.getX() + ", y:" + posAgent.getY() + ")");
            return fuel <= ((envGraph.shortestPathCost(posAgent, posBase) * moveCost) + moveCost*2) || bag >= bagSize;
        }
    }

    private class Mine extends Behaviour {
        public void action() {
            System.out.println("Mining");
            System.out.println("Fuel : " + fuel + "%");
            env.mineOres(posAgent, 1);
            fuel -= mineCost;
        }

        public boolean done() {
            System.out.println("Done mining");
            return env.checkOres(posAgent) == 0 || fuel <= ((envGraph.shortestPathCost(posAgent, posBase) * moveCost) + moveCost*2) || bag >= bagSize;
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