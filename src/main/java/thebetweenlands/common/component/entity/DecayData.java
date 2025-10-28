package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.datagen.tags.BLDimensionTypeTagProvider;
import thebetweenlands.common.handler.PlayerDecayHandler;
import thebetweenlands.common.registries.DimensionRegistries;

public record DecayData(int decayLevel, int prevDecayLevel, float decaySaturationLevel, float decayAccelerationLevel) {

	public static final Codec<DecayData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("decay_level").forGetter(DecayData::decayLevel),
		Codec.INT.fieldOf("prev_decay_level").forGetter(DecayData::prevDecayLevel),
		Codec.FLOAT.fieldOf("decay_saturation_level").forGetter(DecayData::decaySaturationLevel),
		Codec.FLOAT.fieldOf("decay_acceleration_level").forGetter(DecayData::decayAccelerationLevel)
	).apply(instance, DecayData::new));

	public static final StreamCodec<FriendlyByteBuf, DecayData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, DecayData::decayLevel,
		ByteBufCodecs.INT, DecayData::prevDecayLevel,
		ByteBufCodecs.FLOAT, DecayData::decaySaturationLevel,
		ByteBufCodecs.FLOAT, DecayData::decayAccelerationLevel,
		DecayData::new
	);

	public DecayData() {
		this(0, 0, 1.0F, 0.0F);
	}

	/**
	 * Adds or removes decay.
	 * Negative decay increases "decay saturation" (a buffer before you lose decay levels) and decreases the decay level, while positive decay decreases decay saturation and increases the decay level.
	 *
	 * @param decay           Decay to be added
	 * @param decaySaturation Saturation
	 */
	public DecayData addStats(int decay, float decaySaturation) {
		return new DecayData(Mth.clamp(this.decayLevel() + decay, 0, 20), this.prevDecayLevel(), Mth.clamp(this.decaySaturationLevel() + (float) -decay * decaySaturation * 2.0F, 0.0F, (float) (20 - this.decayLevel()) / 4.0F), this.decayAccelerationLevel());
	}

	public DecayData update(Player player) {
		int prevDecayLevel = this.getDecayLevel(player);
		if (isDecayEnabled(player)) {
			int level = this.decayLevel();
			float saturation = this.decaySaturationLevel();
			float acceleration = this.decayAccelerationLevel();
			if (acceleration > 4.0F) {
				acceleration -= 4.0F;

				if (saturation > 0.0F) {
					saturation = Math.max(saturation - 1.0F, 0.0F);
				} else {
					saturation = 0.0F;
					level = Math.min(this.decayLevel() + 1, 20);
				}
			}
			return new DecayData(level, prevDecayLevel, saturation, acceleration);
		}
		//not enabled? No problem, set to default
		return new DecayData();
	}

	public DecayData addDecayAcceleration(float acceleration) {
		return new DecayData(this.decayLevel(), this.prevDecayLevel(), this.decaySaturationLevel(), Math.min(this.decayAccelerationLevel() + acceleration, 40.0F));
	}

	public int getDecayLevel(Player player) {
		return isDecayEnabled(player) ? this.decayLevel() : 0;
	}

	public static float getPlayerMaxHealthPenalty(Player player, int decayLevel) {
		if (BetweenlandsConfig.decayPercentual) return getPlayerMaxHealthPenaltyPercentage(player, decayLevel) * player.getMaxHealth();
		return Math.max((float) decayLevel - BetweenlandsConfig.decayMinHealth, 0.0f);
	}

	public static float getPlayerMaxHealthPenaltyPercentage(Player player, int decayLevel) {
		if (!BetweenlandsConfig.decayPercentual) return getPlayerMaxHealthPenalty(player, decayLevel) / player.getMaxHealth();

		return Math.max(((float) decayLevel / 20.0f - BetweenlandsConfig.decayMinHealthPercentage), 0);
	}

	public static boolean isDecayEnabled(Player player) {
		return player.level().getDifficulty() != Difficulty.PEACEFUL &&
			player.level().getGameRules().getBoolean(TheBetweenlands.DECAY_GAMERULE) && BetweenlandsConfig.useDecay &&
			(player.level().dimension() == DimensionRegistries.DIMENSION_KEY || BetweenlandsConfig.decayDimensionList.contains(player.level().dimension()) || player.level().dimensionTypeRegistration().is(BLDimensionTypeTagProvider.DECAYING_AURA)) &&
			!player.isCreative() && !player.getAbilities().invulnerable;
	}

	public static class DecaySyncing implements AttachmentSyncHandler<DecayData> {

		@Override
		public void write(RegistryFriendlyByteBuf buf, DecayData attachment, boolean initialSync) {
			DecayData.STREAM_CODEC.encode(buf, attachment);
		}

		@Override
		public @Nullable DecayData read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable DecayData previousValue) {
			if (holder instanceof Player player) {
				float initialMax = player.getMaxHealth();
				// Prevent the game from playing the take damage animation
				PlayerDecayHandler.applyDecayAttributeModifiers(player);
				float finalMax = player.getMaxHealth();
				if (player.getHealth() > player.getMaxHealth()) {
					player.setHealth(player.getMaxHealth());
				}
				int difference = Math.round(finalMax - initialMax);
				// Hack to make the game update the max health when decay changes
				Minecraft.getInstance().gui.displayHealth += difference;
			}
			return DecayData.STREAM_CODEC.decode(buf);
		}
	}
}
