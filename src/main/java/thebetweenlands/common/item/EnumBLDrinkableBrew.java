package thebetweenlands.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.ItemRegistry;

public enum EnumBLDrinkableBrew implements IStringSerializable
{
    NETTLE_SOUP(0, "nettle_soup", 8882149 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 6, 1F, false, 0), //just soup
    NETTLE_TEA(1, "nettle_tea", 8519684 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 0, 0.2F, false, 0), //just tea
    PHEROMONE_EXTRACT(2, "pheromone_extract", 10208437 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 0, 0F, true, 2400), // masks from infestations
    SWAMP_BROTH(3, "swamp_broth", 4741952 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 0, 0.2F, true, 400), //decay reduction
    STURDY_STOCK(4, "sturdy_stock", 7361080 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 0, 2F, false, 0), // lots of saturation
    PEAR_CORDIAL(5, "pear_cordial", 15525839 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 0, 0.2F, true, 400), //reduce fall damage
    SHAMANS_BREW(6, "shamans_brew", 14380126 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 0, 0.2F, true, 400), // NV and Hunter's sense
    LAKE_BROTH(7, "lake_broth", 5731428 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 0, 0.2F, true, 400), // water breathing
    SHELL_STOCK(8, "shell_stock", 11053143 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 0, 0.2F, true, 400), // light footed across sludge and mud etc
    FROG_LEG_EXTRACT(9, "frog_leg_extract", 4415799 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 0, 0.2F, true, 400), // jumping for 20 secs
    WITCH_TEA(10, "witch_tea", 4014914 | 0xFF000000, ItemRegistry.DRINKABLE_BREW, 0, 0.2F, true, 0); // restores food sickness and something about worms
    
/*
	// spare metas
	DRINKABLE_BREW_11(11, "drinkable_brew_11", 2189375 | 0xFF000000, null, 0, 0F, false, 0),
    DRINKABLE_BREW_12(12, "drinkable_brew_12", 12761312 | 0xFF000000, null, 0, 0F, false, 0),
    DRINKABLE_BREW_13(13, "drinkable_brew_13", 11295547 | 0xFF000000, null, 0, 0F, false, 0),
    DRINKABLE_BREW_14(14, "drinkable_brew_14", 6951282 | 0xFF000000, null, 0, 0F, false, 0),
    DRINKABLE_BREW_15(15, "drinkable_brew_15", 9936283 | 0xFF000000, null, 0, 0F, false, 0);
*/
	private static final EnumBLDrinkableBrew[] META_LOOKUP = new EnumBLDrinkableBrew[values().length];

	private final int meta;
	private final String name;
	private final int healAmount;
	private final float saturationModifier;
	private final boolean hasBuff;
	private final int buffDuration;
	
	private final int colorValue;
	private final float[] colorComponentValues;
	private Item brewItem;

	private EnumBLDrinkableBrew(int metaIn, String nameIn, int colorValueIn, Item item, int healAmountIn, float saturationModifierIn, boolean hasBuffIn, int buffDurationIn) {
		this.meta = metaIn;
		this.name = nameIn;
		this.colorValue = colorValueIn;
		int i = (colorValueIn & 16711680) >> 16;
		int j = (colorValueIn & 65280) >> 8;
		int k = (colorValueIn & 255) >> 0;
		this.colorComponentValues = new float[] { (float) i / 255.0F, (float) j / 255.0F, (float) k / 255.0F };
		this.brewItem = item;
		this.healAmount = healAmountIn;
		this.saturationModifier = saturationModifierIn;
		this.hasBuff = hasBuffIn;
		this.buffDuration = buffDurationIn;
	}

	public int getMetadata() {
		return this.meta;
	}
	
	public ItemStack getBrewItemStack() {
		return this.brewItem != null ? new ItemStack(this.brewItem, 1, this.meta) : ItemStack.EMPTY;
	}

	public int getHealAmount() {
		return this.healAmount;
	}

	public float getSaturationModifier() {
		return this.saturationModifier;
	}
	public boolean hasBuff() {
		return this.hasBuff;
	}

	public int getBuffDuration() {
		return this.buffDuration;
	}

	@SideOnly(Side.CLIENT)
	public String getBrewName() {
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
		for (EnumBLDrinkableBrew enumbrew : values())
			META_LOOKUP[enumbrew.getMetadata()] = enumbrew;
	}
}