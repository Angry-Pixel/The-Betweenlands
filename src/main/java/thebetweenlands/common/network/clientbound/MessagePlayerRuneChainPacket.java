package thebetweenlands.common.network.clientbound;

import java.io.IOException;
import java.util.function.Consumer;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.api.capability.IRuneChainCapability;
import thebetweenlands.api.capability.IRuneChainUserCapability;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.CapabilityRegistry;

public class MessagePlayerRuneChainPacket extends MessageEntity {
	private EntityPlayer player;
	private int runeChainId;
	private Consumer<PacketBuffer> serializer;

	private PacketBuffer inputsBuffer;

	public MessagePlayerRuneChainPacket() { }

	public MessagePlayerRuneChainPacket(EntityPlayer player, int runeChainId, Consumer<PacketBuffer> serializer) {
		this.addEntity(player);
		this.player = player;
		this.runeChainId = runeChainId;
		this.serializer = serializer;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeVarInt(this.runeChainId);
		PacketBuffer inputsBuffer = new PacketBuffer(Unpooled.buffer());;
		this.serializer.accept(inputsBuffer);
		int bytes = inputsBuffer.writerIndex();
		buf.writeVarInt(bytes);
		buf.writeBytes(inputsBuffer, bytes);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		super.deserialize(buf);
		this.runeChainId = buf.readVarInt();
		this.inputsBuffer = new PacketBuffer(Unpooled.buffer());
		int bytes = buf.readVarInt();
		buf.readBytes(this.inputsBuffer, bytes);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT) {
			Entity entity = this.getEntity(0);

			if(entity instanceof EntityPlayer) {
				IRuneChainUserCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null);

				if(cap != null) {
					//TODO Temporary for testing until running rune chain compositions are synced to clients
					ItemStack stack = ((EntityPlayer) entity).getHeldItemMainhand();
					if(!stack.isEmpty()) {
						IRuneChainCapability chainCap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);
						if(chainCap != null) {
							try {
								chainCap.getBlueprint().create().processPacket(cap.getUser(), this.inputsBuffer);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					/*RuneChainComposition runeChain = cap.getRuneChain(this.runeChainId);

					if(runeChain != null) {
						runeChain.processPacket(cap.getUser(), this.inputsBuffer);
					}*/
				}
			}
		}

		return null;
	}
}
