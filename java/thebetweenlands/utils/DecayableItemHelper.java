package thebetweenlands.utils;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.manager.DecayManager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public final class DecayableItemHelper {
	public static final int MAX_DECAY = 255;
	public static final String TOOLTIP_PART = "/" + MAX_DECAY + ")";
	public static final int DECAY_STAGE_COUNT = 6;

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

	public static float getModifier(ItemStack itemStack) {
		return (-0.5F * (getDecay(itemStack) / (float) MAX_DECAY) + 1);
	}

	public static float getDigSpeed(float normalDigSpeed, ItemStack itemStack, Block block, int meta) {
		return normalDigSpeed * getModifier(itemStack);
	}

	public static Multimap getAttributeModifiers(ItemStack stack, UUID uuid, float damageVsEntity) {
		Multimap multimap = HashMultimap.create();
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(uuid, "Tool modifier", damageVsEntity * getModifier(stack), 0));
		return multimap;
	}

	public static void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		if (world.isRemote) {
			return;
		}
		int decay = getDecay(itemStack);
		if (decay < MAX_DECAY) {
			float probability = holder.isInWater() ? 0.0075F : 0.0025F;
			if (holder instanceof EntityPlayer) {
				probability *= ((((EntityPlayer) holder).isUsingItem() || ((EntityPlayer) holder).isSwingInProgress) && isHeldItem) ? 1.025F : 1;
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
		decayInfo.append(getDecayStage(decay));
		decayInfo.replace(0, decayInfo.length(), StatCollector.translateToLocal(decayInfo.toString()));
		if (advancedItemTooltips) {
			decayInfo.append(" (");
			decayInfo.append(decay);
			decayInfo.append(TOOLTIP_PART);
		}
		lines.add(decayInfo.toString());
	}

	public static int getDecayStage(ItemStack itemStack) {
		return getDecayStage(getDecay(itemStack));
	}

	public static int getDecayStage(int decay) {
		return (10 * decay + 630) / 635;
	}
}
