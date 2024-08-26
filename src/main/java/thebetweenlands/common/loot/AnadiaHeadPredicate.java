package thebetweenlands.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entities.fishing.anadia.Anadia;

public record AnadiaHeadPredicate(MinMaxBounds.Ints head) implements EntitySubPredicate {

	public static final MapCodec<AnadiaHeadPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			MinMaxBounds.Ints.CODEC.optionalFieldOf("head", MinMaxBounds.Ints.ANY).forGetter(AnadiaHeadPredicate::head))
		.apply(instance, AnadiaHeadPredicate::new));

	@Override
	public MapCodec<? extends EntitySubPredicate> codec() {
		return CODEC;
	}

	@Override
	public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
		return entity instanceof Anadia anadia && this.head.matches(anadia.getHeadType().ordinal());
	}

	public static AnadiaHeadPredicate head(MinMaxBounds.Ints head) {
		return new AnadiaHeadPredicate(head);
	}
}
