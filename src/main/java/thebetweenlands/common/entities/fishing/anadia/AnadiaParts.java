package thebetweenlands.common.entities.fishing.anadia;

import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public class AnadiaParts {

	public enum AnadiaHeadParts {
		// part (speedModifier, healthModifier, strengthModifier, stamina)
		HEAD_1(0.125F, 1F, 1F, 1F),
		HEAD_2(0.25F, 2F, 2F, 2F),
		HEAD_3(0.5F, 3F, 3F, 3F),

		UNKNOWN(0.25F, 1F, 1F, 1F);

		private final float speed; // added to movement speed
		private final float health; // added to health
		private final float strength; // added to attack if aggressive, and/or rod damage per catch
		private final float stamina; // possible use for how much it pulls or time taken to catch once hooked

		AnadiaHeadParts(float speedModifier, float healthModifier, float strengthModifier, float staminaModifier) {
			this.speed = speedModifier;
			this.health = healthModifier;
			this.strength = strengthModifier;
			this.stamina = staminaModifier;
		}

		public float getSpeedModifier() {
			return this.speed;
		}

		public float getHealthModifier() {
			return this.health;
		}

		public float getStrengthModifier() {
			return this.strength;
		}

		public float getStaminaModifier() {
			return this.stamina;
		}

		public static AnadiaHeadParts random(RandomSource random) {
			return values()[random.nextInt(values().length - 1)];
		}

		public static AnadiaHeadParts get(int id) {
			if (id >= 0 && id < values().length) {
				return values()[id];
			}
			return AnadiaHeadParts.UNKNOWN;
		}
	}

	public enum AnadiaBodyParts {
		// part (speedModifier, healthModifier, strengthModifier, stamina)
		BODY_1(0.125F, 1F, 1F, 1F),
		BODY_2(0.25F, 2F, 2F, 2F),
		BODY_3(0.5F, 3F, 3F, 3F),
		UNKNOWN(0.25F, 1F, 1F, 1F);

		private final float speed; // added to movement speed
		private final float health; // added to health
		private final float strength; // added to attack if aggressive, and/or rod damage per catch
		private final float stamina; // possible use for how much it pulls or time taken to catch once hooked

		AnadiaBodyParts(float speedModifier, float healthModifier, float strengthModifier, float staminaModifier) {
			this.speed = speedModifier;
			this.health = healthModifier;
			this.strength = strengthModifier;
			this.stamina = staminaModifier;
		}

		public float getSpeedModifier() {
			return this.speed;
		}

		public float getHealthModifier() {
			return this.health;
		}

		public float getStrengthModifier() {
			return this.strength;
		}

		public float getStaminaModifier() {
			return this.stamina;
		}

		public static AnadiaBodyParts random(RandomSource random) {
			return values()[random.nextInt(values().length - 1)];
		}

		public static AnadiaBodyParts get(int id) {
			if (id >= 0 && id < values().length) {
				return values()[id];
			}
			return AnadiaBodyParts.UNKNOWN;
		}
	}

	public enum AnadiaTailParts {
		// part (speedModifier, healthModifier, strengthModifier, stamina)
		TAIL_1(0.125F, 1F, 1F, 1F),
		TAIL_2(0.25F, 2F, 2F, 2F),
		TAIL_3(0.5F, 3F, 3F, 3F),

		UNKNOWN(0.25F, 1F, 1F, 1F);

		private final float speed; // added to movement speed
		private final float health; // added to health
		private final float strength; // added to attack if aggressive, and/or rod damage per catch
		private final float stamina; // possible use for how much it pulls or time taken to catch once hooked

		AnadiaTailParts(float speedModifier, float healthModifier, float strengthModifier, float staminaModifier) {
			this.speed = speedModifier;
			this.health = healthModifier;
			this.strength = strengthModifier;
			this.stamina = staminaModifier;
		}

		public float getSpeedModifier() {
			return this.speed;
		}

		public float getHealthModifier() {
			return this.health;
		}

		public float getStrengthModifier() {
			return this.strength;
		}

		public float getStaminaModifier() {
			return this.stamina;
		}

		public static AnadiaTailParts random(RandomSource random) {
			return values()[random.nextInt(values().length - 1)];
		}

		public static AnadiaTailParts get(int id) {
			if (id >= 0 && id < values().length) {
				return values()[id];
			}
			return AnadiaTailParts.UNKNOWN;
		}
	}

	public enum AnadiaColor implements StringRepresentable {
		SMOKED(false, 0xFF747479),
		ROTTEN(false, 0xFF5FB050),
		BASE(true, 0xFF717A51),
		SILVER(true, 0xFFC2B3DB),
		PURPLE(true, 0xFF714147),
		GREEN(true, 0xFF415432),
		UNKNOWN(false, 0xFF717A51);

		private final boolean isAlive;
		private final int color;

		AnadiaColor(boolean isAlive, int color) {
			this.isAlive = isAlive;
			this.color = color;
		}

		public boolean isAlive() {
			return this.isAlive;
		}

		public int getColor() {
			return this.color;
		}

		public static AnadiaColor get(int id) {
			if (id >= 0 && id < values().length) {
				return values()[id];
			}
			return AnadiaColor.UNKNOWN;
		}

		public static AnadiaColor random(RandomSource random) {
			return values()[random.nextInt(values().length - 1)];
		}

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
