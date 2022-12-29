package thebetweenlands.api.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This event is fired when a splash potion tries to apply its splash potion effects to an entitiy
 */
@Cancelable
public class SplashPotionEvent extends EntityEvent {
	private final Entity potion;
	private final EntityLivingBase target;
	private final PotionEffect effect;
	private final boolean instant;

	public SplashPotionEvent(Entity potion, EntityLivingBase target, PotionEffect effect, boolean instant) {
		super(potion);
		this.potion = potion;
		this.target = target;
		this.effect = effect;
		this.instant = instant;
	}

	public Entity getSplashPotionEntity() {
		return this.potion;
	}

	public EntityLivingBase getTarget() {
		return this.target;
	}

	public PotionEffect getPotionEffect() {
		return this.effect;
	}

	public boolean isInstantEffect() {
		return this.instant;
	}
}
