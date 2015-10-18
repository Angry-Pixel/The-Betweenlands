package thebetweenlands.entities.rowboat;

import static thebetweenlands.entities.rowboat.EntityWeedwoodRowboat.*;
import net.minecraft.util.MathHelper;
import thebetweenlands.utils.MathUtils;

public class Oar {
	private float prevStrokeProgress;
	private float strokeProgress;

	private float prevForce;
	private float force;

	private int prevSquareTick;
	private int squareTick;

	private boolean stroke;
	private boolean square;

	private float prevYaw;
	private float yaw;

	private float prevPitch;
	private float pitch;

	protected void updateControls(boolean stroke, boolean square) {
		this.stroke = stroke;
		this.square = square;
	}

	/*protected void update() {
		prevStrokeProgress = strokeProgress;
		prevForce = force;
		prevSquareTick = squareTick;
		if (stroke) {
			float resistance = getDisplacement() - force;
			if (resistance < 0) {
				 resistance = 0;
			}
			float progressAmount = (STROKE_MAX_SPEED - resistance) * OAR_ACCELERATION;
			if (progressAmount < STROKE_MIN_SPEED) {
				progressAmount = STROKE_MIN_SPEED;
			}
			if (square && prevStrokeProgress < STROKE_SQUARE_POSITION && strokeProgress >= STROKE_SQUARE_POSITION) {
				force -= getDisplacement() * (force + DRAG);
				if (squareTick < STROKE_SQUARE_TIME) {
					squareTick++;
				}
			} else {
				progress(progressAmount);
				float displacement = getDisplacement();
				force += (OAR_MAX_SPEED - force) * displacement;
			}
		} else {
			float resistance = getDisplacement() * force;
			progress(resistance);
			force -= resistance;
		}
		if (!square) {
			if (squareTick > 0) {
				squareTick--;
			}
		}
	}*/

	private void progress(float progress) {
		strokeProgress += progress;
		if (strokeProgress > MathUtils.TAU) {
			strokeProgress -= MathUtils.TAU;
		}
	}

	protected void updateAngles() {
		prevYaw = yaw;
		prevPitch = pitch;
	}

	/*private float getDisplacement() {
		float resistance = 0;
		if (strokeProgress <= STROKE_DRIVE) {
			resistance = (1 - (float) Math.pow(Math.abs(strokeProgress / (MathUtils.PI * STROKE_RATIO) - 1), STROKE_DRIVE_STEEPNESS)) * BLADE_RESISTANCE;
		}
		return resistance;
	}*/

	protected float getForce() {
		return force;
	}
}
