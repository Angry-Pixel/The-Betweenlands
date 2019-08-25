package thebetweenlands.common.item.tools.bow;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemBLBow extends ItemBow implements ICorrodible, IAnimatorRepairable {
	public ItemBLBow() {
		this.maxStackSize = 1;
		this.setMaxDamage(600);
		this.setCreativeTab(BLCreativeTabs.GEARS);

		CorrosionHelper.addCorrosionPropertyOverrides(this);

		this.addPropertyOverride(new ResourceLocation("pull"), (stack, worldIn, entityIn) -> {
			if (entityIn == null) {
				return 0.0F;
			} else {
				ItemStack itemStack = entityIn.getActiveItemStack();
				return !itemStack.isEmpty() && itemStack == stack ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
			}
		});
	}

	protected ItemStack findArrows(EntityPlayer player) {
		if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
			return player.getHeldItem(EnumHand.OFF_HAND);
		} else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
			return player.getHeldItem(EnumHand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if (this.isArrow(stack)) {
					return stack;
				}
			}
			return ItemStack.EMPTY;
		}
	}

	@Override
	protected boolean isArrow(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemArrow;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			boolean infiniteBow = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
			ItemStack arrow = this.findArrows(player);

			int usedTicks = this.getMaxItemUseDuration(stack) - timeLeft;
			usedTicks = ForgeEventFactory.onArrowLoose(stack, world, (EntityPlayer) entityLiving, usedTicks, !arrow.isEmpty() || infiniteBow);

			if (usedTicks < 0) {
				return;
			}

			if (!arrow.isEmpty() || infiniteBow) {
				if (arrow.isEmpty()) {
					arrow = new ItemStack(ItemRegistry.ANGLER_TOOTH_ARROW);
				}

				float strength = getArrowVelocity(usedTicks);

				strength *= CorrosionHelper.getModifier(stack);

				if (strength >= 0.1F) {

					boolean infiniteArrows = player.capabilities.isCreativeMode || (arrow.getItem() instanceof ItemArrow && ((ItemArrow) arrow.getItem()).isInfinite(arrow, stack, player));

					if (!world.isRemote) {
						ItemArrow itemArrow = (ItemArrow)arrow.getItem();
						EntityArrow entityArrow = itemArrow.createArrow(world, arrow, player);
						entityArrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, strength * 3.0F, 1.0F);

						if (strength == 1.0F) {
							entityArrow.setIsCritical(true);
						}

						int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

						if (j > 0) {
							entityArrow.setDamage(entityArrow.getDamage() + (double) j * 0.5D + 0.5D);
						}

						int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

						if (k > 0) {
							entityArrow.setKnockbackStrength(k);
						}

						if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
							entityArrow.setFire(100);
						}

						stack.damageItem(1, player);

						if (infiniteArrows || player.capabilities.isCreativeMode && (arrow.getItem() == Items.SPECTRAL_ARROW || arrow.getItem() == Items.TIPPED_ARROW)) {
							entityArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
						}

						this.fireArrow(player, stack, entityArrow, strength);
					}

					world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + strength * 0.5F);

					if (!infiniteArrows) {
						arrow.shrink(1);

						if (arrow.getCount() == 0) {
							player.inventory.deleteStack(arrow);
						}
					}

					player.addStat(StatList.getObjectUseStats(this));
				}
			}
		}
	}

	protected void fireArrow(EntityPlayer player, ItemStack stack, EntityArrow arrow, float strength) {
		player.world.spawnEntity(arrow);
	}

	public static float getArrowVelocity(int charge) {
		float strength = (float) charge / 20.0F;
		strength = (strength * strength + strength * 2.0F) / 3.0F * 1.15F;
		if (strength > 1.0F) {
			strength = 1.0F;
		}
		return strength;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 100000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemstack = playerIn.getHeldItem(hand);
		boolean flag = !this.findArrows(playerIn).isEmpty();
		ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, hand, flag);
		if (ret != null) return ret;
		if (!playerIn.capabilities.isCreativeMode && !flag) {
			return new ActionResult<>(EnumActionResult.FAIL, itemstack);
		} else {
			playerIn.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
		}
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return CorrosionHelper.shouldCauseBlockBreakReset(oldStack, newStack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return CorrosionHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		return CorrosionHelper.getDestroySpeed(super.getDestroySpeed(stack, state), stack, state);
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrosionHelper.updateCorrosion(itemStack, world, holder, slot, isHeldItem);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		CorrosionHelper.addCorrosionTooltips(stack, tooltip, flagIn.isAdvanced());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onUpdateFov(FOVUpdateEvent event) {
		ItemStack activeItem = event.getEntity().getActiveItemStack();
		if(!activeItem.isEmpty() && activeItem.getItem() instanceof ItemBLBow) {
			int usedTicks = activeItem.getItem().getMaxItemUseDuration(activeItem) - event.getEntity().getItemInUseCount();
			float strength = (float) usedTicks / 20.0F;
			strength = (strength * strength + strength * 2.0F) / 3.0F * 1.15F;
			if (strength > 1.0F) {
				strength = 1.0F;
			}
			event.setNewfov(1.0F - strength * 0.25F);
		}
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(BLMaterialRegistry.TOOL_WEEDWOOD);
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(BLMaterialRegistry.TOOL_WEEDWOOD);
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(BLMaterialRegistry.TOOL_WEEDWOOD);
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(BLMaterialRegistry.TOOL_WEEDWOOD);
	}
}
