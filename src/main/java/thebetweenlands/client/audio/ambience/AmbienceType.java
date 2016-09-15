package thebetweenlands.client.audio.ambience;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AmbienceType {
	public static enum EnumAmbienceLayer {
		LAYER1(1), LAYER2(1), OVERLAY(Integer.MAX_VALUE);

		public static final EnumAmbienceLayer TYPES[] = EnumAmbienceLayer.values();

		public final int tracks;
		private EnumAmbienceLayer(int tracks) {
			this.tracks = tracks;
		}
	}

	public abstract boolean isActive();

	private EntityPlayer player;

	public EntityPlayer getPlayer() {
		return this.player;
	}

	AmbienceType setPlayer(EntityPlayer player) {
		this.player = player;
		return this;
	}

	/**
	 * Defines the compatibility with other playing ambient tracks.
	 * LAYER1 and LAYER2 allows only one ambient track per group to be played at once.
	 * OVERLAY allows any amount of ambient tracks to play at once.
	 */
	public abstract EnumAmbienceLayer getAmbienceLayer();

	/**
	 * Defines the priority over other ambient tracks.
	 * Higher number means higher priority.
	 * @return
	 */
	public abstract int getPriority();

	/**
	 * Returns the ambient music track.
	 * Return null if you don't want to play a track (e.g. when you want to silence other ambient tracks).
	 * @return
	 */
	public abstract SoundEvent getSound();

	/**
	 * Returns the sound category
	 * @return
	 */
	public abstract SoundCategory getCategory();

	/**
	 * Returns the default volume of this ambience
	 * @return
	 */
	public float getVolume() {
		return 1.0F;
	}

	/**
	 * Returns the pitch of this ambience
	 * @return
	 */
	public float getPitch() {
		return 1.0F;
	}

	/**
	 * Returns whether this ambience stops music from playing
	 * @return
	 */
	public boolean stopsMusic() {
		return false;
	}

	/**
	 * Returns the start delay in ticks
	 * @return
	 */
	public int getDelay() {
		return 0;
	}

	/**
	 * Returns the fading time in ticks
	 * @return
	 */
	public int getFadeTime() {
		return 20;
	}

	/**
	 * Returns the volume that lower priority ambiences should be clamped to.
	 * Return <= 0 if other sounds should should stop playing completely.
	 * @return
	 */
	public float getLowerPriorityVolume() {
		return -1F;
	}
}
