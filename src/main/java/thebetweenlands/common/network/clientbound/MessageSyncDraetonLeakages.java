package thebetweenlands.common.network.clientbound;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.entity.draeton.DraetonLeakage;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.network.MessageEntity;

public class MessageSyncDraetonLeakages extends MessageEntity {
	private List<DraetonLeakage> leakages = new ArrayList<>();

	public MessageSyncDraetonLeakages() { }

	public MessageSyncDraetonLeakages(EntityDraeton draeton) {
		this.addEntity(draeton);
		this.leakages.addAll(draeton.getLeakages());
	}

	public static void serialize(List<DraetonLeakage> leakages, PacketBuffer buf) {
		buf.writeVarInt(leakages.size());
		for(DraetonLeakage leakage : leakages) {
			buf.writeFloat((float)leakage.pos.x);
			buf.writeFloat((float)leakage.pos.y);
			buf.writeFloat((float)leakage.pos.z);
			buf.writeFloat((float)leakage.dir.x);
			buf.writeFloat((float)leakage.dir.y);
			buf.writeFloat((float)leakage.dir.z);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		serialize(this.leakages, buf);
	}

	public static void deserialize(List<DraetonLeakage> leakages, PacketBuffer buf) {
		int entries = buf.readVarInt();
		for(int i = 0; i < entries; i++) {
			leakages.add(new DraetonLeakage(new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat()), new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat())));
		}
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		super.deserialize(buf);
		deserialize(this.leakages, buf);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);
		
		if(ctx.side == Side.CLIENT) {
			Entity entity = this.getEntity(0);

			if(entity instanceof EntityDraeton) {
				((EntityDraeton) entity).setLeakages(this.leakages);
			}
		}

		return null;
	}
}
