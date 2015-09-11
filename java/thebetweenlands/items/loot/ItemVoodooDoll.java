package thebetweenlands.items.loot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Bart on 8-7-2015.
 */
public class ItemVoodooDoll extends Item {

    public ItemVoodooDoll() {
        this.maxStackSize = 1;
        this.setUnlocalizedName("thebetweenlands.voodooDoll");
        setTextureName("thebetweenlands:voodooDoll");
    }


    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ, player.posX, player.posY, player.posZ).expand(5, 5, 5));
            int i = 0;
            for (EntityLivingBase entity : living) {
                if (entity != player) {
                    i++;
                    entity.attackEntityFrom(DamageSource.magic, 20f);
                    stack.setItemDamage(stack.getItemDamage() + 1);
                }
            }
            if (i == 0)
                player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("voodoo.no.mobs")));
            return stack;
        }
        return stack;
    }
}
