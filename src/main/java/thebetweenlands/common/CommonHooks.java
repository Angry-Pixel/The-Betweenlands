package thebetweenlands.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.api.event.SplashPotionEvent;

public final class CommonHooks {
	private CommonHooks() { }

	/**
	 * Called before a splash potion calls affectEntity in applySplash
	 * @param potion
	 * @param target
	 * @param effect
	 * @return True to cancel
	 */
	public static boolean onSplashAffectEntity(EntityPotion potion, EntityLivingBase target, PotionEffect effect) {
		SplashPotionEvent event = new SplashPotionEvent(potion, target, effect, true);
		MinecraftForge.EVENT_BUS.post(event);
		return event.isCanceled();
	}

	/**
	 * Called before a splash potion calls addPotionEffect in applySplash
	 * @param potion
	 * @param target
	 * @param effect
	 * @return True to cancel
	 */
	public static boolean onSplashAddPotionEffect(EntityPotion potion, EntityLivingBase target, PotionEffect effect) {
		SplashPotionEvent event = new SplashPotionEvent(potion, target, effect, false);
		MinecraftForge.EVENT_BUS.post(event);
		return event.isCanceled();
	}
}
