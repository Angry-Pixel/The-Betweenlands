package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;


public class ItemChirobarbEruptor extends Item {
	public ItemChirobarbEruptor() {
		super();
		maxStackSize = 1;
		setMaxDamage(32);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.chirobarb_eruptor.usage"), 0));
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeldItem) {
		if(!world.isRemote) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			if (!stack.getTagCompound().hasKey("cooldown"))
				stack.getTagCompound().setInteger("cooldown", 0);
			if (!stack.getTagCompound().hasKey("shooting"))
				stack.getTagCompound().setBoolean("shooting", false);
			if (!stack.getTagCompound().hasKey("rotation"))
				stack.getTagCompound().setInteger("rotation", 0);
	
			
			if (stack.getTagCompound().getInteger("cooldown") < 60)
				stack.getTagCompound().setInteger("cooldown", stack.getTagCompound().getInteger("cooldown") + 1);
	
			if (stack.getTagCompound().getInteger("cooldown") >= 60) {
				stack.getTagCompound().setInteger("cooldown", 60);
				stack.getTagCompound().setInteger("rotation", 0);
			}
			
			if (stack.getTagCompound().getBoolean("shooting") && entity instanceof EntityLivingBase) {
				stack.getTagCompound().setInteger("rotation", stack.getTagCompound().getInteger("rotation") + 30);
				if (stack.getTagCompound().getInteger("rotation") > 720) {
					stack.getTagCompound().setInteger("rotation", 0);
					stack.getTagCompound().setBoolean("shooting", false);
				}
	
				if (stack.getTagCompound().getInteger("rotation") % 30 == 0) {
					EntityBLArrow arrow = new EntityBLArrow(world, (EntityLivingBase) entity);
					arrow.setType(EnumArrowType.CHIROMAW_BARB);
					arrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
					double angle = Math.toRadians(entity.rotationYaw + stack.getTagCompound().getInteger("rotation") - 30F);
					double offSetX = -Math.sin(angle) * 1.5D;
					double offSetZ = Math.cos(angle) * 1.5D;
					arrow.setPosition(entity.posX + offSetX, entity.posY + entity.height * 0.75D, entity.posZ + offSetZ);
					arrow.shoot(entity, 0F, entity.rotationYaw + stack.getTagCompound().getInteger("rotation") - 30F, 1.5F, 1F, 0F);
					world.playSound(null, entity.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, SoundCategory.NEUTRAL, 0.5F, 1F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.8F);
					world.spawnEntity(arrow);
				}
			}
		}
	}

	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    	ItemStack stack = player.getHeldItem(hand);

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
		}

		if(stack.getTagCompound().getInteger("cooldown") < 60) {
			stack.getTagCompound().setInteger("cooldown", 0);
			return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
		}

		if (!stack.getTagCompound().getBoolean("shooting")) {
			if (!world.isRemote) {
				stack.damageItem(1, player);
				stack.getTagCompound().setBoolean("shooting", true);
				stack.getTagCompound().setInteger("cooldown", 0);

				}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("cooldown");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		boolean wasCharging = oldStack.getTagCompound() != null && oldStack.getTagCompound().getInteger("cooldown") < 60;
		boolean isCharging = newStack.getTagCompound() != null && newStack.getTagCompound().getInteger("cooldown") < 60;
		return (super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !isCharging || isCharging != wasCharging) || !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}
}
