package thebetweenlands.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.entity.creature.frog.Frog;
import thebetweenlands.common.entity.creature.frog.FrogVariant;

import javax.annotation.Nullable;

public record FrogVariantPredicate(HolderSet<FrogVariant> variant) implements EntitySubPredicate {

	public static final MapCodec<FrogVariantPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			RegistryCodecs.homogeneousList(BLRegistries.Keys.FROG_VARIANT).fieldOf("variant").forGetter(FrogVariantPredicate::variant))
		.apply(instance, FrogVariantPredicate::new));

	@Override
	public MapCodec<? extends EntitySubPredicate> codec() {
		return CODEC;
	}

	@Override
	public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
		return entity instanceof Frog frog && this.variant().contains(frog.getVariant());
	}

	public static FrogVariantPredicate variant(Holder<FrogVariant> variant) {
		return new FrogVariantPredicate(HolderSet.direct(variant));
	}
}
