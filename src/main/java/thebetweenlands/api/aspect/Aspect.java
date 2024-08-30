package thebetweenlands.api.aspect;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import thebetweenlands.api.aspect.registry.AspectType;

import javax.annotation.Nullable;

public record Aspect(Holder<AspectType> type, int amount) implements Comparable<Aspect> {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(this.amount);
		result = prime * result + this.type.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Aspect other = (Aspect) obj;
		if (Float.floatToIntBits(this.amount) != Float.floatToIntBits(other.amount))
			return false;
		return this.type.equals(other.type);
	}

	@Override
	public int compareTo(Aspect other) {
		return this.type.getRegisteredName().compareTo(other.type.getRegisteredName());
	}
}
