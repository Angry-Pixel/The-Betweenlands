package thebetweenlands.decay;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;

public class DecayStats {
	private int decayLevel = 20;
	private float decaySaturationLevel = 5.0F;
	private float decayExhaustionLevel;
	private int prevDecayLevel = 20;

	public void addStats(int decay, float saturationModifier) {
		this.decayLevel = Math.min(decay + this.decayLevel, 20);
		this.decaySaturationLevel = Math.min(this.decaySaturationLevel + (float)decay * saturationModifier * 2.0F, (float)this.decayLevel / 4.0F);
	}

	public void onUpdate(EntityPlayer player) {
		EnumDifficulty difficulty = player.worldObj.difficultySetting;

		this.prevDecayLevel = this.decayLevel;

		if (this.decayExhaustionLevel > 4.0F) {
			this.decayExhaustionLevel -= 4.0F;

			if (this.decaySaturationLevel > 0.0F) {
				this.decaySaturationLevel = Math.max(this.decaySaturationLevel - 1.0F, 0.0F);
			} else if (difficulty != EnumDifficulty.PEACEFUL) {
				this.decayLevel = Math.max(this.decayLevel - 1, 0);
			}
		}

		if(player.isInWater()) {
			this.addExhaustion(0.0033F);
		} else {
			this.addExhaustion(0.0018F);
		}
	}

	public void readNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("decayLevel", 99)) {
			this.decayLevel = nbt.getInteger("decayLevel");
			this.decaySaturationLevel = nbt.getFloat("decaySaturationLevel");
			this.decayExhaustionLevel = nbt.getFloat("decayExhaustionLevel");
		}
	}

	public void writeNBT(NBTTagCompound nbt) {
		nbt.setInteger("decayLevel", this.decayLevel);
		nbt.setFloat("decaySaturationLevel", this.decaySaturationLevel);
		nbt.setFloat("decayExhaustionLevel", this.decayExhaustionLevel);
	}

	public int getDecayLevel() {
		return this.decayLevel;
	}

	public int getPrevDecayLevel() {
		return this.prevDecayLevel;
	}

	public void addExhaustion(float decayExhaustion) {
		this.decayExhaustionLevel = Math.min(this.decayExhaustionLevel + decayExhaustion, 40.0F);
	}

	public float getSaturationLevel() {
		return this.decaySaturationLevel;
	}

	public float getExhaustionLevel() {
		return this.decayExhaustionLevel;
	}

	public void setDecayLevel(int decay) {
		this.decayLevel = decay;
	}

	public void setDecaySaturationLevel(float saturation) {
		this.decaySaturationLevel = saturation;
	}
}