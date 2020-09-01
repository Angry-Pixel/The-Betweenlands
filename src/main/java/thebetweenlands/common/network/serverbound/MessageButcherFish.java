package thebetweenlands.common.network.serverbound;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.tile.TileEntityFishTrimmingTable;

public class MessageButcherFish extends MessageBase {
	public int entityId;
	public BlockPos tilePos;

	public MessageButcherFish() {

	}

	public MessageButcherFish(EntityPlayer player, BlockPos pos) {
		entityId = player.getEntityId();
		tilePos = pos;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.entityId);
		writeBlockPos(buf, tilePos);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		entityId = buf.readInt();
		tilePos = readBlockPos(buf);
	}

	public BlockPos readBlockPos(ByteBuf buf) {
		return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	public void writeBlockPos(ByteBuf buf, BlockPos pos) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			if (ctx.getServerHandler().player.getEntityId() == entityId) {
				final EntityPlayerMP player = ctx.getServerHandler().player;
				player.getServer().addScheduledTask(new Runnable() {
					public void run() {
						TileEntityFishTrimmingTable tile = (TileEntityFishTrimmingTable) player.world.getTileEntity(tilePos);
						if (tile != null && tile.hasAnadia() && tile.hasChopper() && tile.allResultSlotsEmpty()) {

							// set slot contents 1, 2, 3 to butcher items
							tile.getItems().set(1, tile.getSlotresult(1));
							tile.getItems().set(2, tile.getSlotresult(2));
							tile.getItems().set(3, tile.getSlotresult(3));

							// set slot contents 4 to guts
							tile.getItems().set(4, tile.getSlotresult(4));

							// damage axe
							tile.getItems().get(5).damageItem(1, player);
							
							// set slot contents 0 to empty last so logic works in order
							tile.getItems().set(0, tile.getSlotresult(0));
						}
					}
				});
			}
		}
		return null;
	}
}
