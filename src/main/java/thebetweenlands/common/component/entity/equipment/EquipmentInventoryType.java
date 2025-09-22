package thebetweenlands.common.component.entity.equipment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.IntFunction;

public enum EquipmentInventoryType implements StringRepresentable {
	AMULET,
	RING,
	MISC;

	public static final StringRepresentable.EnumCodec<EquipmentInventoryType> CODEC = StringRepresentable.fromEnum(EquipmentInventoryType::values);
	public static final IntFunction<EquipmentInventoryType> BY_ID = ByIdMap.continuous(EquipmentInventoryType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
	public static final StreamCodec<ByteBuf, EquipmentInventoryType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, EquipmentInventoryType::ordinal);

	@Nullable
	public static EquipmentInventoryType fromID(int id) {
		for (EquipmentInventoryType inv : EquipmentInventoryType.values()) {
			if (inv.ordinal() == id) {
				return inv;
			}
		}
		return null;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}
