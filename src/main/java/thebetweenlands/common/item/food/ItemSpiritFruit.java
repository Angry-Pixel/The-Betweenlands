package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemSpiritFruit extends ItemBLFood {
	public ItemSpiritFruit() {
		super(4, 1.2F, false);
		this.setAlwaysEdible();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		if (!worldIn.isRemote()) {
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
			player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
		}
	}
}