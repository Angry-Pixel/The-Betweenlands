package thebetweenlands.util;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum DrinkableBrew implements StringRepresentable {

	NETTLE_SOUP(736523, 2, 0.3F, false, 0), //just soup
	NETTLE_TEA(2516511, 0, 0.2F, false, 0), //just tea
	PHEROMONE_EXTRACT(7165556, 0, 0F, true, 2400), // masks from infestations
	SWAMP_BROTH(4741952, 0, 0.2F, true, 400), //decay reduction
	STURDY_STOCK(7361080, 0, 2F, false, 0), // lots of saturation
	PEAR_CORDIAL(15525839, 0, 0.2F, true, 400), //reduce fall damage
	SHAMANS_BREW(14380126, 0, 0.2F, true, 400), // NV and Hunter's sense
	LAKE_BROTH(5731428, 0, 0.2F, true, 400), // water breathing
	SHELL_STOCK(11053143, 0, 0.2F, true, 400), // light footed across sludge and mud etc
	FROG_LEG_EXTRACT(4415799, 0, 0.2F, true, 400), // jumping for 20 secs
	WITCH_TEA(4014914, 0, 0.2F, true, 0); // restores food sickness and something about worms

	private final int healAmount;
	private final float saturationModifier;
	private final boolean hasBuff;
	private final int buffDuration;

	private final int colorValue;

	DrinkableBrew(int color, int healAmount, float saturationModifier, boolean hasBuff, int buffDuration) {
		this.colorValue = color;
		this.healAmount = healAmount;
		this.saturationModifier = saturationModifier;
		this.hasBuff = hasBuff;
		this.buffDuration = buffDuration;
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

	public int getColorValue() {
		return this.colorValue;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}
