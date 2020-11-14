package thebetweenlands.api.rune.impl;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.rune.IRuneChainUser;

/**
 * Rune effect modifiers are effects or modifications of a rune to be applied to the effect of another rune before the former rune is even run.
 * For example, this can be used to change the appearance of projectiles of a projectile rune, such as giving them fire particles when a projectile rune is followed by a fire rune.
 */
public class RuneEffectModifier {
	/**
	 * A subject to be affected by rune effect modifiers. This subject can be various things, such as an entity, a block or even just a position.
	 */
	public static class Subject {
		private final Vec3d position;
		private final BlockPos block;
		private final Entity entity;

		public Subject(@Nullable Vec3d position, @Nullable BlockPos block, @Nullable Entity entity) {
			this.position = position;
			this.block = block;
			this.entity = entity;
		}

		public Subject(Vec3d position) {
			this(position, null, null);
		}

		public Subject(BlockPos block) {
			this(null, block, null);
		}

		public Subject(Entity entity) {
			this(null, null, entity);
		}

		/**
		 * Returns the position described by this subject
		 * @return
		 */
		@Nullable
		public Vec3d getPosition() {
			if(this.position == null && this.entity != null) {
				return new Vec3d(this.entity.posX, this.entity.posY + this.entity.height * 0.5f, this.entity.posZ);
			}
			return this.position;
		}

		/**
		 * Returns the block described by this subject
		 * @return
		 */
		@Nullable
		public BlockPos getBlock() {
			return this.block;
		}

		/**
		 * Returns the entity described by this subject
		 * @return
		 */
		@Nullable
		public Entity getEntity() {
			return this.entity;
		}

		/**
		 * Returns whether this subject is still active
		 * @return
		 */
		public boolean isActive() {
			return this.getPosition() != null || this.getBlock() != null || this.getEntity() != null;
		}
	}

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

	public static class RenderProperties {
		/**
		 * Red color component multiplier
		 */
		public float red = 1;

		/**
		 * Green color component multiplier
		 */
		public float green = 1;

		/**
		 * Blue color component multiplier
		 */
		public float blue = 1;

		/**
		 * Alpha color component multiplier
		 */
		public float alpha = 1;

		/**
		 * X size the renderer should fit into, [-sizeX, sizeX]
		 */
		public float sizeX = 0.5f;

		/**
		 * Y size the renderer should fit into, [-sizeY, sizeY]
		 */
		public float sizeY = 0.5f;

		/**
		 * Z size the renderer should fit into, [-sizeZ, sizeZ]
		 */
		public float sizeZ = 0.5f;

		/**
		 * Whether the renderer should be rendered at a fixed position and not move
		 */
		public boolean fixed = false;
	}

	public static class RenderState {
		/**
		 * Creates a new and empty render state
		 * @return
		 */
		public static RenderState none() {
			return new RenderState();
		}

		/**
		 * Creates a new render state that includes the specified render state. The specified render state
		 * will not be ticked by the new render state.
		 * @param other
		 * @return
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
