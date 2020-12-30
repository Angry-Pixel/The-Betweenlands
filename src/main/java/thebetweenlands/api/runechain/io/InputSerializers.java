package thebetweenlands.api.runechain.io;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.capability.IRuneChainUserCapability;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.io.types.IBlockTarget;
import thebetweenlands.api.runechain.io.types.IVectorTarget;
import thebetweenlands.api.runechain.io.types.StaticBlockTarget;
import thebetweenlands.api.runechain.io.types.StaticVectorTarget;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.common.registries.CapabilityRegistry;

public class InputSerializers {
	public static final IInputSerializer<IBlockTarget> BLOCK = new IInputSerializer<IBlockTarget>() {
		@Override
		public void write(IBlockTarget obj, PacketBuffer buffer) {
			buffer.writeLong(obj.block().toLong());
		}

		@Override
		public IBlockTarget read(IRuneChainUser user, PacketBuffer buffer) throws IOException {
			return new StaticBlockTarget(BlockPos.fromLong(buffer.readLong()));
		}
	};

	public static final IInputSerializer<Entity> ENTITY = new IInputSerializer<Entity>() {
		@Override
		public void write(Entity obj, PacketBuffer buffer) {
			buffer.writeVarInt(obj.getEntityId());
		}

		@Override
		public Entity read(IRuneChainUser user, PacketBuffer buffer) throws IOException {
			return user.getWorld().getEntityByID(buffer.readVarInt());
		}		
	};

	//TODO Replace this with IRuneChainUser
	public static final IInputSerializer<Object> USER = new IInputSerializer<Object>() {
		@Override
		public void write(Object obj, PacketBuffer buffer) {
			if(obj instanceof Entity) {
				buffer.writeBoolean(false);
				buffer.writeVarInt(((Entity)obj).getEntityId());
			} else {
				buffer.writeBoolean(true);
				buffer.writeVarInt(((IRuneChainUser)obj).getEntity().getEntityId());
			}
		}

		@Override
		public Object read(IRuneChainUser user, PacketBuffer buffer) throws IOException {
			Entity entity = user.getWorld().getEntityByID(buffer.readVarInt());
			if(entity != null) {
				if(buffer.readBoolean()) {
					IRuneChainUserCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null);
					if(cap != null) {
						return cap.getUser();
					}
				} else {
					return entity;
				}
			}
			return null;
		}		
	};

	public static final IInputSerializer<IVectorTarget> VECTOR = new IInputSerializer<IVectorTarget>() {
		@Override
		public void write(IVectorTarget obj, PacketBuffer buffer) {
			buffer.writeDouble(obj.x());
			buffer.writeDouble(obj.y());
			buffer.writeDouble(obj.z());
		}

		@Override
		public IVectorTarget read(IRuneChainUser user, PacketBuffer buffer) throws IOException {
			return new StaticVectorTarget(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		}		
	};
}
