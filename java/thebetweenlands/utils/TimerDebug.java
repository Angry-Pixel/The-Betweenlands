package thebetweenlands.utils;

import java.lang.reflect.Field;

import net.minecraft.util.Timer;

public class TimerDebug extends Timer {
	private static final Field TICKS_PER_SECOND_FIELD = Timer.class.getDeclaredFields()[0];
	static {
		TICKS_PER_SECOND_FIELD.setAccessible(true);
	}

	public TimerDebug(float ticksPerSecond) {
		super(ticksPerSecond);
	}

	public float getTicksPerSecond() {
		try {
			return (float) TICKS_PER_SECOND_FIELD.get(this);
		} catch (Exception e) {
			throw new Error("How did this happen?", e);
		}
	}

	public void setTicksPerSecond(float ticksPerSecond) {
		try {
			TICKS_PER_SECOND_FIELD.set(this, ticksPerSecond);
		} catch (Exception e) {
			throw new Error("How did this happen?", e);
		}
	}
}
