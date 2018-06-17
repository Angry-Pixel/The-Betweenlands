package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class MessageRiftOpenSound extends MessageBase {
	public MessageRiftOpenSound() { }

	@Override
	public void deserialize(PacketBuffer buf) { }

	@Override
	public void serialize(PacketBuffer buf) { }

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handle();
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(SoundRegistry.RIFT_OPEN.getSoundName(), SoundCategory.AMBIENT, 1, 1, false, 0, ISound.AttenuationType.NONE, 0, 0, 0) {
			@Override
			public float getPitch() {
				EntityPlayer player = Minecraft.getMinecraft().player;
				if(player != null) {
					if(player.posY < WorldProviderBetweenlands.CAVE_START) {
						return 0.5F + (float)player.posY / WorldProviderBetweenlands.CAVE_START * 0.5F;
					}
				}
				return 1;
			}

			@Override
			public float getVolume() {
				EntityPlayer player = Minecraft.getMinecraft().player;
				if(player != null) {
					if(player.posY < WorldProviderBetweenlands.CAVE_START) {
						return 0.15F + (float)player.posY / WorldProviderBetweenlands.CAVE_START * 0.85F;
					}
				}
				return 1;
			}
		});
	}
}
