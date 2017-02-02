package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemRottenFood extends ItemBLFood {
	public ItemRottenFood() {
		super(-1, -1.0F, false);
		this.setCreativeTab(null);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);

		if (player != null) {
			player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 200, 1));
			player.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 1));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		ItemStack originalStack = this.getOriginalStack(stack);
		if (originalStack != null && originalStack.getItem() != null) {
			return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".name", originalStack.getDisplayName()).trim();
		}
		return super.getItemStackDisplayName(stack);
	}

	public void setOriginalStack(ItemStack stack, ItemStack originalStack) {
		stack.setTagInfo("originalStack", originalStack.writeToNBT(new NBTTagCompound()));
	}

	public ItemStack getOriginalStack(ItemStack stack) {
		return stack.getTagCompound() != null ? ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("originalStack")) : null;
	}

	@Override
	public boolean canGetSickOf(ItemStack stack) {
		return false;
	}
}
