package thebetweenlands.herblore.effects;

import net.minecraft.potion.Potion;
import thebetweenlands.utils.PotionHelper;

public class ElixirEffect extends Potion {
	private String effectName;
	
	public ElixirEffect(String name) {
		super(PotionHelper.getFreePotionId(), false, 0xFFFF0000);
		this.effectName = name;
	}

	public String getEffectName() {
		return this.effectName;
	}
	
	public int getDuration() {
		return 0;
	}
	
	public int getStrength() {
		return 0;
	}
}
