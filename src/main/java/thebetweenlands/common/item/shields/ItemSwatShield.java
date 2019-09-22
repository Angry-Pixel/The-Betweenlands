package thebetweenlands.common.item.shields;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

import java.util.List;

public class ItemSwatShield extends ItemBLShield {
	public ItemSwatShield(ToolMaterial material) {
		super(material);

		this.addPropertyOverride(new ResourceLocation("charging"), (stack, worldIn, entityIn) ->
				entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack && (getRemainingChargeTicks(stack, entityIn) > 0 || isPreparingCharge(stack, entityIn)) ? 1.0F : 0.0F);
	}

	/**
	 * Sets whether the specified user is preparing for a charge attack
	 * @param stack
	 * @param user
	 * @param charging
	 */
	public void setPreparingCharge(ItemStack stack, EntityLivingBase user, boolean charging) {
		user.getEntityData().setBoolean("thebetweenlands.shield.charging", charging);
	}

	/**
	 * Returns whether the specified user is preparing for a charge attack
	 * @param stack
	 * @param user
	 * @return
	 */
	public boolean isPreparingCharge(ItemStack stack, EntityLivingBase user) {
		return user.getEntityData().getBoolean("thebetweenlands.shield.charging");
	}

	/**
	 * Sets how long the specified user has been preparing for a charge attack
	 * @param stack
	 * @param user
	 * @param ticks
	 */
	public void setPreparingChargeTicks(ItemStack stack, EntityLivingBase user, int ticks) {
		user.getEntityData().setInteger("thebetweenlands.shield.chargingTicks", ticks);
	}

	/**
	 * Returns how long the specified user has been preparing for a charge attack
	 * @param stack
	 * @param user
	 * @return
	 */
	public int getPreparingChargeTicks(ItemStack stack, EntityLivingBase user) {
		return user.getEntityData().getInteger("thebetweenlands.shield.chargingTicks");
	}

	/**
	 * Sets for how much longer the specified user can charge
	 * @param stack
	 * @param user
	 * @param ticks
	 */
	public void setRemainingChargeTicks(ItemStack stack, EntityLivingBase user, int ticks) {
		user.getEntityData().setInteger("thebetweenlands.shield.remainingRunningTicks", ticks);
	}

	/**
	 * Returns for how much longer the specified user can charge
	 * @param stack
	 * @param user
	 * @return
	 */
	public int getRemainingChargeTicks(ItemStack stack, EntityLivingBase user) {
		return user.getEntityData().getInteger("thebetweenlands.shield.remainingRunningTicks");
	}

	/**
	 * Returns for how many ticks the user can charge for the specified preparation ticks
	 * @param stack
	 * @param user
	 * @param preparingTicks
	 * @return
	 */
	public int getChargeTime(ItemStack stack, EntityLivingBase user, int preparingTicks) {
		float strength = MathHelper.clamp(this.getPreparingChargeTicks(stack, user) / 20.0F - 0.2F, 0, 1);
		return (int)(strength * strength * this.getMaxChargeTime(stack, user));
	}

	/**
	 * Returns the maximum charge ticks
	 * @param stack
	 * @param user
	 * @return
	 */
	public int getMaxChargeTime(ItemStack stack, EntityLivingBase user) {
		return 80;
	}

