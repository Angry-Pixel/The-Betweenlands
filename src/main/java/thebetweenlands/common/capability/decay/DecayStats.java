package thebetweenlands.common.capability.decay;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;

public class DecayStats {
	private int decayLevel = 0;
	private int prevDecayLevel = 0;
	private float decaySaturationLevel = 5.0F;
	private float decayAccelerationLevel;
	private final DecayEntityCapability capability;

	public DecayStats(@Nullable DecayEntityCapability capability) {
		this.capability = capability;
	}

	/**
	 * Adds or removes decay.
	 * Negative decay increases saturation and decreases the decay level while positive decay decreases the saturation and increases the decay level.
	 * @param decay Decay to be added
	 * @param saturationModifier Saturation
	 */
	public void addStats(int decay, float saturationModifier) {
		this.decayLevel = MathHelper.clamp_int(this.decayLevel + decay, 0, 20);
		this.decaySaturationLevel = MathHelper.clamp_float(this.decaySaturationLevel + (float)-decay * saturationModifier * 2.0F, 0.0F, (float)(20 - this.decayLevel) / 4.0F);
		if(this.capability != null)
			this.capability.markDirty();
	}

	/**
	 * Updates the decay stats
	 * @param player
	 */
	public void onUpdate(EntityPlayer player) {
		EnumDifficulty difficulty = player.worldObj.getDifficulty();

		this.prevDecayLevel = this.decayLevel;

		if (difficulty != EnumDifficulty.PEACEFUL) {
			if (this.decayAccelerationLevel > 4.0F) {
				this.decayAccelerationLevel -= 4.0F;
	
				if (this.decaySaturationLevel > 0.0F) {
					this.decaySaturationLevel = Math.max(this.decaySaturationLevel - 1.0F, 0.0F);
				} else {
					this.decaySaturationLevel = 0.0F;
					this.decayLevel = Math.min(this.decayLevel + 1, 20);
	
					if(this.capability != null) {
						this.capability.markDirty();
					}
				}
			}
		} else if(this.getDecayLevel() < 20){
			this.addStats(-1, 0);
		}
	}

	/**
	 * Reads the decay stats from NBT
	 * @param nbt
	 */
	public void readNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("decayLevel", 99)) {
			this.decayLevel = nbt.getInteger("decayLevel");
			this.decaySaturationLevel = nbt.getFloat("decaySaturationLevel");
			this.decayAccelerationLevel = nbt.getFloat("decayExhaustionLevel");
		}
	}

	/**
	 * Writes the decay stats to NBT
	 * @param nbt
	 */
	public void writeNBT(NBTTagCompound nbt) {
		nbt.setInteger("decayLevel", this.decayLevel);
		nbt.setFloat("decaySaturationLevel", this.decaySaturationLevel);
		nbt.setFloat("decayExhaustionLevel", this.decayAccelerationLevel);
	}

	/**
	 * Returns the decay level (0 = no decay, 20 = maximum decay)
	 * @return
	 */
	public int getDecayLevel() {
		return this.decayLevel;
	}

	/**
	 * Returns the decay level in the previous tick
	 * @return
	 */
	public int getPrevDecayLevel() {
		return this.prevDecayLevel;
	}

	/**
	 * Adds decay acceleration (once >= 4 is accumulated the decay level is increased by one)
	 * @param acceleration
	 */
	public void addDecayAcceleration(float acceleration) {
		this.decayAccelerationLevel = Math.min(this.decayAccelerationLevel + acceleration, 40.0F);
	}

	/**
	 * Returns the decay saturation level (higher = slower decay rate)
	 * @return
	 */
	public float getSaturationLevel() {
		return this.decaySaturationLevel;
	}

	/**
	 * Returns the decay acceleration level
	 * @return
	 */
	public float getAccelerationLevel() {
		return this.decayAccelerationLevel;
	}

	/**
	 * Sets the decay level
	 * @param decay
	 */
	public void setDecayLevel(int decay) {
		this.decayLevel = decay;
		if(this.capability != null)
			this.capability.markDirty();
	}

	/**
	 * Sets the decay saturation level (higher = slower decay rate)
	 * @param saturation
	 */
	public void setDecaySaturationLevel(float saturation) {
		this.decaySaturationLevel = saturation;
	}
}