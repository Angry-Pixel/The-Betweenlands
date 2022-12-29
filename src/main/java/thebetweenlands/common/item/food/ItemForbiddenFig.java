package thebetweenlands.common.item.food;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IDecayFood;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.TranslationHelper;


public class ItemForbiddenFig extends ItemBLFood implements IDecayFood {
    public ItemForbiddenFig() {
        super(20, 0.6F, false);
    }

    @Override
    public int getDecayHealAmount(ItemStack stack) {
        return 20;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fig"));
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if (world.isRemote) {
            player.sendStatusMessage(new TextComponentTranslation("chat.item.forbiddenfig"), true);
            world.playSound(player, player.posX, player.posY, player.posZ, SoundRegistry.FIG, SoundCategory.AMBIENT, 0.7F, 0.8F);
        } else {
            player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 1200, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 1));
        }
    }
    
    @Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}
