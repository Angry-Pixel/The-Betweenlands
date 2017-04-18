package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.entity.mobs.EntityScout;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.ItemRegistry;

public class MessageReturnScout extends MessageBase {
    @Override
    public void serialize(PacketBuffer buf) {
    }

    @Override
    public void deserialize(PacketBuffer buf) {

    }

    @Override
    public IMessage process(MessageContext ctx) {
        if (ctx.getServerHandler() != null) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            ItemStack scout = null;
            for (ItemStack itemStack : player.getArmorInventoryList())
                if (itemStack != null && itemStack.getItem() == ItemRegistry.SCOUT) {
                    scout = itemStack.copy();
                    break;
                }
            if (scout != null && scout.getTagCompound() != null && scout.getTagCompound().hasKey("entity_id")) {
                Entity scoutEntity = player.worldObj.getEntityByID(scout.getTagCompound().getInteger("entity_id"));
                if (scoutEntity instanceof EntityScout)
                    ((EntityScout) scoutEntity).setLocation(null);
                else
                    spawnNewScout(player);
            } else {
                spawnNewScout(player);
            }
        }
        return null;
    }


    public void spawnNewScout(EntityPlayer player){
        EntityScout entity = new EntityScout(player.worldObj);
        entity.setLocationAndAngles(player.posX, player.posY, player.posZ, 0, 0);
        if (!player.worldObj.isRemote)
            entity.setOwnerId(player.getUniqueID());
        player.worldObj.spawnEntityInWorld(entity);
    }
}
