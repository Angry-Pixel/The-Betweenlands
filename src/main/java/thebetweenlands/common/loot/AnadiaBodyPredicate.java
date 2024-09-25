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

public record AnadiaBodyPredicate(MinMaxBounds.Ints body) implements EntitySubPredicate {

	public static final MapCodec<AnadiaBodyPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			MinMaxBounds.Ints.CODEC.optionalFieldOf("body", MinMaxBounds.Ints.ANY).forGetter(AnadiaBodyPredicate::body))
		.apply(instance, AnadiaBodyPredicate::new));

	@Override
	public MapCodec<? extends EntitySubPredicate> codec() {
		return CODEC;
	}

	@Override
	public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
		return entity instanceof Anadia anadia && this.body.matches(anadia.getBodyType().ordinal());
	}

	public static AnadiaBodyPredicate body(MinMaxBounds.Ints body) {
		return new AnadiaBodyPredicate(body);
	}
}
