package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
import thebetweenlands.common.entity.EntityFishBait;
import thebetweenlands.common.entity.projectiles.EntityBetweenstonePebble;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.misc.ItemFishBait;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemSimpleSlingshot extends Item implements ICorrodible, IAnimatorRepairable {
	public ItemSimpleSlingshot() {
		maxStackSize = 1;
		setMaxDamage(64);
		setCreativeTab(BLCreativeTabs.GEARS);
		CorrosionHelper.addCorrosionPropertyOverrides(this);

	//TODO Add pulling sprites
		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (entityIn == null) {
					return 0.0F;
				} else {
					return entityIn.getActiveItemStack().getItem() != ItemRegistry.SIMPLE_SLINGSHOT ? 0.0F : (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F;
				}
			}
		});
		this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});

	}

	protected ItemStack findAmmo(EntityPlayer player) {
		if (isSlingShotAmmo(player.getHeldItem(EnumHand.OFF_HAND))) {
			return player.getHeldItem(EnumHand.OFF_HAND);
		} else if (isSlingShotAmmo(player.getHeldItem(EnumHand.MAIN_HAND))) {
			return player.getHeldItem(EnumHand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if (isSlingShotAmmo(stack)) {
					return stack;
				}
			}
			return ItemStack.EMPTY;
		}
	}

	protected boolean isSlingShotAmmo(ItemStack stack) {
		return !stack.isEmpty() && (EnumItemMisc.BETWEENSTONE_PEBBLE.isItemOf(stack) || stack.getItem() == ItemRegistry.FISH_BAIT);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			boolean infinite = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
			ItemStack ammo = findAmmo(player);

			int usedTicks = getMaxItemUseDuration(stack) - timeLeft;
			usedTicks = ForgeEventFactory.onArrowLoose(stack, world, (EntityPlayer) entityLiving, usedTicks, !ammo.isEmpty() || infinite);

			if (usedTicks < 0)
				return;

			if (!ammo.isEmpty() || infinite)
				if (ammo.isEmpty())
					ammo = new ItemStack(EnumItemMisc.BETWEENSTONE_PEBBLE.getItem());

			float strength = getAmmoVelocity(usedTicks);

			strength *= CorrosionHelper.getModifier(stack);

			if (strength >= 0.1F) {
				if (!world.isRemote) {
					if (ammo.getItem() == EnumItemMisc.BETWEENSTONE_PEBBLE.getItem()) {
						ItemMisc itemAmmo = (ItemMisc) ammo.getItem();
						EntityBetweenstonePebble pebble = createPebbleAmmo(world, ammo, player);
						pebble.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, strength * 3.0F, 1.0F);

						if (strength == 1.0F)
							pebble.setIsCritical(true);

						int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

						if (j > 0)
							pebble.setDamage(pebble.getDamage() + (double) j * 0.5D + 0.5D);

						int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

						if (k > 0)
							pebble.setKnockbackStrength(k);

						if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
							pebble.setFire(100);

						stack.damageItem(1, player);
						fireAmmo(player, stack, pebble, strength);
					}

					if (ammo.getItem() == ItemRegistry.FISH_BAIT) {
						ItemFishBait itemAmmo = (ItemFishBait) ammo.getItem();
						EntityFishBait bait = (EntityFishBait) itemAmmo.createEntity(world, player, ammo.copy());
						bait.setInfinitePickupDelay();
						ammo.damageItem(1, player);
						bait.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, strength * 3.0F, 1.0F);
						fireBaitAmmo(player, stack, bait, strength);
					}
				}

				world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.SLINGSHOT_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + strength * 0.5F);

				if (!infinite && ammo.getItem() == EnumItemMisc.BETWEENSTONE_PEBBLE.getItem())
					ammo.shrink(1);

				if (ammo.getCount() == 0)
					player.inventory.deleteStack(ammo);

				player.addStat(StatList.getObjectUseStats(this));
			}
		}
	}

	public EntityBetweenstonePebble createPebbleAmmo(World world, ItemStack stack, EntityLivingBase shooter) {
		EntityBetweenstonePebble pebble = new EntityBetweenstonePebble(world, shooter);
		return pebble;
	}

	protected void fireAmmo(EntityPlayer player, ItemStack stack, EntityBetweenstonePebble ammo, float strength) {
		player.world.spawnEntity(ammo);
	}

	protected void fireBaitAmmo(EntityPlayer player, ItemStack stack, EntityFishBait ammo, float strength) {
		player.world.spawnEntity(ammo);
	}

	public static float getAmmoVelocity(int charge) {
		float strength = (float) charge / 20.0F;
		strength = (strength * strength + strength * 2.0F) / 3.0F * 1.15F;
		if (strength > 1.0F)
			strength = 1.0F;
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
		boolean flag = !findAmmo(playerIn).isEmpty();
		ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, hand, flag);
		if (ret != null)
			return ret;
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
		tooltip.add(TextFormatting.YELLOW + new TextComponentTranslation("tooltip.bl.simple_slingshot").getFormattedText());
		CorrosionHelper.addCorrosionTooltips(stack, tooltip, flagIn.isAdvanced());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onUpdateFov(FOVUpdateEvent event) {
		ItemStack activeItem = event.getEntity().getActiveItemStack();
		if (!activeItem.isEmpty() && activeItem.getItem() instanceof ItemSimpleSlingshot) {
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
