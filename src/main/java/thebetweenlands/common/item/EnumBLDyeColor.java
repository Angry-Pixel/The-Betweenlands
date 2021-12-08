package thebetweenlands.common.item;

import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumBLDyeColor implements IStringSerializable
{
    DULL_LAVENDAR(0, "dull_lavender", 8882149),
    MAROON(1, "maroon", 8519684),
    SHADOW_GREEN(2, "shadow_green", 10208437),
    CAMELOT_MAGENTA(3, "camelot_magenta", 9316177),
    SAFFRON(4, "saffron", 15384110),
    CARIBBEAN_GREEN(5, "caribbean_green", 52901),
    VIVID_TANGERINE(6, "vivid_tangerine", 16747660),
    CHAMPAGNE(7, "champagne", 16378828),
    RAISIN_BLACK(8, "raisin_black", 2170142),
    SUSHI_GREEN(9, "sushi_green", 9416509),
    ELM_CYAN(10, "elm_cyan", 1804678),
    CADMIUM_GREEN(11, "cadmium_green", 2189375),
    LAVENDAR_BLUE(12, "lavender_blue", 12761312),
    BROWN_RUST(13, "brown_rust", 11295547),
    MIDNIGHT_PURPLE(14, "midnight_purple", 6951282),
    PEWTER_GREY(15, "pewter_grey", 9936283);

	private static final EnumBLDyeColor[] META_LOOKUP = new EnumBLDyeColor[values().length];

	private final int meta;
	private final String name;

	private final int colorValue;
	private final float[] colorComponentValues;

	private EnumBLDyeColor(int metaIn, String nameIn, int colorValueIn) {
		this.meta = metaIn;
		this.name = nameIn;
		this.colorValue = colorValueIn;
		int i = (colorValueIn & 16711680) >> 16;
		int j = (colorValueIn & 65280) >> 8;
		int k = (colorValueIn & 255) >> 0;
		this.colorComponentValues = new float[] { (float) i / 255.0F, (float) j / 255.0F, (float) k / 255.0F };
	}

	public int getMetadata() {
		return this.meta;
	}

	@SideOnly(Side.CLIENT)
	public String getDyeColorName() {
		return this.name;
	}

	public String getTranslationKey() {
		return this.name;
	}

	/**
	 * Gets the RGB color corresponding to this dye color.
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

	public static EnumBLDyeColor byMetadata(int meta) {
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
		for (EnumBLDyeColor enumdyecolor : values())
			META_LOOKUP[enumdyecolor.getMetadata()] = enumdyecolor;
	}
}