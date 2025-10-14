package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import thebetweenlands.common.registries.AttachmentRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

public class LastKilledData {

	public static final Codec<LastKilledData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("last_killed").forGetter(o -> o.lastKilled)
	).apply(instance, LastKilledData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, LastKilledData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.ENTITY_TYPE)), o -> o.lastKilled,
		LastKilledData::new
	);

	private Optional<EntityType<?>> lastKilled;

	public LastKilledData() {
		this(Optional.empty());
	}

	private LastKilledData(Optional<EntityType<?>> lastKilled) {
		this.lastKilled = lastKilled;
	}

	@Nullable
	public EntityType<?> getLastKilled() {
		return this.lastKilled.orElse(null);
	}

	public void setLastKilled(EntityType<?> entity) {
		this.lastKilled = Optional.of(entity);
	}

	public static void onLivingDeath(LivingDeathEvent event) {
		DamageSource source = event.getSource();
		Entity attacker = source.getEntity();
		if(attacker != null) {
			attacker.getData(AttachmentRegistry.LAST_KILLED).setLastKilled(event.getEntity().getType());
		}
	}
}
