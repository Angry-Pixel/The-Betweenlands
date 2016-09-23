package thebetweenlands.common.entity.capability;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;

public class DecayStats {
	private int decayLevel = 20;
	private float decaySaturationLevel = 5.0F;
	private float decayExhaustionLevel;
	private int prevDecayLevel = 20;
	private final DecayEntityCapability capability;

	public DecayStats(@Nullable DecayEntityCapability capability) {
		this.capability = capability;
	}

	/**
	 * Adds decay stats
	 * @param decay Decay to be added
	 * @param saturationModifier Saturation
	 */
	public void addStats(int decay, float saturationModifier) {
		this.decayLevel = Math.min(decay + this.decayLevel, 20);
		this.decaySaturationLevel = Math.min(this.decaySaturationLevel + (float)decay * saturationModifier * 2.0F, (float)this.decayLevel / 4.0F);
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

		if (this.decayExhaustionLevel > 4.0F) {
			this.decayExhaustionLevel -= 4.0F;

			if (this.decaySaturationLevel > 0.0F) {
				this.decaySaturationLevel = Math.max(this.decaySaturationLevel - 1.0F, 0.0F);
			} else if (difficulty != EnumDifficulty.PEACEFUL) {
				this.decayLevel = Math.max(this.decayLevel - 1, 0);
				if(this.capability != null)
					this.capability.markDirty();
			}
		}

		if(player.isInWater()) {
			this.addExhaustion(0.00038F);
		} else {
			this.addExhaustion(0.00024F);
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
			this.decayExhaustionLevel = nbt.getFloat("decayExhaustionLevel");
		}
	}

	/**
	 * Writes the decay stats to NBT
	 * @param nbt
	 */
	public void writeNBT(NBTTagCompound nbt) {
		nbt.setInteger("decayLevel", this.decayLevel);
		nbt.setFloat("decaySaturationLevel", this.decaySaturationLevel);
		nbt.setFloat("decayExhaustionLevel", this.decayExhaustionLevel);
	}

	/**
	 * Returns the decay level
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
	 * Adds decay exhaustion
	 * @param decayExhaustion
	 */
	public void addExhaustion(float decayExhaustion) {
		this.decayExhaustionLevel = Math.min(this.decayExhaustionLevel + decayExhaustion, 40.0F);
	}

	/**
	 * Returns the saturation level
	 * @return
	 */
	public float getSaturationLevel() {
		return this.decaySaturationLevel;
	}

	/**
	 * Returns the exhaustion level
	 * @return
	 */
	public float getExhaustionLevel() {
		return this.decayExhaustionLevel;
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
	 * Sets the decay saturation level
	 * @param saturation
	 */
	public void setDecaySaturationLevel(float saturation) {
		this.decaySaturationLevel = saturation;
	}
}