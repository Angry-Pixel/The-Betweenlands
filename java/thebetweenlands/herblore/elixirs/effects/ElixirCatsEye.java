package thebetweenlands.herblore.elixirs.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class ElixirCatsEye extends ElixirEffect {
	public ElixirCatsEye(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		if(entity.getActivePotionEffect(Potion.nightVision) == null || entity.getActivePotionEffect(Potion.nightVision).getDuration() != this.getDuration(entity)) {
			entity.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), this.getDuration(entity), strength));
		}
	}
}
