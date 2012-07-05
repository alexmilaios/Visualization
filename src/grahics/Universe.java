package grahics;

import graph.Node;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.vecmath.Vector4f;

import sort.SenderReceiverPairs;

@SuppressWarnings("serial")
public class Universe extends JPanel {

	private List<List<Vector4f>> list;
	private float axis_size;
	private List<Connection> connections;
	private List<List<Node>> levels;
	private Vector<SenderReceiverPairs> messages;
	private int numOfNodes;

	public Universe(List<List<Vector4f>> list, float axis_size, 
			List<Connection> connections, List<List<Node>> levels, Vector<SenderReceiverPairs> messages, int numOfNodes) {
		this.list = list;
		this.axis_size = axis_size;
		this.connections = connections;
		this.levels = levels;
		this.messages = messages;
		this.numOfNodes = numOfNodes;
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		
		Dimension size = getSize();
		Insets insets =  getInsets();

		int w = size.width - insets.left - insets.right;
		int h = size.height - insets.top - insets.bottom;
		
		float scale = w/ axis_size;  
		
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);


		rh.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		g2d.setRenderingHints(rh);

		g2d.translate(w/2, 3*h/4);
		
		g2d.setColor(Color.black);
		
		
		for(List<Vector4f> vectors : list) {
			for(Vector4f vec : vectors) {
				g2d.fillOval((int) Math.round(((vec.x)*scale))-3, 
						(int) Math.round((-vec.y)*scale)-3, 6, 6);
			}
		}
		
		drawInitialGraph(g2d,scale);
		drawMessages(g2d, scale);
	}
	
	private void drawMessages(Graphics2D g2d,float scale) {
		
		// start from the 1 layer and not 0
		for(int i = 1; i < levels.size(); i++) {
			List<Node> level = levels.get(i);
			for(int j = 0; j < level.size(); j++){
				Node receiver = level.get(j);
				Node sender = getSender(receiver);			
				int identity_receiver = receiver.pair.getNode()-1;
				int identity_sender = (sender.pair.getNode() == 0) ? numOfNodes : sender.pair.getNode()-1;
				
				int level_sender = getSenderLevel(sender);
				
				drawLinesMessages(g2d, scale, identity_sender, level_sender, identity_receiver, i);
				
				//currentLevel[identity_receiver] = i;
			}
		}
	}
	
	private int getSenderLevel(Node sender) {
		for(int i = 0; i < levels.size(); i++){
			for(Node n : levels.get(i)){
				if(n.toString().equals(sender.toString())){
					return i;
				}
			}
		}
		return -1;
	}
	
	private void drawLinesMessages(Graphics2D g2d, float scale, int sender_node,
			int sender_level, int receiver_node, int receiver_level) {
		g2d.setColor(Color.red);
		g2d.drawLine(Math.round((list.get(sender_level).get(sender_node).x)*scale), Math.round(-(list.get(sender_level).get(sender_node).y)*scale), 
				Math.round((list.get(receiver_level).get(receiver_node).x)*scale), Math.round(-(list.get(receiver_level).get(receiver_node).y)*scale));
	}
	
	
	private Node getSender(Node receiver){
		for(int i = 0; i < messages.size(); i++) {
			SenderReceiverPairs pair = messages.elementAt(i);
			if(pair.getReceiver().toString().equals(receiver.toString())) {
				return pair.getSender();
			}
		}
		return null;
	}
	
	private void drawInitialGraph(Graphics2D g2d, float scale) {
		g2d.setColor(Color.blue);
		for(Connection con : connections) {
			g2d.drawLine(Math.round((list.get(0).get(con.from).x)*scale), Math.round(-(list.get(0).get(con.from).y)*scale), 
					Math.round((list.get(0).get(con.to).x)*scale), Math.round(-(list.get(0).get(con.to).y)*scale));
		}
	}
}
