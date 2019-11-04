package thebetweenlands.common.item.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityVolarkite;
import thebetweenlands.util.NBTHelper;

public class ItemVolarkite extends Item {
	public ItemVolarkite() {
		this.setCreativeTab(BLCreativeTabs.GEARS);

		this.setMaxStackSize(1);
		this.setMaxDamage(300);

		this.addPropertyOverride(new ResourceLocation("using"), (stack, worldIn, entityIn) -> {
			if(entityIn != null && (entityIn.getRidingEntity() instanceof EntityVolarkite || entityIn.getPassengers().stream().filter(e -> e instanceof EntityVolarkite).findAny().isPresent())) {
				return stack.getTagCompound() != null && stack.getTagCompound().getBoolean("using_kite") ? 1 : 0;
			}
			return 0;
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(!world.isRemote) {
			if(!player.isRiding() && player.getRecursivePassengersByType(EntityVolarkite.class).isEmpty()) {
				EntityVolarkite entity = new EntityVolarkite(world);
				entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, 0);
				entity.motionX = player.motionX;
				entity.motionY = player.motionY;
				entity.motionZ = player.motionZ;
				entity.velocityChanged = true;

				world.spawnEntity(entity);

				player.startRiding(entity);

				world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1, 1);
			}
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, world, entity, itemSlot, isSelected);

		NBTTagCompound tag = NBTHelper.getStackNBTSafe(stack);

		boolean usingKite = false;

		boolean isRidingKite = entity.getRidingEntity() instanceof EntityVolarkite;

		if(entity instanceof EntityLivingBase && (isRidingKite || entity.getPassengers().stream().filter(e -> e instanceof EntityVolarkite).findAny().isPresent())) {
			EntityLivingBase living = (EntityLivingBase) entity;

			boolean isMainHand = stack == living.getHeldItem(EnumHand.MAIN_HAND);
			boolean isOffHand = stack == living.getHeldItem(EnumHand.OFF_HAND);
			boolean hasOffHand = !living.getHeldItem(EnumHand.OFF_HAND).isEmpty() && living.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemVolarkite;
			if((isMainHand || isOffHand) && ((isMainHand && !hasOffHand) || isOffHand)) {
				if(!world.isRemote && isRidingKite && entity.ticksExisted % 20 == 0) {
					stack.damageItem(1, (EntityLivingBase) entity);
				}

				usingKite = true;
				tag.setBoolean("using_kite", true);
			}
		}

		if(!usingKite) {
			if(tag.getBoolean("using_kite")) {
				tag.setBoolean("using_kite", false);

				if(entity instanceof EntityPlayer) {
					((EntityPlayer) entity).getCooldownTracker().setCooldown(stack.getItem(), 20);
				}
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem() || slotChanged;
	}

	public boolean canRideKite(ItemStack stack, Entity entity) {
		return true;
	}
}
