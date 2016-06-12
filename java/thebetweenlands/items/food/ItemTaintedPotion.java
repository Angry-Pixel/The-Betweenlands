package thebetweenlands.items.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thebetweenlands.herblore.elixirs.ElixirEffectRegistry;
import thebetweenlands.manual.IManualEntryItem;

public class ItemTaintedPotion extends Item implements IManualEntryItem {
	public ItemTaintedPotion() {
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.drink;
	}

	@Override
	public String manualName(int meta) {
		return "taintedPotion";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[0];
	}

	@Override
	public int metas() {
		return 0;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		ItemStack originalStack = this.getOriginalStack(stack);
		if(originalStack != null && originalStack.getItem() != null) {
			return super.getItemStackDisplayName(stack) + " (" + originalStack.getDisplayName() + ")";
		}
		return super.getItemStackDisplayName(stack);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
		}

		if (!world.isRemote) {
			player.addPotionEffect(new PotionEffect(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect().getId(), 180, 3));
			player.addPotionEffect(new PotionEffect(Potion.poison.id, 120, 2));
		}

		if (!player.capabilities.isCreativeMode) {
			if (stack.stackSize <= 0) {
				return new ItemStack(Items.glass_bottle);
			}

			player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		}

		return stack;
	}

	public void setOriginalStack(ItemStack stack, ItemStack originalStack) {
		stack.setTagInfo("originalStack", originalStack.writeToNBT(new NBTTagCompound()));
	}

	public ItemStack getOriginalStack(ItemStack stack) {
		return stack.stackTagCompound != null ? ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag("originalStack")) : null;
	}
}
