package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.api.item.IExtendedReach;
import thebetweenlands.common.network.MessageBase;

public class MessageExtendedReach extends MessageBase {

    private int entityId;

    public MessageExtendedReach() {}

    public MessageExtendedReach(Entity entity) {
        entityId = entity.getEntityId();
    }

    @Override
    public void serialize(PacketBuffer buf) {
        buf.writeInt(entityId);
    }

    @Override
    public void deserialize(PacketBuffer buf) {
        entityId = buf.readInt();
    }

    @Override
    public IMessage process(MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;
        Entity entity = player.getEntityWorld().getEntityByID(entityId);
        if (entity != null && entity.isEntityAlive() && player.hasItemInSlot(EntityEquipmentSlot.MAINHAND) && player.getHeldItemMainhand().getItem() instanceof IExtendedReach) {
            double reach = ((IExtendedReach) player.getHeldItemMainhand().getItem()).getReach();
            if (reach * reach >= player.getDistanceSq(entity)) {
                player.attackTargetEntityWithCurrentItem(entity);
            }
        }
        return null;
    }
}
