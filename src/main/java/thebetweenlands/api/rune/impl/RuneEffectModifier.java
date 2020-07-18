package thebetweenlands.api.rune.impl;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.rune.IRuneChainUser;

public class RuneEffectModifier {
	public static class Subject {
		@Nullable
		public final Vec3d position;

		@Nullable
		public final BlockPos block;

		@Nullable
		public final Entity entity;

		public Subject(@Nullable Vec3d position, @Nullable BlockPos block, @Nullable Entity entity) {
			this.position = position;
			this.block = block;
			this.entity = entity;
		}
	}

	protected IRuneChainUser user;

	@Nullable
	protected Subject subject;

	public void activate(IRuneChainUser user, @Nullable Subject subject) {
		this.user = user;
		this.subject = subject;
	}

	public void terminate() {

	}

	public int getColorModifier() {
		return 0xFFFFFFFF;
	}

	public int getColorModifier(int index) {
		return this.getColorModifier();
	}

	public boolean hasColorModifier() {
		return false;
	}
}
