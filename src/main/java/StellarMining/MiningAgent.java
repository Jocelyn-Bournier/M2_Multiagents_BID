package StellarMining;
import java.util.ArrayList;

import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;


public class MiningAgent extends Agent {
    String name;
    Coords prevPosAgent;
    Coords posAgent;
    Coords posBase;
    Coords posOtherAgent;
    int bag;
    int fuel;
    int ticks;
    Env env;
    int bagSize = 5;
    int mineCost = 4;
    int moveCost = 2;
    int hysteresis = moveCost * 3;
    Graph envGraph;
    ArrayList<Coords> path;

	public void setup() {
        bag = 0;
        fuel = 100;
        env = Env.getInstance();
        env.addRobot(this.getAID());
        posBase = new Coords(env.getPosBase());
        prevPosAgent = new Coords(posBase);
        posAgent = new Coords(posBase);
        name = getAID().getName();
        envGraph = new Graph();
        envGraph.addCoords(posBase);
        System.out.println("Hello! My name is " + name);
        System.out.println("My position is : (x:" + posAgent.getX() + ", y:" + posAgent.getY() + ")");
        System.out.println("My bag is empty : " + bag + "/" + bagSize);
        System.out.println("My fuel : " + fuel);

        addBehaviour(new ReceiveMessage());
        
        TickerBehaviour actions_cycle = new TickerBehaviour(this, 2000) { 
            protected void onTick() {
                System.out.println("Starting a cycle");
                SequentialBehaviour seq = new SequentialBehaviour();
                seq.addSubBehaviour(new Wandering());
                seq.addSubBehaviour(new GoToBase());
                seq.addSubBehaviour(new RefuelAndEmptyBag());
                addBehaviour(seq);
            }
        };

        addBehaviour(actions_cycle);
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
        //check if there is an agent on the next position and remove that possibility to avoid collision
        if(posOtherAgent != null){
            for (int i = 0; i < possible_actions.size(); i++) {
                Coords dest = new Coords(posAgent);
                switch (possible_actions.get(i)) {
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
                if (dest.equals(posOtherAgent)) {
                    possible_actions.remove(i);
                }
            }
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
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("move to " + posAgent);
        for (AID aid : env.getRobotsAID()) {
            if(!aid.equals(this.getAID())){
                msg.addReceiver(aid);
            }
        }
        send(msg);
        envGraph.addCoords(posAgent);
        envGraph.addEdge(prevPosAgent, posAgent);
    }

    private class ReceiveMessage extends CyclicBehaviour {
        public void action() {
            // Attendre la réception d'un message
            ACLMessage msg = receive();

            // Vérifier si un message a été reçu
            if (msg != null) {
                String content = msg.getContent();
                
                if (content.contains("move to")) {
                    String[] parts = content.split(" ");
                    int x = Integer.parseInt(parts[2].substring(1, 2));
                    int y = Integer.parseInt(parts[2].substring(3,4));
                    posOtherAgent = new Coords(x, y);
                    System.out.println("Other agent moved to : (x:" + posOtherAgent.getX() + ", y:" + posOtherAgent.getY() + ")");
                }
            } else {
                // Si aucun message n'a été reçu, bloquer le comportement
                block();
            }
        }
    }

    private class Wandering extends Behaviour {
        public void action() {
            //random walk, need to check if the action is possible before doing it
            ArrayList<Integer> actions = checkActionPossible();
            int rd_ind_direction = (int) (Math.random() * actions.size());
            moveToDirection(actions.get(rd_ind_direction));
            System.out.println("Wandering to : (x:" + posAgent.getX() + ", y:" + posAgent.getY() + ")");
            //if position contains ore, mine it
            if (env.checkOres(posAgent) > 0) {
                System.out.println("Found ore");
                addBehaviour(new Mine());
            }
        }
        
        public boolean done() {
            //si la quantité de fuel est suffisante pour rentrer + 2*moveCost pour avoir une hysteresis
            System.out.println("Wandered, Fuel : " + fuel);
            ArrayList<Coords> path = envGraph.shortestPath(posBase, posAgent);
            System.out.println("Path to base : ");
            for (Coords coords : path) {
                System.out.println(coords);
            }
            if(((envGraph.shortestPathCost(posAgent, posBase) * moveCost) + hysteresis) >= fuel){
                System.out.println("Not enough fuel, go back to base");
            }
            if(bag >= bagSize){
                System.out.println("Not enough place in the bag, go back to base");
            }
            return fuel <= ((envGraph.shortestPathCost(posAgent, posBase) * moveCost) + hysteresis) || bag >= bagSize;
        }
    }

    private class Mine extends Behaviour {
        public void action() {
            System.out.println("Mining");
            System.out.println("Fuel : " + fuel);
            env.mineOres(posAgent, 1);
            fuel -= mineCost;
        }

        public boolean done() {
            System.out.println("Done mining");
            return env.checkOres(posAgent) == 0 || fuel <= ((envGraph.shortestPathCost(posAgent, posBase) * moveCost) + hysteresis) || bag >= bagSize;
        }
    }
    
    private class GoToBase extends Behaviour {
        public void action() {
            System.out.println("Going back to base");
            System.out.println("Fuel : " + fuel);
            //find path to base
            //move to base with appropriate cost
            int cost = envGraph.shortestPathCost(posAgent, posBase);
            moveToCoords(posBase, cost);
        }

        public boolean done() {
            return posAgent.equals(posBase);
        }
    }

    private class RefuelAndEmptyBag extends Behaviour {
        public void action() {
            fuel = 100;
            bag = 0;
            
        }
        
        public boolean done() {
            System.out.println("Refueled and emptied bag");
            return true;
        }
    }
}