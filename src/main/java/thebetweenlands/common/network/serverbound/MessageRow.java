package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.network.MessageBase;

public class MessageRow extends MessageBase {
    private boolean starboard;

    private boolean port;

    private float progressStarboard;

    private float progressPort;

    public MessageRow() {}

    public MessageRow(boolean starboard, boolean port, float progressStarboard, float progressPort) {
        this.starboard = starboard;
        this.port = port;
        this.progressStarboard = progressStarboard;
        this.progressPort = progressPort;
    }

    @Override
    public void serialize(PacketBuffer buf) {
        buf.writeBoolean(starboard);
        buf.writeBoolean(port);
        buf.writeFloat(progressStarboard);
        buf.writeFloat(progressPort);
    }

    @Override
    public void deserialize(PacketBuffer buf) {
        starboard = buf.readBoolean();
        port = buf.readBoolean();
        progressStarboard = buf.readFloat();
        progressPort = buf.readFloat();
    }

    @Override
    public IMessage process(MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;
        Entity e = player.getRidingEntity();
        if (e instanceof EntityWeedwoodRowboat) {
            ((EntityWeedwoodRowboat) e).setOarStates(starboard, port, progressStarboard, progressPort);
        }
        return null;
    }
}
