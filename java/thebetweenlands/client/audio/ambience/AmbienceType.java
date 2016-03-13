package thebetweenlands.client.audio.ambience;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

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

	public AmbienceType setPlayer(EntityPlayer player) {
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
	public abstract ResourceLocation getSound();

	public float getVolume() {
		return 1.0F;
	}

	public float getPitch() {
		return 1.0F;
	}

	public boolean stopsMusic() {
		return false;
	}

	public int getDelay() {
		return 0;
	}
}
