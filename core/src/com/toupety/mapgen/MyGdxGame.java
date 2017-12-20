package com.toupety.mapgen;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.toupety.mapgen.algorithms.BoundedRoomDimmensionsAlgorithm;
import com.toupety.mapgen.algorithms.RoomDimmenstionsAlgorithm;
import com.toupety.mapgen.drawner.DrawnerFactory;

public class MyGdxGame extends ApplicationAdapter {
	
	private SpriteBatch batch;
//	private Texture img;
	private Level level = new Level(Constants.WIDTH, Constants.HEIGHT);
	private CameraHolder camera;
	private ShapeRenderer testRenderer;
	private ExecutorService executor;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
		camera = CameraHolder.instance();
//		camera.getOrtho().zoom = 100;
//		camera.getOrtho().update();
		
		this.executor = Executors.newSingleThreadExecutor();
		
		this.testRenderer = new ShapeRenderer();
		
		Gdx.graphics.setWindowedMode(1024, 648);
	}

	private Map<String, Object> defaultArgs() {
		/**
		 * Constants.MAX_ROOM_WIDTH, 
						  Constants.MAX_ROOM_HEIGHT, 
						  Constants.MIN_ROOM_WIDTH, 
						  Constants.MIN_ROOM_WIDTH,
						  20, 
						  1000
		 */
		Map<String, Object> map = new HashMap<>();
		map.put(BoundedRoomDimmensionsAlgorithm.MAX_ROOM_WIDTH, Constants.MAX_ROOM_WIDTH);
		map.put(BoundedRoomDimmensionsAlgorithm.MAX_ROOM_HEIGHT, Constants.MAX_ROOM_HEIGHT);
		map.put(BoundedRoomDimmensionsAlgorithm.MIN_ROOM_WIDTH, Constants.MIN_ROOM_WIDTH);
		map.put(BoundedRoomDimmensionsAlgorithm.MIN_ROOM_HEIGHT, Constants.MIN_ROOM_HEIGHT);
		map.put(BoundedRoomDimmensionsAlgorithm.MAX_ROOMS, 50);
		map.put(BoundedRoomDimmensionsAlgorithm.MAX_ITERATIONS, 1000);
		
		return map;
	}

	@Override
	public void render () {
		handleInput();
		camera.update();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
		
//		this.testRenderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
//		this.testRenderer.begin(ShapeType.Filled);
//		this.testRenderer.rect(0, 0, 64, 64);
//		this.testRenderer.end();
		
		DrawnerFactory
		.instance()
		.getElementDrawner(Level.class)
		.ifPresent(d -> d.draw(level));
	}
	
	private void handleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.getOrtho().zoom += 4;
			System.out.println(camera.getOrtho().zoom);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			camera.getOrtho().zoom -= 4;
			System.out.println(camera.getOrtho().zoom);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
			camera.getOrtho().zoom -= 400;
			System.out.println(camera.getOrtho().zoom);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.X)) {
			camera.getOrtho().zoom += 200;
			System.out.println(camera.getOrtho().zoom);
		}		
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			camera.getOrtho().translate(-Constants.DESLOC_SPEED, 0, 0);
			System.out.println(camera.getOrtho().position.x);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			camera.getOrtho().translate(Constants.DESLOC_SPEED, 0, 0);
			System.out.println(camera.getOrtho().position.x);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			camera.getOrtho().translate(0, -Constants.DESLOC_SPEED, 0);
			System.out.println(camera.getOrtho().position.y);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			camera.getOrtho().translate(0, Constants.DESLOC_SPEED, 0);
			System.out.println(camera.getOrtho().position.y);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.R)) {
			executor.execute(() -> {
				RoomDimmenstionsAlgorithm algo = new BoundedRoomDimmensionsAlgorithm(defaultArgs());
				level = new Level(Constants.WIDTH, Constants.HEIGHT);
				new RoomGenerator(algo).generate(level);
			});
		}

//		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//			cam.rotate(-rotationSpeed, 0, 0, 1);
//		}
//		if (Gdx.input.isKeyPressed(Input.Keys.E)) {
//			cam.rotate(rotationSpeed, 0, 0, 1);
//		}

//		camera.getOrtho().zoom = MathUtils.clamp(camera.getOrtho().zoom, 0.1f, 100/camera.getOrtho().viewportWidth);

		float effectiveViewportWidth = camera.getOrtho().viewportWidth * camera.getOrtho().zoom;
		float effectiveViewportHeight = camera.getOrtho().viewportHeight * camera.getOrtho().zoom;

		camera.getOrtho().position.x = MathUtils.clamp(camera.getOrtho().position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
		camera.getOrtho().position.y = MathUtils.clamp(camera.getOrtho().position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
	}

	@Override
	public void resize(int width, int height) {
//		camera.getOrtho().viewportWidth = 30f;
//		camera.getOrtho().viewportHeight = 30f * height/width;
//		camera.getOrtho().update();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
//		img.dispose();
	}
}
