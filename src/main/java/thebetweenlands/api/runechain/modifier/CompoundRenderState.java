package thebetweenlands.api.runechain.modifier;

import javax.annotation.Nullable;

/**
 * A fixed size compound render state that can be indexed by an integer.
 */
public class CompoundRenderState extends RenderState {
	private RenderState[] states;

	private CompoundRenderState(RenderState state, int num) {
		this.states = new RenderState[num];
		for(int i = 0; i < num; i++) {
			this.states[i] = RenderState.with(state);
		}
	}

	@Override
	protected void tick() {
		for(RenderState state : this.states) {
			state.update();
		}
	}

	/**
	 * Gets the <code>i</code>'th {@link RenderState} of a compound render state. If no compound render state exists yet a new
	 * one is created with <code>num</code> render states.
	 * @param state the render state to get the compound render state from
	 * @param num the size of the compound render state, i.e. how many render states it should contain
	 * @param i the index of the render state to retrieve
	 * @return
	 */
	@Nullable
	public static RenderState get(@Nullable RenderState state, int num, int i) {
		if(state != null) {
			return state.get(CompoundRenderState.class, () -> new CompoundRenderState(state, num)).states[i];
		}
		return state;
	}
}