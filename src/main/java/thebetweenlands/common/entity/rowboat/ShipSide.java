package thebetweenlands.common.entity.rowboat;

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

    public static <T> EnumMap<ShipSide, T> newEnumMap(Class<? extends T> type, T... defaultValues) {
        EnumMap<ShipSide, T> map = new EnumMap<>(ShipSide.class);
        ShipSide[] sides = values();
        for (int i = 0; i < sides.length; i++) {
            map.put(sides[i], i < defaultValues.length && defaultValues[i] != null ? defaultValues[i] : Defaults.defaultValue(type));
        }
        return map;
    }
}
