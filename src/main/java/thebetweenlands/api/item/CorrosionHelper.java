package thebetweenlands.api.item;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.GameruleRegistry;
import thebetweenlands.util.NBTHelper;

public class CorrosionHelper {
	/**
	 * The number of corrosion stages, used for the item model overrides
	 */
	public static final int CORROSION_STAGE_COUNT = 6;

	/**
	 * The default NBT tag for corrosion used by BL items
	 */
	public static final String ITEM_CORROSION_NBT_TAG = "Corrosion";

	/**
	 * The default NBT tag for coating used by BL items
	 */
	public static final String ITEM_COATING_NBT_TAG = "Coating";

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
	 * Returns a general modifier at the amount corrosion of the specified item
	 * @param stack
	 * @return
	 */
	public static float getModifier(ItemStack stack) {
		if(!stack.isEmpty() && stack.getItem() instanceof ICorrodible) {
			ICorrodible corrodible = (ICorrodible) stack.getItem();
			return (-0.7F * (corrodible.getCorrosion(stack) / (float)corrodible.getMaxCorrosion(stack)) + 1);
		}
		return 1.0F;
	}

	/**
	 * Returns the dig speed of an item at the amount of corrosion of the specified item
	 * @param normalStrength
	 * @param itemStack
	 * @param blockState
	 * @return
	 */
	public static float getDestroySpeed(float normalStrength, ItemStack itemStack, IBlockState blockState) {
		return normalStrength * getModifier(itemStack);
	}

	/**
	 * Returns whether the block breaking should reset, excludes corrosion changes.
	 * Only works for the default NBT tag {@link #ITEM_CORROSION_NBT_TAG}
	 * @param oldStack
	 * @param newStack
	 * @return
	 */
	public static boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return !(newStack.getItem() == oldStack.getItem() && areItemStackTagsEqual(newStack, oldStack) && (newStack.isItemStackDamageable() || newStack.getMetadata() == oldStack.getMetadata()));
	}

	/**
	 * Returns whether the item reequip animation should be played.
	 * Only works for the default NBT tag {@link #ITEM_CORROSION_NBT_TAG}
	 * @param oldStack
	 * @param newStack
	 * @return
	 */
	public static boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !(newStack.getItem() == oldStack.getItem() && areItemStackTagsEqual(newStack, oldStack) && (newStack.isItemStackDamageable() || newStack.getMetadata() == oldStack.getMetadata()));
	}

	/**
	 * Returns whether the item stack NBT is the same, excludes corrosion changes.
	 * Only works for the default NBT tag {@link #ITEM_CORROSION_NBT_TAG}
	 * @param oldStack
	 * @param newStack
	 * @return
	 */
	public static boolean areItemStackTagsEqual(ItemStack oldStack, ItemStack newStack) {
		return NBTHelper.areItemStackTagsEqual(newStack, oldStack, ImmutableList.of(ITEM_CORROSION_NBT_TAG, ITEM_COATING_NBT_TAG));
	}

	/**
	 * Returns a map with the attack damage attribute affected by the corrosion multiplier
	 * @param map
	 * @param slot
	 * @param stack
	 * @param uuid
	 * @param damageVsEntity
	 * @return
	 */
	public static Multimap<String, AttributeModifier> getAttributeModifiers(Multimap<String, AttributeModifier> map, EntityEquipmentSlot slot, ItemStack stack, UUID uuid, float damageVsEntity) {
		if(slot == EntityEquipmentSlot.MAINHAND) {
			map.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(uuid, "Tool modifier", damageVsEntity * getModifier(stack), 0));
		}
		return map;
	}

	/**
	 * Returns whether corrosion is enabled
	 * @return
	 */
	public static boolean isCorrosionEnabled() {
		return GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_CORROSION) && BetweenlandsConfig.GENERAL.useCorrosion;
	}
	
	/**
	 * Updates the corrosion on the specified item
	 * @param stack
	 * @param world
	 * @param holder
	 * @param slot
	 * @param isHeldItem
	 */
	public static void updateCorrosion(ItemStack stack, World world, Entity holder, int slot, boolean isHeldItem) {
		if (world.isRemote) {
			return;
		}
		if(!world.isRemote && holder.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && !(holder instanceof EntityPlayer && ((EntityPlayer)holder).isCreative())) {
			if(!stack.isEmpty() && stack.getItem() instanceof ICorrodible) {
				ICorrodible corrodible = (ICorrodible) stack.getItem();
				int corrosion = corrodible.getCorrosion(stack);
				if(!isCorrosionEnabled()) {
					if(corrosion != 0) {
						corrodible.setCorrosion(stack, 0);
					}
				} else if (corrosion < corrodible.getMaxCorrosion(stack)) {
					float probability = holder.isInWater() ? 0.0014F : 0.0007F;
					if (holder instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) holder;
						probability *= (isHeldItem && !player.getActiveItemStack().isEmpty() ? 2.8F : 1.0F);
						if(player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
							IDecayCapability decay = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
							float playerCorruption = decay.getDecayStats().getDecayLevel() / 20.0F;
							probability *= (1 - Math.pow(playerCorruption, 2) * 0.9F);
						}
					}
					if (world.rand.nextFloat() < probability) {
						int coating = corrodible.getCoating(stack);
						if(coating > 0) {
							corrodible.setCoating(stack, coating - 1);
						} else {
							corrodible.setCorrosion(stack, corrosion + 1);
						}
					}
				}
			}
		}
	}

	/**
	 * Adds the corrosion tooltips
	 * @param stack
	 * @param lines
	 * @param advancedItemTooltips
	 */
	public static void addCorrosionTooltips(ItemStack stack, List<String> lines, boolean advancedItemTooltips) {
		if(!stack.isEmpty() && stack.getItem() instanceof ICorrodible) {
			ICorrodible corrodible = (ICorrodible) stack.getItem();
			int corrosion = corrodible.getCorrosion(stack);
			int coating = corrodible.getCoating(stack);
			
			if(isCorrosionEnabled()) {
				StringBuilder corrosionInfo = new StringBuilder("tooltip.corrosion.");
				corrosionInfo.append(getCorrosionStage(corrosion));
				corrosionInfo.replace(0, corrosionInfo.length(), I18n.format(corrosionInfo.toString()));
				if (advancedItemTooltips) {
					corrosionInfo.append(" (");
					corrosionInfo.append(corrosion);
					corrosionInfo.append("/" + corrodible.getMaxCorrosion(stack) + ")");
				}
				lines.add(corrosionInfo.toString());
			}

			StringBuilder coatingInfo = new StringBuilder("tooltip.coated.");
			coatingInfo.append(coating / 120);
			coatingInfo.replace(0, coatingInfo.length(), I18n.format(coatingInfo.toString()));
			if(coating > 0 || advancedItemTooltips) {
				if (advancedItemTooltips) {
					coatingInfo.append(" (");
					coatingInfo.append(coating);
					coatingInfo.append("/" + corrodible.getMaxCoating(stack) + ")");
				}
				lines.add(coatingInfo.toString());
			}
		}
	}

	/**
	 * Returns the corrosion stage of the specified item. Ranges from [0, 5]
	 * @param stack
	 * @return
	 */
	public static int getCorrosionStage(ItemStack stack) {
		if(!stack.isEmpty() && stack.getItem() instanceof ICorrodible) {
			ICorrodible corrodible = (ICorrodible) stack.getItem();
			return getCorrosionStage(corrodible.getCorrosion(stack));
		}
		return 0;
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
