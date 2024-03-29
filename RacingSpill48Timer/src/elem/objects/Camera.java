package elem.objects;

import org.lwjgl.glfw.GLFW;

import engine.math.Vector3f;

public class Camera {

	private Vector3f position, rotation, moveState;
	private float movespeed = 0.2f, mouseSensitivity = 0.1f;
	private boolean fwd, bck, lft, rgt, up, dwn;
	private double lastMouseX, lastMouseY, newMouseX, newMouseY;

	public Camera() {
		this(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));
	}

	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
		moveState = new Vector3f(0, 0, 0);
	}

	public void update() {
		if(fwd || bck || lft || rgt) {
			double x = Math.sin(Math.toRadians(rotation.y())) * movespeed;
			double z = Math.cos(Math.toRadians(rotation.y())) * movespeed;
			
			//Forward and backwards
			Vector3f movement = new Vector3f(x * moveState.z(), 0, z * moveState.z());
			//Side to side
			movement = Vector3f.add(movement, new Vector3f(z * moveState.x(), 0, -x * moveState.x()));
			
			position = Vector3f.add(position, movement);
		}
		if(up || dwn) {
			position = Vector3f.addY(position, moveState.y() * movespeed);
		}
		
	}

	public void move(int keycode) {
		switch (keycode) {
		case GLFW.GLFW_KEY_W:
			if (!fwd) {
				moveState = Vector3f.addZ(moveState, -1);
				fwd = true;
			}
			break;
		case GLFW.GLFW_KEY_A:
			if (!lft) {
				moveState = Vector3f.addX(moveState, -1);
				lft = true;
			}
			break;
		case GLFW.GLFW_KEY_S:
			if (!bck) {
				moveState = Vector3f.addZ(moveState, 1);
				bck = true;
			}
			break;
		case GLFW.GLFW_KEY_D:
			if (!rgt) {
				moveState = Vector3f.addX(moveState, 1);
				rgt = true;
			}
			break;
		case GLFW.GLFW_KEY_SPACE:
			if (!up) {
				moveState = Vector3f.addY(moveState, 1);
				up = true;
			}
			break;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
			if (!dwn) {
				moveState = Vector3f.addY(moveState, -1);
				dwn = true;
			}
			break;
		}
	}

	public void moveHalt(int keycode) {
		switch (keycode) {
		case GLFW.GLFW_KEY_W:
			moveState = Vector3f.subZ(moveState, -1);
			fwd = false;
			break;
		case GLFW.GLFW_KEY_A:
			moveState = Vector3f.subX(moveState, -1);
			lft = false;
			break;
		case GLFW.GLFW_KEY_S:
			moveState = Vector3f.subZ(moveState, 1);
			bck = false;
			break;
		case GLFW.GLFW_KEY_D:
			moveState = Vector3f.subX(moveState, 1);
			rgt = false;
			break;
		case GLFW.GLFW_KEY_SPACE:
			moveState = Vector3f.subY(moveState, 1);
			up = false;
			break;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
			moveState = Vector3f.subY(moveState, -1);
			dwn = false;
			break;
		}
	}
	
	public void rotateCameraMouseBased(double x, double y) {
		newMouseX = x;
		newMouseY = y;
		
		float dx = (float) (newMouseX - lastMouseX) * mouseSensitivity;
		float dy = (float) (newMouseY - lastMouseY) * mouseSensitivity;

		lastMouseX = newMouseX;
		lastMouseY = newMouseY;
		
		rotation = Vector3f.add(rotation, new Vector3f(-dy, -dx, 0));
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

}
