package thebetweenlands.api.aspect;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import net.minecraft.nbt.CompoundTag;

public final class Aspect implements Comparable<Aspect> {
	public static final DecimalFormat ASPECT_AMOUNT_FORMAT = new DecimalFormat("#.##");

	static {
		ASPECT_AMOUNT_FORMAT.setRoundingMode(RoundingMode.CEILING);
	}

	/**
	 * The type of this aspect
	 */
	public final IAspectType type;

	/**
	 * The amount of this aspect
	 */
	public final int amount;

	public Aspect(IAspectType aspect, int amount) {
		if(aspect == null) throw new RuntimeException("Aspect can't be null");
		this.type = aspect;
		this.amount = amount;
	}

	public float getDisplayAmount() {
		return this.amount / 1000.0F;
	}

	public String getRoundedDisplayAmount() {
		return ASPECT_AMOUNT_FORMAT.format(this.getDisplayAmount());
	}

	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt.putString("aspect", this.type.getName());
		nbt.putInt("amount", this.amount);
		return nbt;
	}

	//TODO reintroduce once registry returns
	public static Aspect readFromNBT(CompoundTag nbt) {
		String aspectName = nbt.getString("aspect");
		int amount = nbt.getInt("amount");
//		IAspectType aspectType = AspectRegistry.getAspectTypeFromName(aspectName);
//		if(aspectType != null) {
//			return new Aspect(aspectType, amount);
//		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(this.amount);
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
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
		if (this.type == null) {
			if (other.type != null)
				return false;
		} else if (!this.type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public int compareTo(Aspect other) {
		return this.type.getName().compareTo(other.type.getName());
	}
}
