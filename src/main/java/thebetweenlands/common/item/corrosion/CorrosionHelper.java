package thebetweenlands.common.item.corrosion;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.capability.decay.IDecayCapability;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.NBTHelper;
import thebetweenlands.util.config.ConfigHandler;

public final class CorrosionHelper {
	public static final int MAX_CORROSION = 255;
	public static final String TOOLTIP_PART = "/" + MAX_CORROSION + ")";
	public static final int CORROSION_STAGE_COUNT = 6;
	public static final String ITEM_CORROSION_NBT_TAG = "Corrosion";
	public static final String ITEM_COATING_NBT_TAG = "Coating";


	private CorrosionHelper() {
	}

	/**
	 * Adds the corrosion property overrides to the specified item
	 * @param item
	 */
	public static void addCorrosionPropertyOverrides(Item item) {
		item.addPropertyOverride(new ResourceLocation("corrosion"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return getCorrosionStage(stack);
			}
		});
	}

	/**
	 * Returns the amount of coating on the specified item
	 * @param itemStack
	 * @return
	 */
	public static int getCoating(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		if(nbt != null && nbt.hasKey(ITEM_COATING_NBT_TAG, Constants.NBT.TAG_INT)) {
			return nbt.getInteger(ITEM_COATING_NBT_TAG);
		}
		return 0;
	}

	/**
	 * Returns the amount of corrosion on the specified item
	 * @param itemStack
	 * @return
	 */
	public static int getCorrosion(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		if(nbt != null && nbt.hasKey(ITEM_CORROSION_NBT_TAG, Constants.NBT.TAG_INT)) {
			return nbt.getInteger(ITEM_CORROSION_NBT_TAG);
		}
		return 0;
	}

	/**
	 * Sets the amount of coating of the specified item
	 * @param itemStack
	 * @param coating
	 */
	public static void setCoating(ItemStack itemStack, int coating) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(itemStack);
		nbt.setInteger(ITEM_COATING_NBT_TAG, coating);
	}

	/**
	 * Sets the amount of corrosion of the specified item
	 * @param itemStack
	 * @param corrosion
	 */
	public static void setCorrosion(ItemStack itemStack, int corrosion) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(itemStack);
		nbt.setInteger(ITEM_CORROSION_NBT_TAG, corrosion);
	}

	/**
	 * Returns a general modifier at the amount corrosion of the specified item
	 * @param itemStack
	 * @return
	 */
	public static float getModifier(ItemStack itemStack) {
		return (-0.7F * (getCorrosion(itemStack) / (float) MAX_CORROSION) + 1);
	}

	/**
	 * Returns the dig speed of an item at the amount of corrosion of the specified item
	 * @param normalStrength
	 * @param itemStack
	 * @param block
	 * @param meta
	 * @return
	 */
	public static float getStrVsBlock(float normalStrength, ItemStack itemStack, IBlockState blockState) {
		return normalStrength * getModifier(itemStack);
	}

	/**
	 * Returns whether the block breaking should reset, excludes corrosion changes
	 * @param oldStack
	 * @param newStack
	 * @return
	 */
	public static boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return !(newStack.getItem() == oldStack.getItem() && areItemStackTagsEqual(newStack, oldStack) && (newStack.isItemStackDamageable() || newStack.getMetadata() == oldStack.getMetadata()));
	}

	/**
	 * Returns whether the item reequip animation should be played
	 * @param oldStack
	 * @param newStack
	 * @return
	 */
	public static boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !(newStack.getItem() == oldStack.getItem() && areItemStackTagsEqual(newStack, oldStack) && (newStack.isItemStackDamageable() || newStack.getMetadata() == oldStack.getMetadata()));
	}

	/**
	 * Returns whether the item stack NBT is the same, excludes corrosion changes
	 * @param oldStack
	 * @param newStack
	 * @return
	 */
	public static boolean areItemStackTagsEqual(ItemStack oldStack, ItemStack newStack) {
		return NBTHelper.areItemStackTagsEqual(newStack, oldStack, ImmutableList.of("ForgeCaps.thebetweenlands:item_corrosion"));
	}

	public static Multimap<String, AttributeModifier> getAttributeModifiers(Multimap<String, AttributeModifier> map, EntityEquipmentSlot slot, ItemStack stack, UUID uuid, float damageVsEntity) {
		if(slot == EntityEquipmentSlot.MAINHAND) {
			map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(uuid, "Tool modifier", damageVsEntity * getModifier(stack), 0));
		}
		return map;
	}

	public static void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		/*if (world.isRemote || !BLGamerules.getGameRuleBooleanValue(BLGamerules.BL_CORROSION)) { TODO: BLGamerules
			return;
		}*/
		if(!world.isRemote && holder.dimension == ConfigHandler.dimensionId && !(holder instanceof EntityPlayer && ((EntityPlayer)holder).isCreative())) {
			int corrosion = getCorrosion(itemStack);
			if (corrosion < MAX_CORROSION) {
				float probability = holder.isInWater() ? 0.0014F : 0.0007F;
				if (holder instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) holder;
					probability *= (isHeldItem && player.getActiveItemStack() != null ? 2.8F : 1.0F);
					if(player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
						IDecayCapability decay = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
						float playerCorruption = decay.getDecayStats().getDecayLevel() / 20.0F;
						probability *= (1 - Math.pow(playerCorruption, 2) * 0.9F);
					}
				}
				if (world.rand.nextFloat() < probability) {
					int coating = getCoating(itemStack);
					if(coating > 0) {
						setCoating(itemStack, coating - 1);
					} else {
						setCorrosion(itemStack, corrosion + 1);
					}
				}
			}
		}
	}

	public static void addInformation(ItemStack itemStack, EntityPlayer player, List<String> lines, boolean advancedItemTooltips) {
		int corrosion = getCorrosion(itemStack);
		int coating = getCoating(itemStack);
		StringBuilder corrosionInfo = new StringBuilder("tooltip.corrosion.");
		corrosionInfo.append(getCorrosionStage(corrosion));
		corrosionInfo.replace(0, corrosionInfo.length(), I18n.format(corrosionInfo.toString()));
		if (advancedItemTooltips) {
			corrosionInfo.append(" (");
			corrosionInfo.append(corrosion);
			corrosionInfo.append(TOOLTIP_PART);
		}
		lines.add(corrosionInfo.toString());
		if(coating > 0) {
			lines.add(I18n.format("tooltip.coated"));
		}
	}

	/**
	 * Returns the corrosion stage of the specified item. Ranges from [0, 5]
	 * @param itemStack
	 * @return
	 */
	public static int getCorrosionStage(ItemStack itemStack) {
		return getCorrosionStage(getCorrosion(itemStack));
	}

	/**
	 * Returns the corrosion stage for the specified amount of corrosion
	 * @param corrosion
	 * @return
	 */
	public static int getCorrosionStage(int corrosion) {
		return (10 * corrosion + 630) / 635;
	}
}
