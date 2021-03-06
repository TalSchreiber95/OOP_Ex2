package api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * This class represents a directed, weighted graph data structure.
 * Graph data is stored in HashMaps inorder to achieve O(1) access to nodes and edges.
 * The graph supports some methodology like adding/removing nodes/edges from the graph,
 * connecting nodes on the graph and holding counts of edge size and node size.
 */
public class DWGraph_DS implements directed_weighted_graph {

    private int edgeSize;
    private int countMC;
    private HashMap<Integer, node_data> nodes; //A map that stores nodes on the graph
    private HashMap<Integer, HashMap<Integer, edge_data>> outEdges; //A map that stores outgoing edges on the graph.
    private HashMap<Integer, HashMap<Integer, edge_data>> inEdges; //A map that stores incoming edges on the graph.
    /*Note: outEdges and inEdges are both of type HashMap, and each contains the in/out-going edges from src to dest.
     * If node 3 has an edge TO node 5, then obtain it by: edge_data e = outEdges.get(3).get(5)
     * If node 5 has an edge FROM node 3, the obtain it by: edge_data e = inEdges.get(5).get(3) */

    /**
     * Empty constructor.
     */
    public DWGraph_DS() {
        edgeSize = 0;
        countMC = 0;
        nodes = new HashMap<Integer, node_data>();
        outEdges = new HashMap<Integer, HashMap<Integer, edge_data>>();
        inEdges = new HashMap<Integer, HashMap<Integer, edge_data>>();
    }

    /**
     * Deep copy constructor.
     * @param g
     */
    public DWGraph_DS(directed_weighted_graph g) {
        if (g != null) {
            nodes = new HashMap<Integer, node_data>();
            outEdges = new HashMap<Integer, HashMap<Integer, edge_data>>();
            inEdges = new HashMap<Integer, HashMap<Integer, edge_data>>();

            for (node_data v : g.getV()) {
                node_data vCopy = new NodeData(v); //Copy original node 'v' into 'vCopy'
                nodes.put(vCopy.getKey(), vCopy);
                outEdges.put(vCopy.getKey(), new HashMap<Integer, edge_data>());
                inEdges.put(vCopy.getKey(), new HashMap<Integer, edge_data>());

                for (edge_data e : g.getE(v.getKey())) { //Iterate over outgoing edges from original node 'v'
                    edge_data eCopy = new EdgeData(e); //Copy edge info
                    outEdges.get(vCopy.getKey()).put(eCopy.getDest(), eCopy); //Update copied map
                }
                for (edge_data e : getInE(v.getKey())) { //Iterate over incoming edges into original node 'v'
                    edge_data eCopy = new EdgeData(e); //Copy edge info
                    inEdges.get(vCopy.getKey()).put(eCopy.getSrc(), eCopy); //Update copied map
                }
            }
            countMC = g.getMC();
            edgeSize = g.edgeSize();
        }
    }


    /**
     * returns the node_data by the node_id,
     *
     * @param key - the node_id
     * @return the node_data by the node_id, null if none.
     */
    @Override
    public node_data getNode(int key) {
        return nodes.get(key);
    }

    /**
     * returns the data of the edge (src,dest), null if none.
     * Note: this method should run in O(1) time.
     *
     * @param src - source node
     * @param dest - destination node
     * @return - the edge connecting both nodes, if there exists one.
     */
    @Override
    public edge_data getEdge(int src, int dest) {
        if (!nodes.containsKey(src) || !nodes.containsKey(dest)) return null;
        return outEdges.get(src).get(dest);
    }

    /**
     * adds a new node to the graph with the given node_data.
     * Note: this method should run in O(1) time.
     *
     * @param n - node
     */
    @Override
    public void addNode(node_data n) {
        if (!nodes.containsKey(n.getKey())) { //If a new node is being added to the graph -
            nodes.put(n.getKey(), n); //Add it to the nodes map.
            outEdges.put(n.getKey(), new HashMap<Integer, edge_data>()); //Init its out-going edge map.
            inEdges.put(n.getKey(), new HashMap<Integer, edge_data>()); //Init its in-coming edge map.
            countMC++; //Count 1 meta-change.
        }
    }

    /**
     * Connects an edge with weight w between node src to node dest.
     * * Note: this method should run in O(1) time.
     *
     * @param src  - the source of the edge.
     * @param dest - the destination of the edge.
     * @param w    - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w) {
        if (!(nodes.containsKey(src) && nodes.containsKey(dest) && src != dest)) return;
        if (w < 0) return;

        if (!outEdges.get(src).containsKey(dest))  //If edge (src,dest) did not exist before, increment edgeSize.
            edgeSize++;
        edge_data edge = new EdgeData(src, dest, w); //Generate a new edge from src to dest with weight 'w'.
        outEdges.get(src).put(dest, edge); //Put a new outgoing edge from src to dest
        inEdges.get(dest).put(src, edge); //Put a new incoming edge from dest to src
        countMC++;
    }

    /**
     * This method returns a pointer (shallow copy) for the
     * collection representing all the nodes in the graph.
     * Note: this method should run in O(1) time.
     *
     * @return Collection<node_data>
     */
    @Override
    public Collection<node_data> getV() {
        return nodes.values();
    }

