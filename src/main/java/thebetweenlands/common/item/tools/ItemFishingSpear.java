package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityFishingSpear;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.TranslationHelper;

public class ItemFishingSpear extends Item implements IAnimatorRepairable{
	public final byte type;
	
	public ItemFishingSpear(byte type) {
		maxStackSize = 1;
		this.type = type;

		if(type == 2)
			setMaxDamage(128);
		else
			setMaxDamage(64);

		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (entityIn == null) {
					return 0.0F;
				} else {
					return !(entityIn.getActiveItemStack().getItem() instanceof ItemFishingSpear) ? 0.0F : (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F;
				}
			}
		});

		this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});
		setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
		if (type == 2 && stack.hasTagCompound() && stack.getTagCompound().hasKey("animated")) {
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fishing_spear_animated") + stack.getTagCompound().getBoolean("animated"));
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeldItem) {
		if(!world.isRemote) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			if (!stack.getTagCompound().hasKey("animated"))
				stack.getTagCompound().setBoolean("animated", false);
		}
	}

	public boolean isAnimated(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("animated") && stack.getTagCompound().getBoolean("animated");
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;

			int i = getMaxItemUseDuration(stack) - timeLeft;
			i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, entityplayer, i, !stack.isEmpty() || flag);
			if (i < 0)
				return;

			if (!stack.isEmpty() || flag) {
				float f = ItemBow.getArrowVelocity(i) / 2;

				if ((double) f >= 0.1D) {
					if (!worldIn.isRemote) {
						EntityFishingSpear entitySpear = new EntityFishingSpear(worldIn, entityplayer);
						entitySpear.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 1.0F, f * 3.0F, 1.0F);
						entitySpear.setItemStackDamage(stack.getItemDamage());
						entitySpear.setType(type);
						entitySpear.setAnimated(isAnimated(stack));
						switch(type) {
						case 0:
							entitySpear.setDamage(2);
							break;
						case 1:
							entitySpear.setDamage(3);
							break;
						case 2:
							entitySpear.setDamage(4);
							break;
						}
						worldIn.spawnEntity(entitySpear);
					}
					worldIn.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundRegistry.SPEAR_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

					if (!entityplayer.capabilities.isCreativeMode) {
						stack.shrink(1);
						if (stack.isEmpty())
							entityplayer.inventory.deleteStack(stack);
					}
					entityplayer.addStat(StatList.getObjectUseStats(this));
				}
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		player.setActiveHand(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
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
