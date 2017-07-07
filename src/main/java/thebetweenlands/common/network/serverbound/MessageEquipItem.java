package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.CapabilityRegistry;

public class MessageEquipItem extends MessageEntity {
	private int sourceSlot, mode;
	private EnumEquipmentInventory inventory;

	public MessageEquipItem() { }

	/**
	 * Creates a message to equip an item
	 * @param sourceSlot
	 * @param target
	 * @param inventory
	 * @param destSlot
	 */
	public MessageEquipItem(int sourceSlot, Entity target) {
		this.addEntity(target);
		this.sourceSlot = sourceSlot;
		this.mode = 0;
	}

	/**
	 * Creates a message to unequip an item
	 * @param target
	 * @param destInv
	 * @param destSlot
	 */
	public MessageEquipItem(Entity target, EnumEquipmentInventory inventory, int slot) {
		this.addEntity(target);
		this.sourceSlot = slot;
		this.inventory = inventory;
		this.mode = 1;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);

		buf.writeInt(this.mode);

		switch(this.mode) {
		default:
		case 0:
			buf.writeInt(this.sourceSlot);
			break;

		case 1:
			buf.writeInt(this.sourceSlot);
			buf.writeInt(this.inventory.id);
			break;
		}
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		super.deserialize(buf);

		this.mode = buf.readInt();

		switch(this.mode) {
		default:
		case 0:
			this.sourceSlot = buf.readInt();
			break;

		case 1:
			this.sourceSlot = buf.readInt();
			this.inventory = EnumEquipmentInventory.fromID(buf.readInt());
			break;
		}
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.getServerHandler() != null) {
			EntityPlayer sender = ctx.getServerHandler().player;
			Entity target = this.getEntity(0);

			if(target != null && target.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
				switch(this.mode) {
				default:
				case 0:
					//Equip
					if(this.sourceSlot >= 0 && this.sourceSlot < sender.inventory.getSizeInventory()) {
						ItemStack stack = sender.inventory.getStackInSlot(this.sourceSlot);
						ItemStack result = EquipmentHelper.equipItem(sender, target, stack, false);
						if(!sender.capabilities.isCreativeMode) {
							sender.inventory.setInventorySlotContents(this.sourceSlot, result);
						}
					}
					break;
				case 1:
					//Unequip
					if(this.sourceSlot >= 0) {
						ItemStack stack = EquipmentHelper.unequipItem(sender, target, this.inventory, this.sourceSlot, false);
						if(stack != null && !sender.inventory.addItemStackToInventory(stack)) {
							target.entityDropItem(stack, target.getEyeHeight());
						}
					}
					break;
				}
			}
		}

		return null;
	}
}
