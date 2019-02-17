package thebetweenlands.common.network.clientbound;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
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
	public void deserialize(PacketBuffer buf) {
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

		if(ctx.side == Dist.CLIENT) {
			this.handle();
		}

		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void handle() {
		Entity entity = this.getEntity(0);
		if(entity != null) {
			Minecraft.getInstance().getSoundHandler().playSound(new EntitySound<Entity>(this.sound, this.category, entity, e -> e.isEntityAlive()));
		}
	}
}
