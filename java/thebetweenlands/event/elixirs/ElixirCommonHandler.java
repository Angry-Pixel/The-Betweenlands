package thebetweenlands.event.elixirs;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import thebetweenlands.herblore.elixirs.effects.ElixirRegistry;

public class ElixirCommonHandler {
	public static final ElixirCommonHandler INSTANCE = new ElixirCommonHandler();

	private boolean ignoreAttackEvent = false;
	public static final float MAX_DAMAGE_MULTIPLIER = 1.0F;
	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event) {
		if(this.ignoreAttackEvent) return;
		EntityLivingBase attackedEntity = event.entityLiving;
		DamageSource source = event.source;
		if(source.getSourceOfDamage() instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase)source.getSourceOfDamage();
			if(ElixirRegistry.EFFECT_STRENGTH.isActive(living)) {
				int strength = ElixirRegistry.EFFECT_STRENGTH.getStrength(living) + 1;
				float damage = event.ammount;
				this.ignoreAttackEvent = true;
				attackedEntity.attackEntityFrom(source, MathHelper.ceiling_float_int(damage + damage / 4.0F * strength * MAX_DAMAGE_MULTIPLIER));
				this.ignoreAttackEvent = false;
				event.setCanceled(true);
			}
		}
	}
}