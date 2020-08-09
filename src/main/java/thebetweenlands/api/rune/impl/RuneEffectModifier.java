package thebetweenlands.api.rune.impl;

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
	 * Returns a color modifier to be applied to the effects of this modifier's subject.
	 * @return
	 */
	public int getColorModifier() {
		return 0xFFFFFFFF;
	}

	/**
	 * Returns a color modified to be applied to the effects of this modifier's subject.
	 * This method allows returning multiple different colors that may be used by multi colored effects.
	 * @param index index of the color modifier. May be any arbitrary number, but usually starts at 0.
	 * @return
	 */
	public int getColorModifier(int index) {
		return this.getColorModifier();
	}

	/**
	 * Returns whether this rune effect modifier has a color modifier.
	 * @return
	 */
	public boolean hasColorModifier() {
		return false;
	}
}
