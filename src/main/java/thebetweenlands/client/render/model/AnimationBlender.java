package thebetweenlands.client.render.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AnimationBlender<T extends MowzieModelBase> {
	private class State {
		private final Consumer<T> animator;
		private final Supplier<Float> weight;

		private float currentWeight;

		private State(Consumer<T> animator, Supplier<Float> weight) {
			this.animator = animator;
			this.weight = weight;
		}

		private void update(AnimationBlender<T> blender, T model, boolean reset) {
			if(reset) {
				for(int i = 0; i < model.parts.length; i++) {
					MowzieModelRenderer part = model.parts[i];
					part.rotateAngleX = Float.NaN;
					part.rotateAngleY = Float.NaN;
					part.rotateAngleZ = Float.NaN;
				}
			}

			this.animator.accept(model);

			for(int i = 0; i < model.parts.length; i++) {
				MowzieModelRenderer part = model.parts[i];

				if(Float.isFinite(part.rotateAngleX)) {
					blender.rotX[i] += this.currentWeight * part.rotateAngleX;
				}

				if(Float.isFinite(part.rotateAngleY)) {
					blender.rotY[i] += this.currentWeight * part.rotateAngleY;
				}

				if(Float.isFinite(part.rotateAngleZ)) {
					blender.rotZ[i] += this.currentWeight * part.rotateAngleZ;
				}
			}
		}
	}

	private final T model;

	private final State baseState;
	private final List<State> states = new ArrayList<>();

	private final float[] rotX;
	private final float[] rotY;
	private final float[] rotZ;

	public AnimationBlender(T model) {
		this.model = model;
		this.states.add(this.baseState = new State(m -> {}, () -> 1.0f));
		this.rotX = new float[this.model.parts.length];
		this.rotY = new float[this.model.parts.length];
		this.rotZ = new float[this.model.parts.length];
	}

	/**
	 * @param animator The animator that set's the model part rotations for this state
	 * @param weight The state's weight
	 */
	public void addState(Consumer<T> animator, Supplier<Float> weight) {
		this.states.add(new State(animator, weight));
	}

	/**
	 * Set's the angles as a weighted combination of each state's rotations.
	 * If all state weights add up to 1 this is the same as an interpolation between all states.
	 * @param useExistingAngles Whether currently already existing rotations should be added. If false all rotations are initialised with 0.
	 */
	public void setAngles(boolean useExistingAngles) {
		for(int i = 0; i < this.model.parts.length; i++) {
			this.rotX[i] = this.rotY[i] = this.rotZ[i] = 0;
		}

		for(State state : this.states) {
			if(useExistingAngles || state != this.baseState) {
				state.currentWeight = state.weight.get();
				state.update(this, this.model, state != this.baseState);
			}
		}

		for(int i = 0; i < this.model.parts.length; i++) {
			MowzieModelRenderer part = model.parts[i];

			part.rotateAngleX = this.rotX[i];
			part.rotateAngleY = this.rotY[i];
			part.rotateAngleZ = this.rotZ[i];
		}
	}
}
