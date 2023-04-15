package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ScreenRenderHandler;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.SoundRegistry;

public class MessageInfectionIncrease extends MessageBase {
	private float amount;

	public MessageInfectionIncrease() { }

	public MessageInfectionIncrease(float amount) {
		this.amount = amount;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.amount = buf.readFloat();
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeFloat(this.amount);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handle();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player != null) {
			player.world.playSound(player, player.posX, player.posY, player.posZ, SoundRegistry.INFECTION_SPREAD, SoundCategory.PLAYERS, 0.75f + 1.0f * MathHelper.clamp(this.amount * 10.0f, 0.0f, 1.0f), 1);
			ScreenRenderHandler.INSTANCE.infectionOverlayRenderer.onInfectionIncrease(this.amount);
		}
	}
}
