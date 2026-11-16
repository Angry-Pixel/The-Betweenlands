package thebetweenlands.common.component.entity;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import thebetweenlands.common.world.storage.ringofgathering.MinionData;

/**
 * Ring of gathering minion data
 * @see MinionData
 */
public record RingOfGatheringMinionData(UUID ownerUuid, UUID minionUuid, long minionGeneration) {
	
	public static final Codec<RingOfGatheringMinionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			UUIDUtil.CODEC.fieldOf("owner").forGetter(RingOfGatheringMinionData::ownerUuid),
			UUIDUtil.CODEC.fieldOf("uuid").forGetter(RingOfGatheringMinionData::minionUuid),
			Codec.LONG.fieldOf("generation").forGetter(RingOfGatheringMinionData::minionGeneration)
	).apply(instance, RingOfGatheringMinionData::new));
	
}
