package thebetweenlands.common.item.food;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;
import java.util.List;


public class ItemChiromawWing extends ItemBLFood {
    public ItemChiromawWing() {
        super(0, 0, false);
        this.setCreativeTab(BLCreativeTabs.ITEMS);
        this.setAlwaysEdible();
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if (player.hasCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null)) {
            IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
            if (FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
                cap.increaseFoodHatred(this, FoodSickness.SICK.maxHatred, FoodSickness.SICK.maxHatred);
            } else {
                player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 2));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        if (player != null && player.hasCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null)) {
            IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
            if (FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
                tooltip.add(TranslationHelper.translateToLocal("tooltip.chiromaw_wing.eat"));
            } else {
                tooltip.add(TranslationHelper.translateToLocal("tooltip.chiromaw_wing.dont_eat"));
            }
        }
    }

    @Override
    public boolean canGetSickOf(ItemStack stack) {
        return false;
    }
}
