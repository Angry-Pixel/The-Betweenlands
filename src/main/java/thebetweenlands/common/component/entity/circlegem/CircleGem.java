package thebetweenlands.common.component.entity.circlegem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import thebetweenlands.common.component.entity.CircleGemData;

import java.util.Locale;
import java.util.function.IntFunction;

public record CircleGem(CircleGemType gemType, CircleGem.CombatType combatType) {

	public static final Codec<CircleGem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		CircleGemType.CODEC.fieldOf("gem_type").forGetter(CircleGem::gemType),
		CombatType.CODEC.fieldOf("combat_type").forGetter(CircleGem::combatType)
	).apply(instance, CircleGem::new));

	public static final StreamCodec<FriendlyByteBuf, CircleGem> STREAM_CODEC = StreamCodec.composite(
		CircleGemType.STREAM_CODEC, o -> o.gemType,
		CombatType.STREAM_CODEC, o -> o.combatType,
		CircleGem::new
	);

	public enum CombatType implements StringRepresentable {
		OFFENSIVE, DEFENSIVE, BOTH;

		public static final StringRepresentable.EnumCodec<CombatType> CODEC = StringRepresentable.fromEnum(CombatType::values);
		public static final IntFunction<CombatType> BY_ID = ByIdMap.continuous(CombatType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		public static final StreamCodec<ByteBuf, CombatType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, CombatType::ordinal);

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

	/**
	 * Returns whether the specified combat type matches
	 *
	 * @param type
	 * @return
	 */
	public boolean matchCombatType(CombatType type) {
		return this.combatType == CombatType.BOTH || type == this.combatType;
	}
}