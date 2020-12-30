package thebetweenlands.api.runechain.modifier;

import java.util.function.Supplier;

import javax.annotation.Nullable;

/**
 * An arbitrary render state that can be used by {@link RuneEffectModifier}s to store rendering state.
 */
public class RenderState {
	/**
	 * Creates a new and empty render state
	 * @return
	 */
	public static RenderState none() {
		return new RenderState();
	}

	/**
	 * Creates a new render state that includes the specified render state. The inherited render state, <code>other</code>,
	 * will not be ticked by the new render state.
	 * @param other the render state to be inherited, i.e. all properties of <code>other</code> will be inherited unless they're overwritten
	 * @return a new render state
	 */
	public static RenderState with(RenderState other) {
		RenderState state = new RenderState(other);
		state.next = other;
		return state;
	}

	private RenderState next;
	private RenderState unticked;

	private RenderState(RenderState unticked) {
		this.unticked = unticked;
	}

	protected RenderState() {

	}

	/**
	 * Updates/ticks the render state
	 */
	public final void update() {
		RenderState state = this;
		while(state != null) {
			if(state == this.unticked) {
				break;
			}
			state.tick();
			state = state.next;
		}
	}

	protected void tick() {

	}

	/**
	 * Returns the specified render state. May be null, but only if state does not yet exist and the factory returns null.
	 * @param cls State type
	 * @param factory State factory, may return null
	 * @return
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends RenderState> T get(Class<T> cls, Supplier<T> factory) {
		RenderState state = this;
		while(state != null) {
			if(state.getClass() == cls) {
				return (T) state;
			}
			state = state.next;
		}
		state = factory.get();
		if(state != null) {
			state.next = this.next;
			this.next = state;
			return (T) state;
		}
		return null;
	}
}