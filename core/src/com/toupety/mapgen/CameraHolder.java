package com.toupety.mapgen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraHolder {

	private OrthographicCamera cam;
	private static CameraHolder inst;
	
	private CameraHolder() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		
		cam = new OrthographicCamera(30, 30 * (h / w));
		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
//		cam.setToOrtho(true);
//		cam.rotate(180,0,1,0);//TODO hard coded
		cam.update();
	}
	
	public static CameraHolder instance() {
		synchronized (CameraHolder.class) {
			
			if(inst == null) {
				inst = new CameraHolder();
			}
			
			return inst;
		}
	}
	
	public OrthographicCamera getOrtho() {
		return cam;
	}
	
	public void update() {
		cam.update();
	}
}
