package thebetweenlands.common.item.corrosion;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.capability.corrosion.ICorrosionCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public final class CorrosionHelper {
	public static final int MAX_CORROSION = 255;
	public static final String TOOLTIP_PART = "/" + MAX_CORROSION + ")";
	public static final int CORROSION_STAGE_COUNT = 6;

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
		if(itemStack.hasCapability(CapabilityRegistry.CAPABILITY_CORROSION, null)) {
			ICorrosionCapability capability = itemStack.getCapability(CapabilityRegistry.CAPABILITY_CORROSION, null);
			return capability.getCoating();
		}
		return 0;
	}

	/**
	 * Returns the amount of corrosion on the specified item
	 * @param itemStack
	 * @return
	 */
	public static int getCorrosion(ItemStack itemStack) {
		if(itemStack.hasCapability(CapabilityRegistry.CAPABILITY_CORROSION, null)) {
			ICorrosionCapability capability = itemStack.getCapability(CapabilityRegistry.CAPABILITY_CORROSION, null);
			return capability.getCorrosion();
		}
		return 0;
	}

	/**
	 * Sets the amount of coating of the specified item
	 * @param itemStack
	 * @param coating
	 */
	public static void setCoating(ItemStack itemStack, int coating) {
		if(itemStack.hasCapability(CapabilityRegistry.CAPABILITY_CORROSION, null)) {
			ICorrosionCapability capability = itemStack.getCapability(CapabilityRegistry.CAPABILITY_CORROSION, null);
			capability.setCoating(coating);
		}
	}

	/**
	 * Sets the amount of corrosion of the specified item
	 * @param itemStack
	 * @param corrosion
	 */
	public static void setCorrosion(ItemStack itemStack, int corrosion) {
		if(itemStack.hasCapability(CapabilityRegistry.CAPABILITY_CORROSION, null)) {
			ICorrosionCapability capability = itemStack.getCapability(CapabilityRegistry.CAPABILITY_CORROSION, null);
			capability.setCorrosion(corrosion);
		}
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
	 * @param normalDigSpeed
	 * @param itemStack
	 * @param block
	 * @param meta
	 * @return
	 */
	public static float getDigSpeed(float normalDigSpeed, ItemStack itemStack, Block block, int meta) {
		return normalDigSpeed * getModifier(itemStack);
	}

	/*public static Multimap getAttributeModifiers(ItemStack stack, UUID uuid, float damageVsEntity) {
		Multimap multimap = HashMultimap.create();
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(uuid, "Tool modifier", damageVsEntity * getModifier(stack), 0));
		return multimap;
	}*/

	/*public static void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		if (world.isRemote || !BLGamerules.getGameRuleBooleanValue(BLGamerules.BL_CORROSION)) {
			return;
		}
		if(world.provider instanceof WorldProviderBetweenlands) {
			int corrosion = getCorrosion(itemStack);
			if (corrosion < MAX_CORROSION) {
				float probability = holder.isInWater() ? 0.0014F : 0.0007F;
				if (holder instanceof EntityPlayer) {
					probability *= ((((EntityPlayer) holder).isUsingItem() || ((EntityPlayer) holder).isSwingInProgress) && isHeldItem) ? 2.8F : 1.0F;
					float playerCorruption = DecayManager.getCorruptionLevel((EntityPlayer) holder) / 10F;
					probability *= (1 - Math.pow(playerCorruption, 2) * 0.9F);
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
	}*/

	/*public static void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		int corrosion = getCorrosion(itemStack);
		int coating = getCoating(itemStack);
		StringBuilder corrosionInfo = new StringBuilder("corrosion.");
		corrosionInfo.append(getCorrosionStage(corrosion));
		corrosionInfo.replace(0, corrosionInfo.length(), StatCollector.translateToLocal(corrosionInfo.toString()));
		if (advancedItemTooltips) {
			corrosionInfo.append(" (");
			corrosionInfo.append(corrosion);
			corrosionInfo.append(TOOLTIP_PART);
		}
		lines.add(corrosionInfo.toString());
		if(coating > 0) {
			lines.add(StatCollector.translateToLocal("tooltip.coated"));
		}
	}*/

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
