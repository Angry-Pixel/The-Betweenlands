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
	protected boolean isInstant() {
		return true;
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		if(this.getDuration(entity) > 0) {
			entity.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), this.getDuration(entity), strength));
			this.removeElixir(entity);
		}
	}
}
