package thebetweenlands.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class ItemBlockEnum<T extends Enum<T> & IStringSerializable> extends ItemCloth {

	public static <T extends Enum<T> & IStringSerializable> ItemBlockEnum<T> create(Block block, Class<T> cls) {
		return new ItemBlockEnum<T>(block, cls.getEnumConstants(), '.');
	}

	public static <T extends Enum<T> & IStringSerializable> ItemBlockEnum<T> create(Block block, Class<T> cls, char separator) {
		return new ItemBlockEnum<T>(block, cls.getEnumConstants(), separator);
	}

	private final T[] values;
	private final char separator;

	private ItemBlockEnum(Block block, T[] values, char separator) {
		super(block);
		this.values = values;
		this.separator = separator;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + separator + values[stack.getMetadata()].getName();
	}
}