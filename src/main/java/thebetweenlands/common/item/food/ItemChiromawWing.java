package thebetweenlands.common.item.food;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.TranslationHelper;


public class ItemChiromawWing extends ItemBLFood {
	public ItemChiromawWing() {
		super(0, 0, false);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
		this.setAlwaysEdible();
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);

		if(player != null && player.hasCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null)) {
			IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
			if(FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
				cap.increaseFoodHatred(this, 64, 32);
			} else {
				player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 2));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
		if(player != null && player.hasCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null)) {
			IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
			if(FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
				list.add(TranslationHelper.translateToLocal("tooltip.chiromawWing.eat"));
			} else {
				list.add(TranslationHelper.translateToLocal("tooltip.chiromawWing.dontEat"));
			}
		}
	}

	@Override
	public boolean canGetSickOf(ItemStack stack) {
		return false;
	}
}
