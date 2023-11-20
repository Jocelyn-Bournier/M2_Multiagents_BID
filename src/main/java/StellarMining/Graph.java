package StellarMining;

import java.util.ArrayList;

/**
 * This class represents a graph of nodes and edges.
 * 
 * It's used to represent the environment of the mining agents.
 * 
 */

class Edge {
    Coords from;
    Coords to;
    
    Edge(Coords from, Coords to){
        this.from = from;
        this.to = to;
    }

    public Coords getFrom() {
        return from;
    }

    public Coords getTo() {
        return to;
    }
}

public class Graph {
    //graphe constitué dune liste de noeud et d'arrêtes

    private ArrayList<Coords> nodes;
    private ArrayList<Edge> edges;
    private int nbCoordss;
    
    public Graph(){
        this.nbCoordss = 0;
        this.nodes = new ArrayList<Coords>();
        this.edges = new ArrayList<Edge>();
    }

    public void addCoords(Coords c){
        //chek if coords id already in nodes
        if (this.nodes.contains(c)){
            return;
        }
        this.nodes.add(c);
        this.nbCoordss++;
    }
    
    public void addEdge(Coords c1, Coords c2){
        if(this.edges.contains(new Edge(c1, c2))){
            return;
        }
        this.edges.add(new Edge(c1, c2));
        this.edges.add(new Edge(c2, c1));
    }

    public ArrayList<Coords> getNeighbours(Coords n) {
        ArrayList<Coords> neighbours = new ArrayList<Coords>();
        for (Edge e : this.edges){
            if (e.getFrom() == n){
                neighbours.add(e.getTo());
            }
        }
        return neighbours;
    }

    public int shortestPathCost(Coords start, Coords end){
        //return shortest path cost between start and end
        return this.dijkstra(start, end).size();
    }

    public ArrayList<Coords> shortestPath(Coords start, Coords end){
        //return shortest path between start and end
        return this.dijkstra(start, end);
    }

    //shortest path with dijkstra, return path length, graph is unweighted
    private ArrayList<Coords> dijkstra(Coords start, Coords end){
        ArrayList<Coords> unvisited = new ArrayList<Coords>();
        ArrayList<Coords> visited = new ArrayList<Coords>();
        ArrayList<Coords> path = new ArrayList<Coords>();
        int[] dist = new int[this.nbCoordss];
        int[] prev = new int[this.nbCoordss];
        int i = 0;
        for (Coords n : this.nodes){
            unvisited.add(n);
            dist[i] = Integer.MAX_VALUE;
            prev[i] = -1;
            i++;
        }
        dist[this.nodes.indexOf(start)] = 0;
        while(!unvisited.isEmpty()){
            Coords u = unvisited.get(0);
            for (Coords n : unvisited){
                if (dist[this.nodes.indexOf(n)] < dist[this.nodes.indexOf(u)]){
                    u = n;
                }
            }
            unvisited.remove(u);
            visited.add(u);
            for (Coords v : this.getNeighbours(u)){
                if (!visited.contains(v)){
                    int alt = dist[this.nodes.indexOf(u)] + 1;
                    if (alt < dist[this.nodes.indexOf(v)]){
                        dist[this.nodes.indexOf(v)] = alt;
                        prev[this.nodes.indexOf(v)] = this.nodes.indexOf(u);
                    }
                }
            }
        }
        Coords u = end;
        while (prev[this.nodes.indexOf(u)] != -1){
            path.add(u);
            u = this.nodes.get(prev[this.nodes.indexOf(u)]);
        }
        return path;
    }
}
