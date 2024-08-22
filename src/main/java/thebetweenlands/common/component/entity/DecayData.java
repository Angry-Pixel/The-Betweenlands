package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.api.capability.IDecayData;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.datagen.BetweenlandsDimensionTypeTagProvider;
import thebetweenlands.common.network.clientbound.UpdateDecayDataPacket;
import thebetweenlands.common.registries.DimensionRegistries;

public class DecayData implements IDecayData {

	private int decayLevel;
	private int prevDecayLevel;
	private float decaySaturationLevel;
	private float decayAccelerationLevel;

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
	 * Negative decay increases "decay saturation" (a buffer before you lose decay levels) and decreases the decay level, while positive decay decreases decay saturation and increases the decay level.
	 * @param decay Decay to be added
	 * @param decaySaturation Saturation
	 */
	@Override
	public void addStats(Player player, int decay, float decaySaturation) {
		this.decayLevel = Mth.clamp(this.decayLevel + decay, 0, 20);
		this.decaySaturationLevel = Mth.clamp(this.decaySaturationLevel + (float)-decay * decaySaturation * 2.0F, 0.0F, (float)(20 - this.decayLevel) / 4.0F);
		this.setChanged(player);
	}

	/**
	 * Updates the decay stats
	 * @param player
	 */
	@Override
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
	@Override
	public int getDecayLevel(Player player) {
		return this.isDecayEnabled(player) ? this.decayLevel : 0;
	}

	/**
	 * Returns the decay level in the previous tick
	 * @return
	 */
	@Override
	public int getPrevDecayLevel() {
		return this.prevDecayLevel;
	}

	/**
	 * Returns the decay saturation level (higher = slower decay rate)
	 * @return
	 */
	@Override
	public float getSaturationLevel() {
		return this.decaySaturationLevel;
	}

	/**
	 * Returns the decay acceleration level
	 * @return
	 */
	@Override
	public float getAccelerationLevel() {
		return this.decayAccelerationLevel;
	}

	/**
	 * Sets the decay level
	 * @param decay
	 */
	@Override
	public void setDecayLevel(Player player, int decay) {
		this.decayLevel = decay;
		this.setChanged(player);
	}

	/**
	 * Sets the decay saturation level (higher = slower decay rate)
	 * @param saturation
	 */
	@Override
	public void setDecaySaturationLevel(Player player, float saturation) {
		this.decaySaturationLevel = saturation;
		this.setChanged(player);
	}

	/**
	 * Adds decay acceleration (once >= 4 is accumulated the decay level is increased by one)
	 * @param acceleration
	 */
	@Override
	public void addDecayAcceleration(Player player, float acceleration) {
		this.decayAccelerationLevel = Math.min(this.decayAccelerationLevel + acceleration, 40.0F);
		this.setChanged(player);
	}

	private void setChanged(Player player) {
		if (player instanceof ServerPlayer) {
			PacketDistributor.sendToPlayer((ServerPlayer) player, new UpdateDecayDataPacket(this.decayLevel, this.prevDecayLevel, this.decaySaturationLevel, this.decayAccelerationLevel));
		}
	}

	@Override
	public boolean isDecayEnabled(Player player) {
		return player.level().getDifficulty() != Difficulty.PEACEFUL &&
			player.level().getGameRules().getBoolean(TheBetweenlands.DECAY_GAMERULE) && BetweenlandsConfig.useDecay &&
			(player.level().dimension() == DimensionRegistries.DIMENSION_KEY || BetweenlandsConfig.decayDimensionList.contains(player.level().dimension()) || player.level().dimensionTypeRegistration().is(BetweenlandsDimensionTypeTagProvider.DECAYING_AURA)) &&
			!player.isCreative() && !player.getAbilities().invulnerable;
	}

	@Override
	public float getPlayerMaxHealthPenalty(Player player, int decayLevel) {
		if(BetweenlandsConfig.decayPercentual) return getPlayerMaxHealthPenaltyPercentage(player, decayLevel) * player.getMaxHealth();
		return Math.max((float)decayLevel - BetweenlandsConfig.decayMinHealth, 0.0f);
	}
	
	@Override
	public float getPlayerMaxHealthPenaltyPercentage(Player player, int decayLevel) {
		if(!BetweenlandsConfig.decayPercentual) return getPlayerMaxHealthPenalty(player, decayLevel) / player.getMaxHealth();

		return Math.max(((float)decayLevel / 20.0f - BetweenlandsConfig.decayMinHealthPercentage), 0);
		
	}
}
