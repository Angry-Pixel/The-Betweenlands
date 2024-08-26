package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import thebetweenlands.common.registries.AttachmentRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

public class LastKilledData {

	public static final Codec<LastKilledData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.optionalFieldOf("location").forGetter(o -> o.lastKilled)
	).apply(instance, LastKilledData::new));

	private Optional<ResourceLocation> lastKilled;

	public LastKilledData() {
		this(Optional.empty());
	}

	public LastKilledData(Optional<ResourceLocation> lastKilled) {
		this.lastKilled = lastKilled;
	}

	@Nullable
	public ResourceLocation getLastKilled() {
		return this.lastKilled.orElse(null);
	}

	public void setLastKilled(ResourceLocation key) {
		this.lastKilled = Optional.of(key);
//		this.setChanged();
	}

	public static void onLivingDeath(LivingDeathEvent event) {
		DamageSource source = event.getSource();
		Entity attacker = source.getEntity();
		if(attacker != null) {
			attacker.getData(AttachmentRegistry.LAST_KILLED).setLastKilled(BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()));
		}
	}
}
