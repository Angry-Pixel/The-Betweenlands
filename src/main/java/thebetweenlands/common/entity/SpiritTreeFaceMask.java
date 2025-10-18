package thebetweenlands.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class SpiritTreeFaceMask extends HangingEntity implements IEntityWithComplexSpawn {
	public enum MaskType {
		LARGE, SMALL;
	}

	private MaskType maskType;

	public SpiritTreeFaceMask(EntityType<? extends HangingEntity> type, Level level) {
		super(type, level);
		this.maskType = MaskType.SMALL;
	}

	public SpiritTreeFaceMask(Level level, BlockPos pos, Direction direction, MaskType maskType) {
		super(EntityRegistry.SPIRIT_TREE_FACE_MASK.get(), level, pos);
		this.maskType = maskType;
		this.setDirection(direction);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
	}

	public MaskType getMaskType() {
		return this.maskType;
	}

	@Override
	protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
		Vec3 vec3 = Vec3.atCenterOf(pos).relative(direction, -0.46875D);
		int size = this.getMaskType() == MaskType.LARGE ? 2 : 1;
		Direction.Axis axis = direction.getAxis();
		double x = axis == Direction.Axis.X ? 0.0625 : size;
		double z = axis == Direction.Axis.Z ? 0.0625 : size;
		return AABB.ofSize(vec3, x, size, z);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putByte("facing", (byte)this.direction.get2DDataValue());
		tag.putInt("mask_type", this.maskType.ordinal());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		this.direction = Direction.from2DDataValue(tag.getByte("facing"));
		super.readAdditionalSaveData(tag);
		this.setDirection(this.direction);
		this.maskType = MaskType.values()[tag.getInt("mask_type")];
	}

	@Override
	public void dropItem(@Nullable Entity entity) {
		if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.playSound(SoundEvents.WOOD_BREAK);
			if (entity instanceof Player player && player.hasInfiniteMaterials()) {
				return;
			}

			this.spawnAtLocation(new ItemStack(this.maskType == MaskType.LARGE ? ItemRegistry.LARGE_SPIRIT_TREE_FACE_MASK.get() : ItemRegistry.SMALL_SPIRIT_TREE_FACE_MASK.get()));
		}
	}

	@Override
	public void playPlacementSound() {
		this.playSound(SoundEvents.WOOD_PLACE);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
		return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
	}

	@Override
	public void recreateFromPacket(ClientboundAddEntityPacket packet) {
		super.recreateFromPacket(packet);
		this.setDirection(Direction.from3DDataValue(packet.getData()));
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buf) {
		buf.writeInt(this.maskType.ordinal());
		buf.writeInt(this.direction.ordinal());
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf buf) {
		this.maskType = MaskType.values()[buf.readInt()];
		this.setDirection(Direction.values()[buf.readInt()]);
		this.recalculateBoundingBox();
	}
}
