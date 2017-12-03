package thebetweenlands.common.item.shields;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.ItemBLShield;

public class ItemSyrmoriteShield extends ItemBLShield {
	public ItemSyrmoriteShield() {
		super(BLMaterialRegistry.TOOL_SYRMORITE);

		this.addPropertyOverride(new ResourceLocation("charging"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack && (getRemainingChargeTicks(stack, entityIn) > 0 || isPreparingCharge(stack, entityIn)) ? 1.0F : 0.0F;
			}
		});
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

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if(player instanceof EntityPlayer) {
			boolean preparing = this.isPreparingCharge(stack, player);

			int runningTicks = this.getRemainingChargeTicks(stack, player);

			if(preparing && runningTicks <= 0) {
				this.setPreparingChargeTicks(stack, player, this.getPreparingChargeTicks(stack, player) + 1);
			}

			if(preparing && !player.isSneaking() && runningTicks <= 0) {
				float strength = MathHelper.clamp(this.getPreparingChargeTicks(stack, player) / 20.0F - 0.2F, 0, 1);
				this.setRemainingChargeTicks(stack, player, (int)(strength * strength * 80));
				this.setPreparingChargeTicks(stack, player, 0);
				this.setPreparingCharge(stack, player, false);
			} else if(!preparing && player.isSneaking()) {
				this.setRemainingChargeTicks(stack, player, 0);
				this.setPreparingChargeTicks(stack, player, 0);
				this.setPreparingCharge(stack, player, true);
			}

			if(runningTicks > 0) {
				if(player.onGround && !player.isSneaking()) {
					Vec3d dir = player.getLookVec();
					dir = new Vec3d(dir.x, 0, dir.z).normalize();
					player.motionX += dir.x * 0.35D;
					player.motionZ += dir.z * 0.35D;

					((EntityPlayer) player).getFoodStats().addExhaustion(0.25F);
				}
				if(Math.sqrt(player.motionX*player.motionX + player.motionZ*player.motionZ) > 0.2D) {
					Vec3d moveDir = new Vec3d(player.motionX, player.motionY, player.motionZ).normalize();
					List<EntityLivingBase> targets = player.world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().grow(1), e -> e != player);
					for(EntityLivingBase target : targets) {
						target.knockBack(player, 6.0F, -moveDir.x, -moveDir.z);
						target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), 10.0F);
					}
				}
				this.setRemainingChargeTicks(stack, player, --runningTicks);
				if(runningTicks == 0) {
					player.stopActiveHand();
				}
			}
		}

		super.onUsingTick(stack, player, count);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
		this.setPreparingChargeTicks(stack, entityLiving, 0);
		this.setRemainingChargeTicks(stack, entityLiving, 0);
		this.setPreparingCharge(stack, entityLiving, false);
	}

	@Override
	public boolean canBlockDamageSource(ItemStack stack, EntityLivingBase attacked, EnumHand hand, DamageSource source) {
		if(this.getRemainingChargeTicks(stack, attacked) > 0) {
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
		if(!activeItem.isEmpty() && activeItem.getItem() instanceof ItemSyrmoriteShield && ((ItemSyrmoriteShield)activeItem.getItem()).isPreparingCharge(activeItem, event.getEntity())) {
			int usedTicks = activeItem.getItem().getMaxItemUseDuration(activeItem) - event.getEntity().getItemInUseCount();
			float strength = MathHelper.clamp(usedTicks / 20.0F - 0.2F, 0.0F, 1.0F);
			event.setNewfov(1.0F - strength * strength * 0.25F);
		}
	}
}
