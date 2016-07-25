package thebetweenlands.common.message.clientbound;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.message.BLMessage;
import thebetweenlands.common.tile.TileEntityDruidAltar;

public class MessageDruidAltarProgress extends BLMessage {
    public BlockPos pos;
    public int progress;

    public MessageDruidAltarProgress() {}

    public MessageDruidAltarProgress(TileEntityDruidAltar tile) {
        this.pos = tile.getPos();
        this.progress = tile.craftingProgress;
    }

    public MessageDruidAltarProgress(TileEntityDruidAltar tile, int progress) {
        this.pos = tile.getPos();
        this.progress = progress;
    }

    @Override
    public void serialize(PacketBuffer buffer) {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.progress);
    }

    @Override
    public void deserialize(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.progress = buffer.readInt();
    }

	@Override
	public IMessage process(MessageContext ctx) {
		World world = FMLClientHandler.instance().getWorldClient();
		TileEntity te = world.getTileEntity(this.pos);
        if (te instanceof TileEntityDruidAltar) {
            TileEntityDruidAltar altar = (TileEntityDruidAltar) te;
            if (this.progress >= 0) {
                altar.craftingProgress = this.progress;
            }
        }
		return null;
	}
}
