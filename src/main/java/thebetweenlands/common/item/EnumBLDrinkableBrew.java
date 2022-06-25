package thebetweenlands.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.ItemRegistry;

public enum EnumBLDrinkableBrew implements IStringSerializable
{
    NETTLE_SOUP(0, "nettle_soup", 8882149 | 0xFF000000, ItemRegistry.NETTLE_SOUP),
    NETTLE_TEA(1, "nettle_tea", 8519684 | 0xFF000000, ItemRegistry.NETTLE_TEA),
    PHEROMONE_EXTRACT(2, "pheromone_extract", 10208437 | 0xFF000000, ItemRegistry.PHEROMONE_EXTRACT),
    //TODO
    DRINKABLE_BREW_3(3, "drinkable_brew_3", 9316177 | 0xFF000000, null),
    DRINKABLE_BREW_4(4, "drinkable_brew_4", 15384110 | 0xFF000000, null),
    DRINKABLE_BREW_5(5, "drinkable_brew_5", 52901 | 0xFF000000, null),
    DRINKABLE_BREW_6(6, "drinkable_brew_6", 16747660 | 0xFF000000, null),
    DRINKABLE_BREW_7(7, "drinkable_brew_7", 16378828 | 0xFF000000, null),
    DRINKABLE_BREW_8(8, "drinkable_brew_8", 2170142 | 0xFF000000, null),
    DRINKABLE_BREW_9(9, "drinkable_brew_9", 9416509 | 0xFF000000, null),
    DRINKABLE_BREW_10(10, "drinkable_brew_10", 1804678 | 0xFF000000, null),
    DRINKABLE_BREW_11(11, "drinkable_brew_11", 2189375 | 0xFF000000, null),
    DRINKABLE_BREW_12(12, "drinkable_brew_12", 12761312 | 0xFF000000, null),
    DRINKABLE_BREW_13(13, "drinkable_brew_13", 11295547 | 0xFF000000, null),
    DRINKABLE_BREW_14(14, "drinkable_brew_14", 6951282 | 0xFF000000, null),
    DRINKABLE_BREW_15(15, "drinkable_brew_15", 9936283 | 0xFF000000, null);

	private static final EnumBLDrinkableBrew[] META_LOOKUP = new EnumBLDrinkableBrew[values().length];

	private final int meta;
	private final String name;

	private final int colorValue;
	private final float[] colorComponentValues;
	private Item brewItem;

	private EnumBLDrinkableBrew(int metaIn, String nameIn, int colorValueIn, Item item) {
		this.meta = metaIn;
		this.name = nameIn;
		this.colorValue = colorValueIn;
		int i = (colorValueIn & 16711680) >> 16;
		int j = (colorValueIn & 65280) >> 8;
		int k = (colorValueIn & 255) >> 0;
		this.colorComponentValues = new float[] { (float) i / 255.0F, (float) j / 255.0F, (float) k / 255.0F };
		this.brewItem = item;
	}

	public int getMetadata() {
		return this.meta;
	}
	
	public ItemStack getBrewItemStack() {
		return this.brewItem != null ? new ItemStack(this.brewItem) : ItemStack.EMPTY;
	}

	@SideOnly(Side.CLIENT)
	public String getBrewColourName() {
		return this.name;
	}

	public String getTranslationKey() {
		return this.name;
	}

	/**
	 * Gets the RGB color corresponding to this brew color.
	 */
	@SideOnly(Side.CLIENT)
	public int getColorValue() {
		return this.colorValue;
	}

	/**
	 * Gets an array containing 3 floats ranging from 0.0 to 1.0: the red,
	 * green, and blue components of the corresponding color.
	 */
	public float[] getColorComponentValues() {
		return this.colorComponentValues;
	}

	public static EnumBLDrinkableBrew byMetadata(int meta) {
		if (meta < 0 || meta >= META_LOOKUP.length)
			meta = 0;
		return META_LOOKUP[meta];
	}

	public String toString() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	static {
		for (EnumBLDrinkableBrew enumbrewcolor : values())
			META_LOOKUP[enumbrewcolor.getMetadata()] = enumbrewcolor;
	}
}