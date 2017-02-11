package thebetweenlands.common.item.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IBLBoss;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class ItemVoodooDoll extends Item {
    public ItemVoodooDoll() {
        maxStackSize = 1;
        setMaxDamage(24);
        setCreativeTab(BLCreativeTabs.SPECIALS);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(player.posX, player.posY, player.posZ, player.posX, player.posY, player.posZ).expand(5, 5, 5));
        living.remove(player);
        boolean attacked = false;
        for (EntityLivingBase entity : living) {
            if (entity.isEntityAlive() && !(entity instanceof IBLBoss)) {
                if (!world.isRemote) {
                    attacked |= entity.attackEntityFrom(DamageSource.magic, 20);
                } else if (!entity.isEntityInvulnerable(DamageSource.magic)) {
                    attacked = true;
                    for (int i = 0; i < 20; i++)
                        BLParticles.SWAMP_SMOKE.spawn(world, entity.posX, entity.posY + entity.height / 2.0D, entity.posZ, ParticleFactory.ParticleArgs.get().withMotion((world.rand.nextFloat() - 0.5F) * 0.5F, (world.rand.nextFloat() - 0.5F) * 0.5F, (world.rand.nextFloat() - 0.5F) * 0.5F).withColor(1, 1, 1, 1));
                }
            }
        }
        if (!world.isRemote) {
            if (living.isEmpty()) {
                player.addChatMessage(new TextComponentTranslation("chat.voodoo.no.mobs"));
            } else if (attacked) {
                stack.damageItem(1, player);
                world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.VOODOO_DOLL, SoundCategory.PLAYERS, 0.5F, 1.0F - world.rand.nextFloat() * 0.3F);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

}
