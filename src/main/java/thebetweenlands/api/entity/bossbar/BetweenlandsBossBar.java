package thebetweenlands.api.entity.bossbar;

import java.util.function.IntFunction;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.phys.Vec3;

public interface BetweenlandsBossBar {
	BetweenlandsServerBossBar getBar();

	default BossType getBossType() {
		return BossType.NORMAL_BOSS;
	}

	default float getMiniBossTagSize(float partialTicks) {
		return 0.5F;
	}

	default Vec3 getMiniBossTagOffset(float partialTicks) {
		return Vec3.ZERO;
	}

	enum BossType {
		NORMAL_BOSS,
		MINI_BOSS;

		public static final IntFunction<BossType> BY_ID = ByIdMap.continuous(BossType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		public static final StreamCodec<ByteBuf, BossType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BossType::ordinal);
	}
}
