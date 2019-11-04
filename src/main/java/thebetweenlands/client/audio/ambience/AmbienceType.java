package thebetweenlands.client.audio.ambience;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.config.BetweenlandsConfig;

@SideOnly(Side.CLIENT)
public abstract class AmbienceType {
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
	 * Returns whether this ambience type is active in the specified world.
	 * @param world
	 * @return
	 */
	public boolean isActiveInWorld(World world) {
		return world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId;
	}

	/**
	 * Defines the compatibility and priorities with other playing ambient tracks in the same layer
	 */
	public abstract AmbienceLayer getAmbienceLayer();

	/**
	 * Defines the priority over other ambient tracks.
	 * Higher number means higher priority.
	 * @return
	 */
	public abstract int getPriority();

	/**
	 * Returns the ambient music track.
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
