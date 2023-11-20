package StellarMining;

import java.util.ArrayList;
import java.util.HashSet;

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

    @Override
    public String toString() {
        return "Edge [from=" + from + ", to=" + to + "]";
    }
    
    @Override
    public boolean equals(Object e){
        return this.from.equals(((Edge)e).getFrom()) && this.to.equals(((Edge)e).getTo());
    }
}

public class Graph {
    //graphe constitué dune liste de noeud et d'arrêtes

    private HashSet<Coords> nodes;
    private HashSet<Edge> edges;
    private int nbCoordss;
    
    public Graph(){
        this.nbCoordss = 0;
        this.nodes = new HashSet<Coords>();
        this.edges = new HashSet<Edge>();
    }

    public HashSet<Coords> getNodes() {
        return nodes;
    }

    public HashSet<Edge> getEdges() {
        return edges;
    }

    public void addCoords(Coords c){
        if (this.nodes.add(c)){
            this.nbCoordss++;
        }
    }
    
    public void addEdge(Coords c1, Coords c2){
        if(!c1.equals(c2)){
            this.edges.add(new Edge(c1, c2));
            this.edges.add(new Edge(c2, c1));
        }
    }

    public HashSet<Coords> getNeighbours(Coords n) {
        HashSet<Coords> neighbours = new HashSet<Coords>();
        for (Edge e : this.edges){
            if (e.getFrom().equals(n)){
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
        ArrayList<Coords> nodes = new ArrayList<Coords>(this.nodes);
        int[] dist = new int[this.nbCoordss];
        int[] prev = new int[this.nbCoordss];
        int i = 0;
        for (Coords n : this.nodes){
            unvisited.add(n);
            dist[i] = Integer.MAX_VALUE;
            prev[i] = -1;
            i++;
        }
        dist[nodes.indexOf(start)] = 0;
        while(!unvisited.isEmpty()){
            Coords u = unvisited.get(0);
            for (Coords n : unvisited){
                if (dist[nodes.indexOf(n)] < dist[nodes.indexOf(u)]){
                    u = n;
                }
            }
            unvisited.remove(u);
            visited.add(u);
            for (Coords v : this.getNeighbours(u)){
                if (!visited.contains(v)){
                    int alt = dist[nodes.indexOf(u)] + 1;
                    if (alt < dist[nodes.indexOf(v)]){
                        dist[nodes.indexOf(v)] = alt;
                        prev[nodes.indexOf(v)] = nodes.indexOf(u);
                    }
                }
            }
        }
        Coords u = end;
        while (prev[nodes.indexOf(u)] != -1){
            path.add(u);
            u = nodes.get(prev[nodes.indexOf(u)]);
        }
        return path;
    }
}
