package thebetweenlands.common.item.shields;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

		this.addPropertyOverride(new ResourceLocation("thebetweenlands.shield.charging"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack && (entityIn.getEntityData().getInteger("thebetweenlands.shield.chargingTicks") > 0 || (stack.getTagCompound() != null && stack.getTagCompound().hasKey("thebetweenlands.shield.charging") && stack.getTagCompound().getBoolean("thebetweenlands.shield.charging"))) ? 1.0F : 0.0F;
			}
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if(stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setBoolean("thebetweenlands.shield.charging", playerIn.isSneaking());
		playerIn.setActiveHand(handIn);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if(player instanceof EntityPlayer) {
			int chargingTicks = player.getEntityData().getInteger("thebetweenlands.shield.chargingTicks");

			if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("thebetweenlands.shield.charging") && stack.getTagCompound().getBoolean("thebetweenlands.shield.charging") && !player.isSneaking() && chargingTicks <= 0) {
				int useCount = this.getMaxItemUseDuration(stack) - count;
				float strength = MathHelper.clamp(useCount / 20.0F - 0.2F, 0.0F, 1.0F);
				player.getEntityData().setInteger("thebetweenlands.shield.chargingTicks", (int)(80 * strength));
				stack.getTagCompound().setBoolean("thebetweenlands.shield.charging", false);
			} else if(player.isSneaking()) {
				if(stack.getTagCompound() == null) {
					stack.setTagCompound(new NBTTagCompound());
				}
				stack.getTagCompound().setBoolean("thebetweenlands.shield.charging", true);
			}

			if(chargingTicks > 0) {
				if(player.onGround && !player.isSneaking()) {
					Vec3d dir = player.getLookVec();
					dir = new Vec3d(dir.x, 0, dir.z).normalize();
					player.motionX += dir.x * 0.35D;
					player.motionZ += dir.z * 0.35D;

					((EntityPlayer) player).getFoodStats().addExhaustion(0.75F);
				}
				if(Math.sqrt(player.motionX*player.motionX + player.motionZ*player.motionZ) > 0.2D) {
					Vec3d moveDir = new Vec3d(player.motionX, player.motionY, player.motionZ).normalize();
					List<EntityLivingBase> targets = player.world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().grow(1), e -> e != player);
					for(EntityLivingBase target : targets) {
						target.knockBack(player, 6.0F, -moveDir.x, -moveDir.z);
						target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), 10.0F);
					}
				}
				player.getEntityData().setInteger("thebetweenlands.shield.chargingTicks", --chargingTicks);
				if(chargingTicks == 0) {
					player.stopActiveHand();
				}
			}
		}

		super.onUsingTick(stack, player, count);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
		entityLiving.getEntityData().setInteger("thebetweenlands.shield.chargingTicks", 0);
		if(stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setBoolean("thebetweenlands.shield.charging", false);
	}

	@Override
	public boolean canBlockDamageSource(ItemStack stack, EntityLivingBase attacked, EnumHand hand, DamageSource source) {
		if(attacked.getEntityData().getInteger("thebetweenlands.shield.chargingTicks") > 0) {
			return true;
		}
		return super.canBlockDamageSource(stack, attacked, hand, source);
	}

	@Override
	public float getBlockedDamage(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		if(attacked.getEntityData().getInteger("thebetweenlands.shield.chargingTicks") > 0) {
			return 0;
		}
		return super.getBlockedDamage(stack, attacked, damage, source);
	}

	@Override
	public float getDefenderKnockbackMultiplier(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		if(attacked.getEntityData().getInteger("thebetweenlands.shield.chargingTicks") > 0) {
			return 0;
		}
		return super.getDefenderKnockbackMultiplier(stack, attacked, damage, source);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onUpdateFov(FOVUpdateEvent event) {
		ItemStack activeItem = event.getEntity().getActiveItemStack();
		if(!activeItem.isEmpty() && activeItem.getItem() instanceof ItemSyrmoriteShield && activeItem.getTagCompound() != null && activeItem.getTagCompound().getBoolean("thebetweenlands.shield.charging")) {
			int usedTicks = activeItem.getItem().getMaxItemUseDuration(activeItem) - event.getEntity().getItemInUseCount();
			float strength = MathHelper.clamp(usedTicks / 20.0F - 0.2F, 0.0F, 1.0F);
			event.setNewfov(1.0F - strength * 0.25F);
		}
	}
}
