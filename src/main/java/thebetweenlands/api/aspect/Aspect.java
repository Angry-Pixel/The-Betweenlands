package thebetweenlands.api.aspect;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import javax.annotation.Nullable;

public record Aspect(Holder<AspectType> type, int amount) {
	public static final DecimalFormat ASPECT_AMOUNT_FORMAT = new DecimalFormat("#.##");

	static {
		ASPECT_AMOUNT_FORMAT.setRoundingMode(RoundingMode.CEILING);
	}

	public float getDisplayAmount() {
		return this.amount / 1000.0F;
	}

	public String getRoundedDisplayAmount() {
		return ASPECT_AMOUNT_FORMAT.format(this.getDisplayAmount());
	}

	public CompoundTag writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {
		AspectType.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.type()).ifSuccess(tag1 -> tag.put("aspect", tag1));
		tag.putInt("amount", this.amount);
		return tag;
	}

	@Nullable
	public static Aspect readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
		int amount = tag.getInt("amount");
		Holder<AspectType> aspectType = AspectType.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("aspect")).result().orElse(null);
		if (aspectType != null) {
			return new Aspect(aspectType, amount);
		}
		return null;
	}
}
