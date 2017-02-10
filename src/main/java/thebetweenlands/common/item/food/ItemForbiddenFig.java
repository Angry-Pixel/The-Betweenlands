package thebetweenlands.common.item.food;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IDecayFood;
import thebetweenlands.util.TranslationHelper;


public class ItemForbiddenFig extends ItemBLFood implements IDecayFood {
    public ItemForbiddenFig() {
        super(20, 5.0F, false);
    }

    @Override
    public int getDecayHealAmount(ItemStack stack) {
        return 20;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        list.add(TranslationHelper.translateToLocal("fig.tooltip"));
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if (player != null) {
            if (world.isRemote) {
                player.addChatMessage(new TextComponentTranslation("chat.item.forbiddenfig"));
                //world.playSoundAtEntity(player, "thebetweenlands:fig", 0.7F, 0.8F);
            } else {
                player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 1200, 1));
                player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 1));
            }
        }
    }
}
