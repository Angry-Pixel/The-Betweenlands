package thebetweenlands.client.audio.ambience;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.DimensionRegistries;

public abstract class AmbienceType {

	public abstract boolean isActive();

	private Player player;

	public Player getPlayer() {
		return this.player;
	}

	AmbienceType setPlayer(Player player) {
		this.player = player;
		return this;
	}

	public boolean isActiveInWorld(Level level) {
		return level.dimension().equals(DimensionRegistries.DIMENSION_KEY);
	}

	public abstract AmbienceLayer getAmbienceLayer();

	public abstract int getPriority();

	public abstract SoundEvent getSound();

	public abstract SoundSource getCategory();

	public float getVolume() {
		return 1.0F;
	}

	/**
	 * Returns the pitch of this ambience
	 */
	public float getPitch() {
		return 1.0F;
	}

	public boolean stopsMusic() {
		return false;
	}

	public int getDelay() {
		return 0;
	}

	public int getFadeTime() {
		return 20;
	}

	public float getLowerPriorityVolume() {
		return -1.0F;
	}
}
