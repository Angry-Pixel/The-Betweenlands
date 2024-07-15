package thebetweenlands.api.aspect;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.api.BLRegistries;

public interface AspectType {
	/**
	 * Returns the name of this aspect
	 * @return
	 */
	String getName();

	/**
	 * Returns the type of this aspect
	 * @return
	 */
	String getType();

	/**
	 * Returns the description of this aspect
	 * @return
	 */
	String getDescription();

	/**
	 * Returns the aspect icon
	 * @return
	 */
	ResourceLocation getIcon();

	/**
	 * Returns the color of the aspect
	 * @return
	 */
	int getColor();

	/**
	 * Writes this aspect type to the specified NBT
	 * @param nbt
	 * @return
	 */
	default CompoundTag writeToNBT(CompoundTag nbt) {
		nbt.putString("type", this.getName());
		return nbt;
	}

	/**
	 * Reads the aspect type from the specified NBT
	 * @param nbt
	 * @return
	 */
	@Nullable
	static AspectType readFromNBT(CompoundTag nbt) {
		return BLRegistries.ASPECTS.get(ResourceLocation.tryParse(nbt.getString("type")));
	}
}
