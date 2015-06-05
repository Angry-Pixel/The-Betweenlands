package thebetweenlands.network.packets;

import net.minecraft.item.Item;
import io.netty.buffer.ByteBuf;
import thebetweenlands.network.base.IPacket;
import thebetweenlands.tileentities.TileEntityAnimator;

public class PacketAnimatorProgress implements IPacket {
	public PacketAnimatorProgress() {
	}

	public int x, y, z, progress, life, slot0Size, slot0ItemID, slot0ItemMeta, slot1Size, slot2Size, itemsConsumed;
	public boolean lifeDepleted;

	public PacketAnimatorProgress(TileEntityAnimator tile) {
		this.x = tile.xCoord;
		this.y = tile.yCoord;
		this.z = tile.zCoord;
		this.progress = tile.progress;
		this.life = tile.life;
		if (tile.getStackInSlot(0) != null) {
			this.slot0Size = tile.getStackInSlot(0).stackSize;
			this.slot0ItemID = Item.getIdFromItem(tile.getStackInSlot(0).getItem());
			this.slot0ItemMeta = tile.getStackInSlot(0).getItemDamage();
		}
		if (tile.getStackInSlot(1) != null)
			this.slot1Size = tile.getStackInSlot(1).stackSize;
		if (tile.getStackInSlot(2) != null)
			this.slot2Size = tile.getStackInSlot(2).stackSize;
		this.itemsConsumed = tile.itemsConsumed;
		this.lifeDepleted = tile.lifeDepleted;
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.progress = buffer.readInt();
		this.life = buffer.readInt();
		this.slot0Size = buffer.readInt();
		this.slot0ItemID = buffer.readInt();
		this.slot0ItemMeta = buffer.readInt();
		this.slot1Size = buffer.readInt();
		this.slot2Size = buffer.readInt();
		this.itemsConsumed = buffer.readInt();
		this.lifeDepleted = buffer.readBoolean();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeInt(this.x);
		buffer.writeInt(this.y);
		buffer.writeInt(this.z);
		buffer.writeInt(this.progress);
		buffer.writeInt(this.life);
		buffer.writeInt(this.slot0Size);
		buffer.writeInt(this.slot0ItemID);
		buffer.writeInt(this.slot0ItemMeta);
		buffer.writeInt(this.slot1Size);
		buffer.writeInt(this.slot2Size);
		buffer.writeInt(this.itemsConsumed);
		buffer.writeBoolean(this.lifeDepleted);
	}
}