    /**
     * This method returns a pointer (shallow copy) for the
     * collection representing all the edges getting out of
     * the given node (all the edges starting (source) at the given node).
     * @Runtime: this method runs in O(k) time, k being the collection size.
     *
     * @param node_id - node key
     * @return Collection<edge_data>
     */
    @Override
    public Collection<edge_data> getE(int node_id) {
        return outEdges.get(node_id).values(); //This is the outgoing edge_data collection for 'node_id'.
    }
    /**
     * This method returns a pointer (shallow copy) for the
     * collection representing all the edges getting into
     * the given node.
     * @Runtime: this method runs in O(k) time, k being the collection size.
     *
     * @param node_id - node key
     * @return Collection<edge_data>
     */
    public Collection<edge_data> getInE(int node_id) {
        return inEdges.get(node_id).values(); //This is the outgoing edge_data collection for 'node_id'.
    }

    /**
     * Deletes the node (with the given ID) from the graph -
     * and removes all edges which starts or ends at this node.
     * @Runtime: This method runs in O(k), V.degree=k, as all the edges should be removed.
     *
     * @param key - the node to delete.
     * @return the data of the removed node (null if none).
     */
    @Override
    public node_data removeNode(int key) {
        if (!nodes.containsKey(key)) return null;

        Iterator<edge_data> itr = getE(key).iterator();
        while (itr.hasNext()) { //Remove all outgoing edges from 'key'
            edge_data e = itr.next(); //Hold the edge src --> dest(i)
            itr.remove(); //This fixes a ConcurrentModificationException error
            removeEdge(e.getSrc(), e.getDest());
        }

        itr = inEdges.get(key).values().iterator();
        while (itr.hasNext()) { //Remove all incoming edges to 'key'
            edge_data e = itr.next(); //Hold the edge from dest(i) --> src
            itr.remove(); //This fixes a ConcurrentModificationException error
            removeEdge(e.getSrc(), e.getDest());
        }
        return nodes.remove(key);
    }

    /**
     * Deletes the edge from the graph,
     * @Runtime: this method runs in O(1) time.
     *
     * @param src - source node.
     * @param dest - dest node.
     * @return the data of the removed edge (null if none).
     */
    @Override
    public edge_data removeEdge(int src, int dest) {
        if (getNode(src) != null && getNode(dest) != null && src != dest) {
//            if (getEdge(src, dest) != null) {
                inEdges.get(dest).remove(src);
                edgeSize--;
                return outEdges.get(src).remove(dest);
//            }
        }
        return null;
    }

    /**
     * Returns the number of vertices (nodes) in the graph.
     * @Runtime: this method runs in O(1) time.
     *
     * @return - graph node size.
     */
    @Override
    public int nodeSize() {
        return nodes.size();
    }

    /**
     * Returns the number of edges (assume directional graph).
     * @Runtime: this method runs in O(1) time.
     *
     * @return - graph edge size.
     */
    @Override
    public int edgeSize() {
        return edgeSize;
    }

    /**
     * Returns the Mode Count - for testing changes in the graph.
     *
     * @return - the meta changes made on this graph.
     */
    @Override
    public int getMC() {
        return countMC;
    }

    @Override
    public String toString() {
        String str = "";
        for (Integer x : nodes.keySet()) {
            str += "" + x + " --> out [";
            for (edge_data i : getE(x)) {
                str += i.getDest() + " (" + outEdges.get(x).get(i.getDest()).getWeight() + ") , ";//EdgeMap.get(x).keySet().toString() + " \n ";
            }
            str += "] \n";
            str += "" + x + " --> in [";
            for (edge_data i : getInE(x)) {
                str += i.getSrc() + " (" + inEdges.get(x).get(i.getSrc()).getWeight() + ") , ";//EdgeMap.get(x).keySet().toString() + " \n ";
            }
            str += "] \n";
        }
        return str + " ";
    }

    /**
     * Override equals() method to compare graphs
     * by: node size, edge size, and outgoing edges.
     * @param o - graph to compare to.
     * @return - true or false if graphs are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DWGraph_DS graph_ds = (DWGraph_DS) o;
        return edgeSize == graph_ds.edgeSize &&
                Objects.equals(nodes, graph_ds.nodes) &&
                Objects.equals(outEdges, graph_ds.outEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edgeSize, nodes, outEdges);
    }
}
