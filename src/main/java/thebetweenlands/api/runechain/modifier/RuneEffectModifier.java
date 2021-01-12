package thebetweenlands.api.runechain.modifier;

import javax.annotation.Nullable;

import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.AbstractRune.Blueprint;

/**
 * Rune effect modifiers are effects or modifications of a rune to be applied to the effect of another rune before the former rune is even run.
 * For example, this can be used to change the appearance of projectiles of a projectile rune, such as giving them fire particles when a projectile rune is followed by a fire rune.
 */
public class RuneEffectModifier {
	protected AbstractRune<?> rune;

	protected IRuneChainUser user;

	@Nullable
	protected Subject subject;

	/**
	 * Called when the rune that this rune effect modifier applies to is activated ({@link AbstractRune.Blueprint#activate(AbstractRune, thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext, thebetweenlands.api.rune.INodeBlueprint.INodeIO)}).
	 * This may be called multiple times for different subjects.
	 * @param rune rune that this rune effect modifier applies to
	 * @param user user that activated the rune chain
	 * @param subject subject this rune effect modifier applies to
	 * @return whether the activation was successful and the rune effect modifier should by synced to clients
	 */
	public boolean activate(AbstractRune<?> rune, IRuneChainUser user, @Nullable Subject subject) {
		this.rune = rune;
		this.user = user;
		this.subject = subject;
		return true;
	}

	/**
	 * Called once every rune chain update, usually per tick, after the first activation until the rune chain is terminated.
	 */
	public void update() {

	}

	/**
	 * Called when the rune/rune chain that this rune effect modified applies to is terminated ({@link AbstractRune.Blueprint#terminate(AbstractRune, thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext)}).
	 */
	public void terminate() {

	}

	/**
	 * Returns a color modified to be applied to the effects of this modifier's subject.
	 * This method allows returning multiple different colors that may be used by multi colored effects.
	 * @param subject subject this rune effect modifier applies to
	 * @param index index of the color modifier. May be any arbitrary number, but usually starts at 0.
	 * @return
	 */
	public int getColorModifier(@Nullable Subject subject, int index) {
		return 0xFFFFFFFF;
	}

	/**
	 * Returns how many color modifiers this rune effect modifier has.
	 * @param subject subject this rune effect modifier applies to
	 * @return
	 */
	public int getColorModifierCount(@Nullable Subject subject) {
		return 0;
	}

	/**
	 * Renders the effect modifier.
	 * This method allows returning multiple different colors that may be used by multi colored effects.
	 * Render state is expected to be the same as when rendering an entity, both before and after this method call.
	 * @param subject subject this rune effect modifier applies to
	 * @param index index of the renderer. May be any arbitrary number, but usually starts at 0.
	 * @param properties Properties that specify how the effect is supposed to be rendered
	 * @param state Rendering state. May be null if no state can be provided
	 * @param partialTicks Partial render ticks
	 */
	public void render(@Nullable Subject subject, int index, RenderProperties properties, @Nullable RenderState state, float partialTicks) {

	}

	/**
	 * Returns how many renderers this rune effect modifier has.
	 * @param subject subject this rune effect modifier applies to
	 * @return
	 */
	public int getRendererCount(@Nullable Subject subject) {
		return 0;
	}
}
