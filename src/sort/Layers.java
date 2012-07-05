package sort;

import graph.Graph;
import graph.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;



public class Layers {

	public List<List<Node>> layers;
	public Vector<SenderReceiverPairs> messages;

	public Layers(File file) {
		try {
			FileInputStream stream1 = new FileInputStream(file);
			FileInputStream stream2 = new FileInputStream(file);
			
			TraceReader reader = new TraceReader(stream1);
			Vector<Node> list = reader.getPairs();
			
			Node array [] = new Node [list.size()];

			for(int i = 0; i < array.length; i++) {
				array[i] = list.elementAt(i);
			}
			
			reader = new TraceReader(stream2);
			messages = reader.getSRPairs();
			
			
			for(int i = 0; i < array.length; i++) {
				int index;
				Node tmp = new Node(new Pair(array[i].pair.getNode(),array[i].pair.getTime()+1));
				if((index = getIndex(array, tmp)) != -1){
					array[i] = array[i].addEdge(array[index]);
				}
			}
						
			for(int i = 0; i < messages.size(); i++) {
				int index = getIndex(array,messages.elementAt(i).getSender());
				int dest = getIndex(array, messages.elementAt(i).getReceiver());
				array[index] = array[index].addEdge(array[dest]);
			}
			
			Graph graph = new Graph(array);
			
			layers = graph.topologicalSort();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static int getIndex(Node [] array, Node node) {
		for(int i = 0; i < array.length; i++) {
			if(array[i].toString().equals(node.toString()))
				return i;
		}
		return -1;
	}
}
