package thebetweenlands.utils;

import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

import thebetweenlands.client.tooltips.HeldItemTooltipHandler;
import thebetweenlands.manager.DecayManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public final class DecayableItemHelper {
	public static final int MAX_DECAY = 255;
	public static final String TOOLTIP_PART = "/" + MAX_DECAY + ")";

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
			float probability = holder.isInWater() ? 0.0075F : 0.0025F;
			if (holder instanceof EntityPlayer) {
				probability *= ((EntityPlayer) holder).isUsingItem() ? 1.025F : 1;
				float playerDecay = DecayManager.getDecayLevel((EntityPlayer) holder) / 20F;
				probability *= (1 - playerDecay * 0.5F);
			}
			if (world.rand.nextFloat() < probability) {
				setDecay(itemStack, decay + 1);
			}
		}
	}

	public static void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		int decay = getDecay(itemStack);
		StringBuilder decayInfo = new StringBuilder("decay.");
		decayInfo.append((10 * decay + 630) / 635);
		decayInfo.replace(0, decayInfo.length(), StatCollector.translateToLocal(decayInfo.toString()));
		if (advancedItemTooltips) {
			decayInfo.append(" (");
			decayInfo.append(decay);
			decayInfo.append(TOOLTIP_PART);
		}
		lines.add(decayInfo.toString());
	}
}
