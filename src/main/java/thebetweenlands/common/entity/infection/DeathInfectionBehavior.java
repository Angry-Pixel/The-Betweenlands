package thebetweenlands.common.entity.infection;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.entity.IInfectionBehaviorOverlay;
import thebetweenlands.common.registries.SoundRegistry;

public class DeathInfectionBehavior extends AbstractInfectionBehavior implements IInfectionBehaviorOverlay {

	private int ticks = 0;

	private float startMaxHealth;
	
	public DeathInfectionBehavior(EntityLivingBase entity) {
		super(entity);
	}

	@Override
	public void update() {
		++this.ticks;

		if(this.world.isRemote && this.entity instanceof EntityPlayer && this.ticks == 100) {
			this.world.playSound((EntityPlayer) this.entity, this.entity.posX, this.entity.posY, this.entity.posZ, SoundRegistry.INFECTION_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f);
		}
		
		this.entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, this.ticks * 10 / 160));
		
		if(this.ticks >= 160) {
			if(!this.world.isRemote) {
				this.entity.setHealth(0);
				this.entity.attackEntityFrom(DamageSource.GENERIC, Float.MAX_VALUE);
			}
		} else if(this.ticks >= 100) {
			if(!this.world.isRemote) {
				float newMaxHealth = MathHelper.clamp(MathHelper.floor(this.startMaxHealth * (1 - (this.ticks - 100) / 60.0f)), 1.0f, (float)this.entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
				this.entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newMaxHealth);
			}
		} else {
			this.startMaxHealth = (float)this.entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
		}

		super.update();
	}

	@Override
	public boolean isDone() {
		EntityPlayer player = this.entity instanceof EntityPlayer ? (EntityPlayer) this.entity : null;
		return (this.ticks > 160 && this.entity.isEntityAlive()) || this.entity.getIsInvulnerable() || (player != null && (player.isCreative() || player.isSpectator()));
	}

	@Override
	public float getOverlayPercentage() {
		return 1.0f;
	}

	public float getDeathProgress() {
		return this.ticks > 100 ? Math.min((this.ticks - 100) / 60.0f, 1.0f) : 0.0f;
	}
	

	@SubscribeEvent
	public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		getInfectionBehavior(entity, DeathInfectionBehavior.class).ifPresent(behavior -> {
			entity.motionY *= 1.0f - Math.min(1.0f, behavior.ticks / 80.0f);
		});
	}

}
