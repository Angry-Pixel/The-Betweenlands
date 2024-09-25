package thebetweenlands.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import thebetweenlands.common.entity.fishing.anadia.Anadia;

public record AnadiaTailPredicate(MinMaxBounds.Ints tail) implements EntitySubPredicate {

	public static final MapCodec<AnadiaTailPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			MinMaxBounds.Ints.CODEC.optionalFieldOf("tail", MinMaxBounds.Ints.ANY).forGetter(AnadiaTailPredicate::tail))
		.apply(instance, AnadiaTailPredicate::new));

	@Override
	public MapCodec<? extends EntitySubPredicate> codec() {
		return CODEC;
	}

	@Override
	public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
		return entity instanceof Anadia anadia && this.tail.matches(anadia.getTailType().ordinal());
	}

	public static AnadiaTailPredicate tail(MinMaxBounds.Ints tail) {
		return new AnadiaTailPredicate(tail);
	}
}
