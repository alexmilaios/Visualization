package grahics;

import graph.Node;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import matrices.OrthographicProjection;


import sort.SenderReceiverPairs;

@SuppressWarnings("serial")
public class Universe extends JPanel {

	private List<List<Vector4f>> list;
	private float axis_size;
	private List<Connection> connections;
	private List<List<Node>> levels;
	private Vector<SenderReceiverPairs> messages;
	private int numOfNodes;
	private Model model;
	private Vector3f camera_pos;
	
	public Universe( float axis_size, List<Connection> connections, List<List<Node>> levels, 
			Vector<SenderReceiverPairs> messages, int numOfNodes) {
		this.axis_size = axis_size;
		this.connections = connections;
		this.levels = levels;
		this.messages = messages;
		this.numOfNodes = numOfNodes;
		
		CameraModifier cameraMod = new CameraModifier();
		addMouseListener(cameraMod);
		addMouseMotionListener(cameraMod);
		addMouseWheelListener(new zoomModifier());
		
		model = new Model(numOfNodes,levels.size());
		camera_pos = new Vector3f(-4.35f, 4.0f, -35.0f);
		model.transformPoints(camera_pos);
		list = model.project(new OrthographicProjection());
		setDoubleBuffered(true);
	}

	public void paint(Graphics g){
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
		
		g2d.translate(w/2, 3.5*h/8);
		
		g2d.setColor(Color.black);
		
		drawPoints(g2d, scale);
		drawTimeLines(g2d, scale);
		drawInitialGraph(g2d,scale);
		drawMessages(g2d, scale);
	}
	
	private void drawPoints(Graphics2D g2d,float scale) {
		
		for(List<Vector4f> vectors : list) {
			for(Vector4f vec : vectors) {
				g2d.fillOval((int) Math.round(((vec.x)*scale))-3, 
						(int) Math.round((-vec.y)*scale)-3, 6, 6);
			}
		}
	}
	
 	private void drawTimeLines(Graphics2D g2d,float scale) {
		
		g2d.setColor(Color.green);
		for(int i = 0; i < list.get(1).size(); i++){
			Vector4f start = list.get(0).get(i);
			Vector4f end = list.get(list.size()-1).get(i);
			g2d.drawLine(Math.round(start.x*scale), Math.round(-start.y*scale), 
				Math.round(end.x*scale), Math.round(-end.y*scale));
		}
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
		g2d.drawLine(Math.round((list.get(sender_level).get(sender_node).x)*scale), 
				Math.round(-(list.get(sender_level).get(sender_node).y)*scale), 
				Math.round((list.get(receiver_level).get(receiver_node).x)*scale), 
				Math.round(-(list.get(receiver_level).get(receiver_node).y)*scale));
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
	
	
	class CameraModifier extends MouseAdapter {
		
		private int x,y;

		public void mousePressed(MouseEvent event) {
			x = event.getX();
			y = event.getY();
		} 
		
		public void mouseReleased(MouseEvent event) {
			Vector2f vector = new Vector2f((float) event.getX() - x, (float) event.getY() - y );
			vector.normalize();
			if(Math.abs(event.getX()- x) <  0.0001) {
				camera_pos = new Vector3f(-4.35f, 4.0f, -35.0f);
			}else {
				camera_pos = new Vector3f((float) (camera_pos.x + vector.x*4.0),(float) (camera_pos.y + vector.y*4.0), camera_pos.z);
				//camera_pos = calculateCordinates(vector);
			}
			model.transformPoints(camera_pos);
			list = model.project(new OrthographicProjection());
			repaint();
		}
		
//		private Vector3f calculateCordinates(Vector2f direction) {
//			Vector3f newCoordinates = new Vector3f();
//			Vector3f mag = new Vector3f(camera_pos);
//			
//			float phi = (float) Math.atan(camera_pos.y / camera_pos.x);
//			float theta = (float) Math.acos(camera_pos.z / mag.length()); 
//			System.out.println("phi: " + phi + " theta: " + theta + " lenght: " + mag.length());
//			phi += (float) (direction.x * (Math.PI/6)); 
//			theta += (float) (direction.y * (Math.PI/6));
//			
//			newCoordinates.x = (float) (mag.length() * Math.sin(theta) * Math.cos(phi));
//			newCoordinates.y = (float) (mag.length() * Math.sin(theta) * Math.sin(phi));
//			newCoordinates.z = (float) (mag.length() * Math.cos(theta));
//			
//			return newCoordinates;
//		}
	}
	
	class zoomModifier implements MouseWheelListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				float amount = e.getWheelRotation() * 5f;
				Vector3f mag = new Vector3f(camera_pos);
				
				float phi = (float) Math.atan(camera_pos.y / camera_pos.x);
				float theta = (float) Math.acos(camera_pos.z / mag.length());
				float newMag = mag.length() + amount;
	
				camera_pos.x = (float) (newMag * Math.sin(theta) * Math.cos(phi));
				camera_pos.y = (float) (newMag * Math.sin(theta) * Math.sin(phi));
				camera_pos.z = (float) (newMag * Math.cos(theta));
				
				model.transformPoints(camera_pos);
				list = model.project(new OrthographicProjection());
				repaint();
			}
		}
		
	}
}
