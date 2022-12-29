package thebetweenlands.common.item.food;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.network.clientbound.MessageShowFoodSicknessLine;
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

		IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
		if (!world.isRemote && cap != null) {
			if (FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
				cap.increaseFoodHatred(this, FoodSickness.SICK.maxHatred, FoodSickness.SICK.maxHatred);
				if(player instanceof EntityPlayerMP) {
					TheBetweenlands.networkWrapper.sendTo(new MessageShowFoodSicknessLine(stack, FoodSickness.SICK), (EntityPlayerMP) player);
				}
			} else {
				player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 2));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
		if (player != null) {
			IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
			if (cap != null) {
				if (FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
					tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.chiromaw_wing.eat"));
				} else {
					tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.chiromaw_wing.dont_eat"));
				}
			}
		}
	}

	@Override
	public boolean canGetSickOf(EntityPlayer player, ItemStack stack) {
		return false;
	}
}
