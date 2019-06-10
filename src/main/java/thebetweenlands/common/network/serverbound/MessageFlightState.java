package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.IFlightCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class MessageFlightState extends MessageBase {
	private boolean state;

	public MessageFlightState() { }

	public MessageFlightState(boolean state) {
		this.state = state;
	}

	@Override
	public void deserialize(PacketBuffer buffer) {
		this.state = buffer.readBoolean();
	}

	@Override
	public void serialize(PacketBuffer buffer) {
		buffer.writeBoolean(this.state);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			IFlightCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FLIGHT, null);
			if(cap != null) {
				boolean canPlayerFly = false;

				IEquipmentCapability equipmentCap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
				if(equipmentCap != null) {
					IInventory inv = equipmentCap.getInventory(EnumEquipmentInventory.RING);
					for(int i = 0; i < inv.getSizeInventory(); i++) {
						ItemStack stack = inv.getStackInSlot(i);
						if(!stack.isEmpty() && stack.getItem() == ItemRegistry.RING_OF_FLIGHT) {
							canPlayerFly = true;
							break;
						}
					}
				}

				if(canPlayerFly && this.state) {
					cap.setFlying(this.state);
				} else if(!this.state) {
					cap.setFlying(false);
				}
			}
		}

		return null;
	}
}
