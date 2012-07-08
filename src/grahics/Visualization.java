package grahics;

import graph.Node;

import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import sort.SenderReceiverPairs;

public class Visualization {

	public Visualization(int numOfNodes,List<Connection> connections,List<List <Node>> levels, Vector<SenderReceiverPairs> messages) {

		//List<Vector4f> result = model.project(new PerspectiveProjection(10.0f));
		JFrame frame = new JFrame("Points");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new Universe(46.0f,connections,levels,messages,numOfNodes));
		frame.setSize(700, 700);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}	
}

