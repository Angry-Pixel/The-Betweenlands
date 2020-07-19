package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.shields.ItemLivingWeedwoodShield;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.ItemRegistry;

public class MessageLivingWeedwoodShieldSpit extends MessageEntity {
	private boolean mainhand;
	private int ticks;

	public MessageLivingWeedwoodShieldSpit() { }

	public MessageLivingWeedwoodShieldSpit(EntityLivingBase owner, boolean mainhand, int ticks) {
		this.addEntity(owner);
		this.mainhand = mainhand;
		this.ticks = ticks;
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);

		this.mainhand = buf.readBoolean();
		this.ticks = buf.readInt();
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);

		buf.writeBoolean(this.mainhand);
		buf.writeInt(this.ticks);
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
		Entity owner = this.getEntity(0);
		if(owner instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) owner;
			ItemStack held = living.getHeldItem(this.mainhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);

			if(!held.isEmpty() && held.getItem() instanceof ItemLivingWeedwoodShield) {
				((ItemLivingWeedwoodShield) held.getItem()).setSpitTicks(held, this.ticks);

				float yaw = -(180 - living.renderYawOffset);
				Vec3d lookVec = living.getLookVec();
				Vec3d bodyForward = new Vec3d(MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI), 0, MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI));
				Vec3d up = new Vec3d(0, 1, 0);
				Vec3d right = bodyForward.crossProduct(up);
				Vec3d offset = new Vec3d(bodyForward.x * 0.5F, owner.getEyeHeight(), bodyForward.z * 0.5F).add(right.scale(this.mainhand ? 0.35D : -0.35D).add(0, lookVec.y * 0.5D - 0.4D, 0).add(bodyForward.scale(-0.1D)));

				int itemId = Item.getIdFromItem(ItemRegistry.SAP_SPIT);
				for(int i = 0; i < 20; i++) {
					double dx = living.world.rand.nextDouble() * 0.2D - 0.1D + bodyForward.x * 0.1D;
					double dy = living.world.rand.nextDouble() * 0.2D - 0.1D + bodyForward.y * 0.1D + 0.08D;
					double dz = living.world.rand.nextDouble() * 0.2D - 0.1D + bodyForward.z * 0.1D;
					living.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, living.posX + offset.x + dx, living.posY + offset.y + dy, living.posZ + offset.z + dz, dx * 1.25D, dy, dz * 1.25D, itemId);
				}
			}
		}
	}
}
