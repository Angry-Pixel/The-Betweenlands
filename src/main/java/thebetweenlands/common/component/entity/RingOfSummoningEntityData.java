package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import thebetweenlands.common.registries.AttachmentRegistry;

public class RingOfSummoningEntityData {

	private int cooldownTicks;
	private int activeTicks;
	private boolean active;

	public static final Codec<RingOfSummoningEntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("active").forGetter(o -> o.active),
			Codec.INT.fieldOf("activeTicks").forGetter(o -> o.activeTicks),
			Codec.INT.fieldOf("cooldownTicks").forGetter(o -> o.cooldownTicks)
	).apply(instance, RingOfSummoningEntityData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RingOfSummoningEntityData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, d -> d.active,
			ByteBufCodecs.INT, d -> d.activeTicks,
			ByteBufCodecs.INT, d -> d.cooldownTicks,
			RingOfSummoningEntityData::new
		);

	public RingOfSummoningEntityData() {
		this(false, 0, 0);
	}

	public RingOfSummoningEntityData(boolean active, int activeTicks, int cooldownTicks) {
		this.active = active;
		this.activeTicks = activeTicks;
		this.cooldownTicks = cooldownTicks;
	}
	
	public void setActive(LivingEntity entity, boolean active) {
		this.active = active;
		entity.syncData(AttachmentRegistry.RING_OF_SUMMONING_ENTITY_DATA);
	}

	/**
	 * Returns whether the entity is summoning
	 * @return
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * Returns the cooldown
	 * @return
	 */
	public int getCooldownTicks() {
		return this.cooldownTicks;
	}

	/**
	 * Sets the cooldown
	 * @param ticks
	 */
	public void setCooldownTicks(LivingEntity entity, int ticks) {
		this.cooldownTicks = ticks;
		entity.syncData(AttachmentRegistry.RING_OF_SUMMONING_ENTITY_DATA);
	}

	/**
	 * Returns how long the summoning is active
	 * @return
	 */
	public int getActiveTicks() {
		return this.activeTicks;
	}

	/**
	 * Sets how long the summoning is active
	 * @param ticks
	 */
	public void setActiveTicks(LivingEntity entity, int ticks) {
		this.activeTicks = ticks;
		entity.syncData(AttachmentRegistry.RING_OF_SUMMONING_ENTITY_DATA);
		
	}
}
