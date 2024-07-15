package thebetweenlands.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum BLDyeColor implements StringRepresentable {
	DULL_LAVENDER(0, "dull_lavender", 8882149),
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
	LAVENDER_BLUE(12, "lavender_blue", 12761312),
	BROWN_RUST(13, "brown_rust", 11295547),
	MIDNIGHT_PURPLE(14, "midnight_purple", 6951282),
	PEWTER_GREY(15, "pewter_grey", 9936283);

	private static final IntFunction<BLDyeColor> BY_ID = ByIdMap.continuous(BLDyeColor::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StringRepresentable.EnumCodec<BLDyeColor> CODEC = StringRepresentable.fromEnum(BLDyeColor::values);
	public static final StreamCodec<ByteBuf, BLDyeColor> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BLDyeColor::getId);

	private final int id;
	private final String name;

	private final int colorValue;
	private final float[] colorComponentValues;

	BLDyeColor(int id, String name, int color) {
		this.id = id;
		this.name = name;
		this.colorValue = color;
		int i = (color & 16711680) >> 16;
		int j = (color & 65280) >> 8;
		int k = (color & 255);
		this.colorComponentValues = new float[]{(float) i / 255.0F, (float) j / 255.0F, (float) k / 255.0F};
	}

	public int getId() {
		return this.id;
	}

	public String getDyeColorName() {
		return this.name;
	}

	public String getTranslationKey() {
		return this.name;
	}

	/**
	 * Gets the RGB color corresponding to this dye color.
	 */
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

	public static BLDyeColor byId(int colorId) {
		return BY_ID.apply(colorId);
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
