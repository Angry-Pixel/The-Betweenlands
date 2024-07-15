package thebetweenlands.common.component.entity.circlegem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public record CircleGem(CircleGemType gemType, CircleGem.CombatType combatType) {

	public static final Codec<CircleGem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		CircleGemType.CODEC.fieldOf("gem_type").forGetter(CircleGem::gemType),
		CombatType.CODEC.fieldOf("combat_type").forGetter(CircleGem::combatType)
	).apply(instance, CircleGem::new));

	public enum CombatType implements StringRepresentable {
		OFFENSIVE, DEFENSIVE, BOTH;

		public static final StringRepresentable.EnumCodec<CombatType> CODEC = StringRepresentable.fromEnum(CombatType::values);

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