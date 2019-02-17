package thebetweenlands.common.network.clientbound;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.DimensionBetweenlands;

public class MessageRiftSound extends MessageBase {
	public static enum RiftSoundType {
		CREAK, OPEN;
	}
	
	private RiftSoundType type;
	
	public MessageRiftSound() { }

	public MessageRiftSound(RiftSoundType type) {
		this.type = type;
	}
	
	@Override
	public void deserialize(PacketBuffer buf) {
		this.type = RiftSoundType.values()[buf.readInt()];
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.type.ordinal());
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Dist.CLIENT) {
			this.handle();
		}

		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void handle() {
		if(Minecraft.getInstance().world != null) {
			SoundEvent sound;
			switch(this.type) {
			default:
			case CREAK:
				sound = SoundRegistry.RIFT_CREAK;
				break;
			case OPEN:
				sound = SoundRegistry.RIFT_OPEN;
				break;
			}
			
			final float pitchRange = (this.type == RiftSoundType.CREAK ? (Minecraft.getInstance().world.rand.nextFloat() * 0.3f + 0.7f) : 1.0f);
			
			Minecraft.getInstance().getSoundHandler().play(new PositionedSoundRecord(sound.getSoundName(), SoundCategory.AMBIENT, 1, 1, false, 0, ISound.AttenuationType.NONE, 0, 0, 0) {
				@Override
				public float getPitch() {
					EntityPlayer player = Minecraft.getInstance().player;
					if(player != null) {
						if(player.posY < DimensionBetweenlands.CAVE_START) {
							return (0.5F + (float)player.posY / DimensionBetweenlands.CAVE_START * 0.5F) * pitchRange;
						}
					}
					return 1;
				}
	
				@Override
				public float getVolume() {
					EntityPlayer player = Minecraft.getInstance().player;
					if(player != null) {
						if(player.posY < DimensionBetweenlands.CAVE_START) {
							return (0.15F + (float)player.posY / DimensionBetweenlands.CAVE_START * 0.85F);
						}
					}
					return 1;
				}
			});
		}
	}
}
