package thebetweenlands.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.monster.PeatMummy;

public record BossPeatMummyPredicate(boolean bossMummy) implements EntitySubPredicate {

	public static final MapCodec<BossPeatMummyPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.fieldOf("is_boss_summoned").forGetter(BossPeatMummyPredicate::bossMummy))
		.apply(instance, BossPeatMummyPredicate::new));

	@Override
	public MapCodec<? extends EntitySubPredicate> codec() {
		return CODEC;
	}

	@Override
	public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
		if (entity instanceof PeatMummy mummy) {
			return mummy.isBossMummy() == this.bossMummy();
		}
		return false;
	}
}
