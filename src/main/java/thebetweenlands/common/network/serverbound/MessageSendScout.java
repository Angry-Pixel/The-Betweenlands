package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.entity.mobs.EntityScout;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.ItemRegistry;

public class MessageSendScout extends MessageBase {
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
                if (scoutEntity instanceof EntityScout) {
                    int d = 1;
                    Vec3d initialLocation = player.getPositionVector().add(new Vec3d(0, 1, 0));
                    Vec3d location = null;
                    while (d <= 20) {
                        location = initialLocation.add(player.getLookVec().scale(d)).add(new Vec3d(0, .5f, 0));
                        if (!player.worldObj.isAirBlock(new BlockPos(location)))
                            break;
                        d++;
                    }
                    if (player.worldObj.isAirBlock(new BlockPos(location)))
                        ((EntityScout) scoutEntity).setLocation(new BlockPos(location));
                    else {
                        BlockPos loc = new BlockPos(location);
                        if (player.worldObj.isAirBlock(loc.up()))
                            ((EntityScout) scoutEntity).setLocation(loc.up());
                        else {
                            ((EntityScout) scoutEntity).setLocation(loc.add(player.getHorizontalFacing().getOpposite().getDirectionVec()));
                        }
                    }
                }
            }
        }
        return null;
    }
}
