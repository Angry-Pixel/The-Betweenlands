package thebetweenlands.utils;

import java.util.List;

import thebetweenlands.manager.DecayManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;

public final class DecayableItemHelper {
	public static final int MAX_DECAY = 255;

	private DecayableItemHelper() {
	}

	public static int getDecay(ItemStack itemStack) {
		if (itemStack.hasTagCompound()) {
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			if (tagCompound.hasKey("Decay", 3)) {
				return tagCompound.getInteger("Decay");
			}
		}
		return 0;
	}

	public static void setDecay(ItemStack itemStack, int decay) {
		itemStack.setTagInfo("Decay", new NBTTagInt(decay));
	}

	public static float getDigSpeed(float normalDigSpeed, ItemStack itemStack, Block block, int meta) {
		return normalDigSpeed * (-0.5F * (getDecay(itemStack) / (float) MAX_DECAY) + 1);
	}

	public static void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		if (world.isRemote) {
			return;
		}
		int decay = getDecay(itemStack);
		if (decay < MAX_DECAY) {
			float probability = holder.isInWater() ? 0.1F : 0.05F;
			if (holder instanceof EntityPlayer) {
				probability *= ((EntityPlayer) holder).isUsingItem() ? 0.4F : 0.05F;
				float playerDecay = DecayManager.getDecayLevel((EntityPlayer) holder) / 20F;
				probability *= (1 - playerDecay * 0.5F);
			}
			if (world.rand.nextFloat() < probability) {
				setDecay(itemStack, decay + 1);
			}
		}
	}

	public static void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean isShiftDown) {
		int decay = getDecay(itemStack) / 51;
		
	}
}
