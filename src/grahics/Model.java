package grahics;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import matrices.MyMatrix;
import matrices.OrthographicProjection;
import matrices.RotationXMatrix;
import matrices.ViewingMatrix;

public class Model {

	private List<List<Vector4f>> list;
	
	private int layers, nodes;
	
	public Model(int nodes, int layers) {
		this.layers = layers;
		this.nodes = nodes;
		list = new ArrayList <List<Vector4f>>();

		//Vector3f center_pos = new Vector3f(1.0f, 5.0f, -5.0f);
		//Vector3f direction = new Vector3f(-0.12f, -0.8f, 1.0f);
		//camera = new ViewingMatrix(center_pos, direction);
	}

	private void recoputeModel() {
		list = new ArrayList <List<Vector4f>>();

		for(int i = 0; i < layers; i++) {
			List<Vector4f> newLayer = new ArrayList<Vector4f>();
			
			for( int j = 0; j < nodes; j++) {
				Vector4f vec = new Vector4f( 12.0f * ((float) Math.cos((2*Math.PI/nodes)*(j) + Math.PI/4 )), 
						(float) 3*(layers-i-1), 12.0f *((float) Math.sin((2*Math.PI/nodes)*(j) + Math.PI/4 )), 1.0f );
				newLayer.add(vec);
			}
			if(i == 0) {
				Vector4f vec = new Vector4f( 0.0f, (float) 3*(layers-i-1), 0.0f, 1.0f );
				newLayer.add(vec);
			}
			list.add(newLayer);
		}
	}
	
	public Vector3f getDirection(int layers, Vector3f camera_pos) {
		Vector3f center = new Vector3f(0.0f,(float) 3*( (layers-1)/2),0.0f);
		Vector3f direction = new Vector3f(center.x- camera_pos.x, center.y - camera_pos.y, center.z - camera_pos.z);
		direction.normalize();
		return direction;
	}
	
	public void transformPoints(Vector3f center_pos) {
		
		ViewingMatrix camera = new ViewingMatrix(center_pos, getDirection(layers, center_pos));
		
		recoputeModel();
		
		for(int i = 0; i < list.size(); i++) {
			List<Vector4f> vectors = list.get(i);

			for(int j = 0; j < vectors.size(); j++){
				vectors.set(j, camera.mul(vectors.get(j)));
			}
			list.set(i,vectors);
		}
//		System.out.println(camera.matrix);
//		System.out.println();
	}

	public List<List<Vector4f>> getPoints() {
		return list;
	}
	
	public void RotateX(RotationXMatrix rotation) {
		for(int i = 0; i < list.size(); i++) {
			List<Vector4f> vectors = list.get(i);
			
			for(int j = 0; j < vectors.size(); j++){
				vectors.set(j, rotation.mul(vectors.get(j)));
			}
			list.set(i,vectors);
		}
	}
	
	public List<List<Vector4f>> project(MyMatrix projection) {
		List<List<Vector4f>> projections = new ArrayList<List<Vector4f>>();
		
		for(int j = 0; j < list.size(); j++){
			List<Vector4f> vec = new ArrayList<Vector4f>();
			List<Vector4f> tmpVec = list.get(j);
			for(int i = 0; i < tmpVec.size(); i++) {
				vec.add(projection.mul(tmpVec.get(i)));
			}
			projections.add(vec);
		}
		
		if(projection instanceof OrthographicProjection)
			return projections;
		else{
			for(List<Vector4f> lists :  projections)
				for(Vector4f v : lists) {
					v.x /= v.w; v.y /= v.w; v.z /= v.w;  v.w /= v.w; 
				}
			return projections;
		}
	}
}
