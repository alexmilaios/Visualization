package grahics;

import graph.Node;

import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.vecmath.Vector4f;

import sort.SenderReceiverPairs;

import matrices.OrthographicProjection;

public class Visualization {

	public Visualization(int numOfNodes,List<Connection> connections,List<List <Node>> levels, Vector<SenderReceiverPairs> messages) {
		
		Model model = new Model(numOfNodes,levels.size());

		model.transformPoints();

		List<List<Vector4f>> result = model.project(new OrthographicProjection());

		//List<Vector4f> result = model.project(new PerspectiveProjection(10.0f));
		
		
		JFrame frame = new JFrame("Points");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new Universe(result, 40.0f,connections,levels,messages,numOfNodes));
		frame.setSize(700, 700);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}	
}

