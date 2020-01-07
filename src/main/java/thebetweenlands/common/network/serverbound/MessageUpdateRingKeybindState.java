package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.item.equipment.ItemRing;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.CapabilityRegistry;

public class MessageUpdateRingKeybindState extends MessageBase {
	private boolean active;
	private int number;

	public MessageUpdateRingKeybindState() { }

	public MessageUpdateRingKeybindState(boolean active, int number) {
		this.active = active;
		this.number = number;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBoolean(this.active);
		buf.writeVarInt(this.number);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.active = buf.readBoolean();
		this.number = buf.readVarInt();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null && this.number >= 0) {
			EntityPlayer player = ctx.getServerHandler().player;

			IEquipmentCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			if(cap != null) {
				IInventory inv = cap.getInventory(EnumEquipmentInventory.RING);

				if(this.number < inv.getSizeInventory()) {
					int ringCount = 0;

					for(int i = 0; i < inv.getSizeInventory(); i++) {
						ItemStack stack = inv.getStackInSlot(i);

						if(!stack.isEmpty() && stack.getItem() instanceof ItemRing) {
							if(ringCount == this.number) {
								((ItemRing) stack.getItem()).onKeybindState(player, stack, inv, this.active);
								break;
							}

							ringCount++;
						}
					}
				}
			}
		}
		return null;
	}

}
