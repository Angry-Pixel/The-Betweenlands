package thebetweenlands.utils;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
import thebetweenlands.decay.DecayManager;
import thebetweenlands.world.BLGamerules;

public final class CorrodibleItemHelper {
	public static final int MAX_CORROSION = 255;
	public static final String TOOLTIP_PART = "/" + MAX_CORROSION + ")";
	public static final int CORROSION_STAGE_COUNT = 6;

	private CorrodibleItemHelper() {
	}

	public static int getCorrosion(ItemStack itemStack) {
		if (itemStack.hasTagCompound()) {
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			if (tagCompound.hasKey("Corrosion", 3)) {
				return tagCompound.getInteger("Corrosion");
			}
		}
		return 0;
	}

	public static void setCorrosion(ItemStack itemStack, int corrosion) {
		itemStack.setTagInfo("Corrosion", new NBTTagInt(corrosion));
	}

	public static float getModifier(ItemStack itemStack) {
		return (-0.7F * (getCorrosion(itemStack) / (float) MAX_CORROSION) + 1);
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
		if (world.isRemote || !BLGamerules.getGameRuleBooleanValue(BLGamerules.BL_CORROSION)) {
			return;
		}
		int corrosion = getCorrosion(itemStack);
		if (corrosion < MAX_CORROSION) {
			float probability = holder.isInWater() ? 0.006F : 0.0014F;
			if (holder instanceof EntityPlayer) {
				probability *= ((((EntityPlayer) holder).isUsingItem() || ((EntityPlayer) holder).isSwingInProgress) && isHeldItem) ? 2.8F : 1.0F;
				float playerCorruption = DecayManager.getCorruptionLevel((EntityPlayer) holder) / 10F;
				probability *= (1 - Math.pow(playerCorruption, 2) * 0.9F);
			}
			if (world.rand.nextFloat() < probability) {
				setCorrosion(itemStack, corrosion + 1);
			}
		}
	}

	public static void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		int corrosion = getCorrosion(itemStack);
		StringBuilder corrosionInfo = new StringBuilder("corrosion.");
		corrosionInfo.append(getCorrosionStage(corrosion));
		corrosionInfo.replace(0, corrosionInfo.length(), StatCollector.translateToLocal(corrosionInfo.toString()));
		if (advancedItemTooltips) {
			corrosionInfo.append(" (");
			corrosionInfo.append(corrosion);
			corrosionInfo.append(TOOLTIP_PART);
		}
		lines.add(corrosionInfo.toString());
	}

	public static int getCorrosionStage(ItemStack itemStack) {
		return getCorrosionStage(getCorrosion(itemStack));
	}

	public static int getCorrosionStage(int corrosion) {
		return (10 * corrosion + 630) / 635;
	}
}
