package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.entity.draeton.DraetonPhysicsPart;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.SoundRegistry;

public class MessageSetDraetonAnchorPos extends MessageEntity {
	private BlockPos pos;

	public MessageSetDraetonAnchorPos() {

	}

	public MessageSetDraetonAnchorPos(EntityDraeton carriage) {
		this.addEntity(carriage);
		DraetonPhysicsPart part = carriage.getAnchorPhysicsPart();
		this.pos = new BlockPos(part.x, part.y, part.z);
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeBlockPos(this.pos);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		super.deserialize(buf);
		this.pos = buf.readBlockPos();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		EntityPlayer player = ctx.getServerHandler().player;

		Entity entity = this.getEntity(0);
		if(entity instanceof EntityDraeton) {
			EntityDraeton carriage = (EntityDraeton) entity;

			if(carriage.getControllingPassenger() == player) {
				DraetonPhysicsPart part = carriage.getAnchorPhysicsPart();

				if(part != null) {
					Vec3d anchorPos = new Vec3d(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f);
	
					Vec3d diff = anchorPos.subtract(carriage.getPositionVector());
	
					//Make sure position is in valid range since it is client controlled
					float dist = (float) Math.sqrt(diff.x * diff.x + diff.y * diff.y + diff.z * diff.z);
					float maxDist = carriage.getMaxTetherLength(part) + 4.0f;
					if(dist > maxDist) {
						diff = diff.scale(1.0f / dist * maxDist);
					}
	
					BlockPos newPos = new BlockPos(carriage.getPositionVector().add(diff));
	
					if(!newPos.equals(carriage.getAnchorPos())) {
						carriage.world.playSound(null, newPos.getX() + 0.5D, newPos.getY() + 0.5D, newPos.getZ() + 0.5D, SoundRegistry.DRAETON_ANCHOR, SoundCategory.NEUTRAL, 1, 1);
					}
					
					carriage.setAnchorPos(newPos, true);
				}
			}
		}

		return null;
	}
}
