package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.network.UpdateDecayDataPacket;
import thebetweenlands.common.registries.DimensionRegistries;

public class DecayData {

	private int decayLevel;
	private int prevDecayLevel;
	private float decaySaturationLevel;
	private float decayAccelerationLevel;
	private int removedHealth = 0;

	public static final Codec<DecayData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("decay_level").forGetter(o -> o.decayLevel),
		Codec.INT.fieldOf("prev_decay_level").forGetter(o -> o.prevDecayLevel),
		Codec.FLOAT.fieldOf("decay_saturation_level").forGetter(o -> o.decaySaturationLevel),
		Codec.FLOAT.fieldOf("decay_acceleration_level").forGetter(o -> o.decayAccelerationLevel)
	).apply(instance, DecayData::new));

	public DecayData() {
		this(0, 0, 5.0F, 0.0F);
	}

	public DecayData(int level, int prevLevel, float saturationLevel, float accelerationLevel) {
		this.decayLevel = level;
		this.prevDecayLevel = prevLevel;
		this.decaySaturationLevel = saturationLevel;
		this.decayAccelerationLevel = accelerationLevel;
	}

	/**
	 * Adds or removes decay.
	 * Negative decay increases saturation and decreases the decay level while positive decay decreases the saturation and increases the decay level.
	 * @param decay Decay to be added
	 * @param saturationModifier Saturation
	 */
	public void addStats(Player player, int decay, float saturationModifier) {
		this.decayLevel = Mth.clamp(this.decayLevel + decay, 0, 20);
		this.decaySaturationLevel = Mth.clamp(this.decaySaturationLevel + (float)-decay * saturationModifier * 2.0F, 0.0F, (float)(20 - this.decayLevel) / 4.0F);
		this.setChanged(player);
	}

	/**
	 * Updates the decay stats
	 * @param player
	 */
	public void tick(Player player) {
		this.prevDecayLevel = this.getDecayLevel(player);

		if(this.isDecayEnabled(player)) {
			if (this.decayAccelerationLevel > 4.0F) {
				this.decayAccelerationLevel -= 4.0F;

				if (this.decaySaturationLevel > 0.0F) {
					this.decaySaturationLevel = Math.max(this.decaySaturationLevel - 1.0F, 0.0F);
				} else {
					this.decaySaturationLevel = 0.0F;
					this.decayLevel = Math.min(this.decayLevel + 1, 20);
					this.setChanged(player);
				}
			}
		}
	}

	/**
	 * Returns the decay level (0 = no decay, 20 = maximum decay)
	 * @return
	 */
	public int getDecayLevel(Player player) {
		return this.isDecayEnabled(player) ? this.decayLevel : 0;
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
	public void setDecayLevel(Player player, int decay) {
		this.decayLevel = decay;
		this.setChanged(player);
	}

	/**
	 * Sets the decay saturation level (higher = slower decay rate)
	 * @param saturation
	 */
	public void setDecaySaturationLevel(float saturation) {
		this.decaySaturationLevel = saturation;
	}

	private void setChanged(Player player) {
		if (player instanceof ServerPlayer) {
			PacketDistributor.sendToPlayer((ServerPlayer) player, new UpdateDecayDataPacket(this.decayLevel, this.prevDecayLevel, this.decaySaturationLevel, this.decayAccelerationLevel));
		}
	}

	public boolean isDecayEnabled(Player player) {
		return player.level().getDifficulty() != Difficulty.PEACEFUL &&
			player.level().getGameRules().getBoolean(TheBetweenlands.DECAY_GAMERULE) && BetweenlandsConfig.useDecay &&
			(player.level().dimension() == DimensionRegistries.DIMENSION_KEY || BetweenlandsConfig.decayDimensionList.contains(player.level().dimension())) &&
			!player.isCreative() && !player.getAbilities().invulnerable;
	}

	public int getRemovedHealth() {
		return this.removedHealth;
	}

	public void setRemovedHealth(int removedHealth) {
		this.removedHealth = removedHealth;
	}

	public float getMaxPlayerHealth(int decayLevel) {
		return Math.min(20.0F + BetweenlandsConfig.decayMinHealth - decayLevel, 20.0F);
	}

	public float getMaxPlayerHealthPercentage(int decayLevel) {
		return BetweenlandsConfig.decayMinHealthPercentage + (1.0F - BetweenlandsConfig.decayMinHealthPercentage) * (Math.min(26.0F - decayLevel, 20.0F) - 6.0F) / (20.0F - 6.0F);
	}
}
