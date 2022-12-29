package thebetweenlands.common.item.food;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;

public class ItemTaintedPotion extends Item {
	public ItemTaintedPotion() {
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setCreativeTab(null);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		ItemStack originalStack = this.getOriginalStack(stack);
		if(!originalStack.isEmpty() && originalStack.getItem() != Items.AIR) {
			return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".name", originalStack.getRarity().color + originalStack.getDisplayName() + TextFormatting.RESET).trim();
		}
		return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + "_empty.name").trim();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		playerIn.setActiveHand(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}

	@Override
	@Nullable
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
		EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;

		if (player == null || !player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}

		if (!world.isRemote) {
			player.addPotionEffect(new PotionEffect(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect(), 180, 3));
			player.addPotionEffect(new PotionEffect(MobEffects.POISON, 120, 2));
		}

		if (player != null) {
			player.addStat(StatList.getObjectUseStats(this));
		}

		if (player == null || !player.capabilities.isCreativeMode) {
			if (stack.getCount() <= 0) {
				return new ItemStack(Items.GLASS_BOTTLE);
			}

			if (player != null) {
				player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		return stack;
	}

	public void setOriginalStack(ItemStack stack, ItemStack originalStack) {
		stack.setTagInfo("originalStack", originalStack.writeToNBT(new NBTTagCompound()));
	}

	public ItemStack getOriginalStack(ItemStack stack) {
		return stack.getTagCompound() != null ? new ItemStack(stack.getTagCompound().getCompoundTag("originalStack")) : ItemStack.EMPTY;
	}
}
