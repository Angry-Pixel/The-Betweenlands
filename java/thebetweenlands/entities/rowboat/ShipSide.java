package thebetweenlands.entities.rowboat;

import java.util.EnumMap;

import com.google.common.base.Defaults;

public enum ShipSide {
	STARBOARD {
		@Override
		public ShipSide getOpposite() {
			return PORT;
		}
	},
	PORT {
		@Override
		public ShipSide getOpposite() {
			return STARBOARD;
		}
	};

	public abstract ShipSide getOpposite();

	public static <T> EnumMap<ShipSide, T> newEnumMap(Class<T> type, T... defaultValues) {
		EnumMap map = new EnumMap<ShipSide, T>(ShipSide.class);
		ShipSide[] sides = values();
		for (int i = 0 ; i < sides.length; i++) {
			map.put(sides[i], i >= 0 && i < defaultValues.length ? defaultValues[i] : Defaults.defaultValue(type));
		}
		return map;
	}
}