	/**
	 * Called when an enemy is rammed
	 * @param stack
	 * @param user
	 * @param enemy
	 * @param rammingDir
	 */
	public void onEnemyRammed(ItemStack stack, EntityLivingBase user, EntityLivingBase enemy, Vec3d rammingDir) {
		boolean attacked = false;
		
		if(user instanceof EntityPlayer) {
			attacked = enemy.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)user), 10.0F);
			
			if (user instanceof EntityPlayerMP)
				AdvancementCriterionRegistry.SWAT_SHIELD.trigger((EntityPlayerMP) user, enemy);
		} else {
			attacked = enemy.attackEntityFrom(DamageSource.causeMobDamage(user), 10.0F);
		}
		
		if(attacked) {
			enemy.knockBack(user, 6.0F, -rammingDir.x, -rammingDir.z);
		}
	}

	/**
	 * Called every tick when charging
	 * @param stack
	 * @param user
	 */
	public void onChargingUpdate(ItemStack stack, EntityLivingBase user) {
		if(user.onGround && !user.isSneaking()) {
			Vec3d dir = user.getLookVec();
			dir = new Vec3d(dir.x, 0, dir.z).normalize();
			user.motionX += dir.x * 0.35D;
			user.motionZ += dir.z * 0.35D;

			if(user instanceof EntityPlayer) {
				((EntityPlayer) user).getFoodStats().addExhaustion(0.15F);
			}
		}
		if(Math.sqrt(user.motionX*user.motionX + user.motionZ*user.motionZ) > 0.2D) {
			Vec3d moveDir = new Vec3d(user.motionX, user.motionY, user.motionZ).normalize();
			
			List<EntityLivingBase> targets = user.world.getEntitiesWithinAABB(EntityLivingBase.class, user.getEntityBoundingBox().grow(1), e -> e != user);
			
			for(EntityLivingBase target : targets) {
				Vec3d dir = target.getPositionVector().subtract(user.getPositionVector()).normalize();
				
				//45° angle range
				if(target.canBeCollidedWith() && Math.toDegrees(Math.acos(moveDir.dotProduct(dir))) < 45) {
					this.onEnemyRammed(stack, user, target, moveDir);
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase user, int count) {
		boolean preparing = this.isPreparingCharge(stack, user);

		int runningTicks = this.getRemainingChargeTicks(stack, user);

		if(preparing && runningTicks <= 0) {
			this.setPreparingChargeTicks(stack, user, this.getPreparingChargeTicks(stack, user) + 1);
		}

		if(preparing && !user.isSneaking() && runningTicks <= 0) {
			this.setRemainingChargeTicks(stack, user, this.getChargeTime(stack, user, this.getPreparingChargeTicks(stack, user)));
			this.setPreparingChargeTicks(stack, user, 0);
			this.setPreparingCharge(stack, user, false);
		} else if(!preparing && user.isSneaking()) {
			this.setRemainingChargeTicks(stack, user, 0);
			this.setPreparingChargeTicks(stack, user, 0);
			this.setPreparingCharge(stack, user, true);
		}

		if(runningTicks > 0) {
			this.onChargingUpdate(stack, user);
			this.setRemainingChargeTicks(stack, user, --runningTicks);
			if(runningTicks == 0) {
				user.stopActiveHand();
			}
		}

		super.onUsingTick(stack, user, count);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
		this.setPreparingChargeTicks(stack, entityLiving, 0);
		this.setRemainingChargeTicks(stack, entityLiving, 0);
		this.setPreparingCharge(stack, entityLiving, false);
		if (entityLiving instanceof EntityPlayerMP)
			AdvancementCriterionRegistry.SWAT_SHIELD.revert((EntityPlayerMP) entityLiving);
	}

	@Override
	public boolean canBlockDamageSource(ItemStack stack, EntityLivingBase attacked, EnumHand hand, DamageSource source) {
		if(this.getRemainingChargeTicks(stack, attacked) > 0 && source.getImmediateSource() != null) {
			return true;
		}
		return super.canBlockDamageSource(stack, attacked, hand, source);
	}

	@Override
	public float getBlockedDamage(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		if(this.getRemainingChargeTicks(stack, attacked) > 0) {
			return 0;
		}
		return super.getBlockedDamage(stack, attacked, damage, source);
	}

	@Override
	public float getDefenderKnockbackMultiplier(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		if(this.getRemainingChargeTicks(stack, attacked) > 0) {
			return 0;
		}
		return super.getDefenderKnockbackMultiplier(stack, attacked, damage, source);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onUpdateFov(FOVUpdateEvent event) {
		ItemStack activeItem = event.getEntity().getActiveItemStack();
		ItemSwatShield swatShield;
		if(!activeItem.isEmpty() && activeItem.getItem() instanceof ItemSwatShield && (swatShield = (ItemSwatShield) activeItem.getItem()).isPreparingCharge(activeItem, event.getEntity())) {
			int preparingTicks = swatShield.getPreparingChargeTicks(activeItem, event.getEntity());
			float progress = Math.min(swatShield.getChargeTime(activeItem, event.getEntity(), preparingTicks) / (float)swatShield.getMaxChargeTime(activeItem, event.getEntity()), 1);
			event.setNewfov(1.0F - progress * 0.25F);
		}
	}
}
