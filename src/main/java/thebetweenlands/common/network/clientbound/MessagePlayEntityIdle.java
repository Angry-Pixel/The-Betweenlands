package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.audio.EntitySound;
import thebetweenlands.common.network.MessageEntity;

public class MessagePlayEntityIdle extends MessageEntity {
	private SoundEvent sound;
	private SoundCategory category;
	private float soundVolume;
	private float soundPitch;

	public MessagePlayEntityIdle() { }

	public MessagePlayEntityIdle(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this.addEntity(entity);
		this.sound = sound;
		this.category = category;
		this.soundVolume = volume;
		this.soundPitch = pitch;
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);
		this.sound = (SoundEvent)SoundEvent.REGISTRY.getObjectById(buf.readInt());
		this.category = (SoundCategory)buf.readEnumValue(SoundCategory.class);
		this.soundVolume = buf.readFloat();
		this.soundPitch = buf.readFloat();
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeInt(SoundEvent.REGISTRY.getIDForObject(this.sound));
		buf.writeEnumValue(this.category);
		buf.writeFloat(this.soundVolume);
		buf.writeFloat(this.soundPitch);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT) {
			this.handle();
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		Entity entity = this.getEntity(0);
		if(entity != null) {
			Minecraft.getMinecraft().getSoundHandler().playSound(new EntitySound<Entity>(this.sound, this.category, entity, e -> e.isEntityAlive()));
		}
	}
}
