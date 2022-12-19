package thebetweenlands.common.item;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class ItemBlockEnum<T extends Enum<T> & IStringSerializable> extends ItemBlock {
	/**
	 * This can be implemented in a generic block enum if the generic enum requires a custom selector
	 */
	public static interface IGenericMetaSelector {
		boolean isMetadataMatching(int meta);
	}

	public static <T extends Enum<T> & IStringSerializable> ItemBlockEnum<T> create(Block block, Class<T> cls) {
		return new ItemBlockEnum<T>(block, cls.getEnumConstants(), '.', IGenericMetaSelector.class.isAssignableFrom(cls));
	}

	public static <T extends Enum<T> & IStringSerializable> ItemBlockEnum<T> create(Block block, Class<T> cls, char separator) {
		return new ItemBlockEnum<T>(block, cls.getEnumConstants(), separator, IGenericMetaSelector.class.isAssignableFrom(cls));
	}

	private final T[] values;
	private final boolean hasGenericMetaSelector;
	private final char separator;
	private final String[] unlocalizedNames;

	protected ItemBlockEnum(Block block, T[] values, char separator, boolean hasGenericMetaSelector) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.values = values;
		this.separator = separator;
		this.hasGenericMetaSelector = hasGenericMetaSelector;
		this.unlocalizedNames = new String[values.length];
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		T match = null;
		if (this.hasGenericMetaSelector)
			for (T type : this.values)
				if (((IGenericMetaSelector) type).isMetadataMatching(stack.getMetadata())) {
					match = type;
					break;
				}
		if (match == null) {
			if(stack.getMetadata() >= this.values.length) {
				return "item.thebetweenlands.enum.unknown";
			}
			match = this.values[stack.getMetadata()];
		}
		return this.getTranslationKey(match);
	}

	private String getTranslationKey(T value) {
		String name = this.unlocalizedNames[value.ordinal()];
		if (name == null) {
			name = super.getTranslationKey() + this.separator + value.getName().toLowerCase(Locale.ENGLISH);
			this.unlocalizedNames[value.ordinal()] = name;
		}
		return name;
	}
}