package thebetweenlands.client.color;

public record AmateMapColor(int id, int color) {

	// HEX to signed decimal (FF - Blue - Green - Red)
	public static final AmateMapColor[] MAP_COLORS = new AmateMapColor[64];
	public static final AmateMapColor EMPTY = new AmateMapColor(0, 0);
	public static final AmateMapColor DEFAULT = new AmateMapColor(1, -10648444);
	public static final AmateMapColor BORDER = new AmateMapColor(2, -16115169);
	public static final AmateMapColor PATCHY_ISLANDS = new AmateMapColor(3, -14992057);
	public static final AmateMapColor SWAMPLANDS = new AmateMapColor(4, -10648444);
	public static final AmateMapColor DEEP_WATERS = new AmateMapColor(5, -9267562);
	public static final AmateMapColor COARSE_ISLANDS = new AmateMapColor(6, -12097390);
	public static final AmateMapColor RAISED_ISLES = new AmateMapColor(7, -12873584);
	public static final AmateMapColor SLUDGE_PLAINS = new AmateMapColor(8, -15003041);
	public static final AmateMapColor MARSH = new AmateMapColor(9, -12871271);
	public static final AmateMapColor ERODED_MARSH = new AmateMapColor(10, -13991791);
	public static final AmateMapColor SWAMPLANDS_CLEARING = new AmateMapColor(11, -9202283);

	public AmateMapColor(int id, int color) {
		if (id >= 0 && id <= 63) {
			this.color = color;
			this.id = id;
			MAP_COLORS[id] = this;
		} else {
			throw new IndexOutOfBoundsException(id + " is out of bounds! (0-63)");
		}
	}
}
