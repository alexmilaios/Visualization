package main;

import graph.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import grahics.Connection;
import grahics.Visualization;

import sort.Layers;
import sort.SenderReceiverPairs;

public class Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Layers layers = new Layers(new File("trace1.txt"));
		
		List<List<Node>> levels = layers.layers; 
		Vector<SenderReceiverPairs> messages = layers.messages; 
		
		List<Connection> connections = new ArrayList<Connection>();
		connections.add(new Connection(0, 1));
		connections.add(new Connection(1, 2));
		connections.add(new Connection(2, 3));
		connections.add(new Connection(3, 0));
		
		new Visualization(4,connections,levels,messages);
	}

}
