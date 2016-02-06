package thebetweenlands.utils.mc;

import net.minecraft.block.material.MapColor;
import net.minecraft.util.EnumChatFormatting;

public enum EnumDyeColor {
	WHITE(0, 15, "white", "white", MapColor.snowColor, EnumChatFormatting.WHITE),
	ORANGE(1, 14, "orange", "orange", MapColor.adobeColor, EnumChatFormatting.GOLD),
	MAGENTA(2, 13, "magenta", "magenta", MapColor.magentaColor, EnumChatFormatting.AQUA),
	LIGHT_BLUE(3, 12, "light_blue", "lightBlue", MapColor.lightBlueColor, EnumChatFormatting.BLUE),
	YELLOW(4, 11, "yellow", "yellow", MapColor.yellowColor, EnumChatFormatting.YELLOW),
	LIME(5, 10, "lime", "lime", MapColor.limeColor, EnumChatFormatting.GREEN),
	PINK(6, 9, "pink", "pink", MapColor.pinkColor, EnumChatFormatting.LIGHT_PURPLE),
	GRAY(7, 8, "gray", "gray", MapColor.grayColor, EnumChatFormatting.DARK_GRAY),
	SILVER(8, 7, "silver", "silver", MapColor.silverColor, EnumChatFormatting.GRAY),
	CYAN(9, 6, "cyan", "cyan", MapColor.cyanColor, EnumChatFormatting.DARK_AQUA),
	PURPLE(10, 5, "purple", "purple", MapColor.purpleColor, EnumChatFormatting.DARK_PURPLE),
	BLUE(11, 4, "blue", "blue", MapColor.blueColor, EnumChatFormatting.DARK_BLUE),
	BROWN(12, 3, "brown", "brown", MapColor.brownColor, EnumChatFormatting.GOLD),
	GREEN(13, 2, "green", "green", MapColor.greenColor, EnumChatFormatting.DARK_GREEN),
	RED(14, 1, "red", "red", MapColor.redColor, EnumChatFormatting.DARK_RED),
	BLACK(15, 0, "black", "black", MapColor.blackColor, EnumChatFormatting.BLACK);
	private static final EnumDyeColor[] META_LOOKUP = new EnumDyeColor[values().length];

	private static final EnumDyeColor[] DYE_DMG_LOOKUP = new EnumDyeColor[values().length];

	private final int meta;

	private final int dyeDamage;

	private final String name;

	private final String unlocalizedName;

	private final MapColor mapColor;

	private final EnumChatFormatting chatColor;

	private static final String __OBFID = "CL_00002180";

	private EnumDyeColor(int meta, int dyeDamage, String name, String unlocalizedName, MapColor mapColorIn, EnumChatFormatting chatColor) {
		this.meta = meta;
		this.dyeDamage = dyeDamage;
		this.name = name;
		this.unlocalizedName = unlocalizedName;
		this.mapColor = mapColorIn;
		this.chatColor = chatColor;
	}

	public int getMetadata() {
		return this.meta;
	}

	public int getDyeDamage() {
		return this.dyeDamage;
	}

	public String getUnlocalizedName() {
		return this.unlocalizedName;
	}

	public MapColor getMapColor() {
		return this.mapColor;
	}

	public static EnumDyeColor byDyeDamage(int damage) {
		if (damage < 0 || damage >= DYE_DMG_LOOKUP.length) {
			damage = 0;
		}

		return DYE_DMG_LOOKUP[damage];
	}

	public static EnumDyeColor byMetadata(int meta) {
		if (meta < 0 || meta >= META_LOOKUP.length) {
			meta = 0;
		}

		return META_LOOKUP[meta];
	}

	public String toString() {
		return this.unlocalizedName;
	}

	public String getName() {
		return this.name;
	}

	static {
		EnumDyeColor[] var0 = values();
		int var1 = var0.length;

		for (int var2 = 0; var2 < var1; ++var2) {
			EnumDyeColor var3 = var0[var2];
			META_LOOKUP[var3.getMetadata()] = var3;
			DYE_DMG_LOOKUP[var3.getDyeDamage()] = var3;
		}
	}
}
