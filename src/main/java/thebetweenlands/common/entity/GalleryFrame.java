package thebetweenlands.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Locale;

public class GalleryFrame extends HangingEntity {

	protected static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(GalleryFrame.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<String> URL = SynchedEntityData.defineId(GalleryFrame.class, EntityDataSerializers.STRING);

	public GalleryFrame(EntityType<? extends HangingEntity> type, Level level) {
		super(type, level);
	}

	public GalleryFrame(Level level, BlockPos pos, Direction facing, Type type) {
		super(EntityRegistry.GALLERY_FRAME.get(), level, pos);
		this.getEntityData().set(TYPE, type.ordinal());
		this.setDirection(facing);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(URL, "");
		builder.define(TYPE, Type.SMALL.ordinal());
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (TYPE.equals(key)) {
			this.recalculateBoundingBox();
		}
	}

	@Override
	public Component getName() {
		return Component.translatable("entity.thebetweenlands." + this.getFrameType().getSerializedName() + "_gallery_frame");
	}

	public void setUrl(String url) {
		this.getEntityData().set(URL, url);
	}

	public String getUrl() {
		return this.getEntityData().get(URL);
	}

	public Type getFrameType() {
		return Type.values()[this.getEntityData().get(TYPE)];
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		tag.putByte("facing", (byte) this.direction.get2DDataValue());
		super.addAdditionalSaveData(tag);
		tag.putInt("type", this.getEntityData().get(TYPE));
		tag.putString("url", this.getUrl());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		this.direction = Direction.from2DDataValue(tag.getByte("facing"));
		super.readAdditionalSaveData(tag);
		this.setDirection(this.direction);
		this.getEntityData().set(TYPE, tag.getInt("type"));
		this.setUrl(tag.getString("url"));
	}

	@Override
	protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
		Vec3 vec3 = Vec3.atCenterOf(pos).relative(direction, -0.46875);
		Direction.Axis axis = direction.getAxis();
		double d0 = axis == Direction.Axis.X ? 0.0625 : this.getFrameType().getSize();
		double d1 = this.getFrameType().getSize();
		double d2 = axis == Direction.Axis.Z ? 0.0625 : this.getFrameType().getSize();
		return AABB.ofSize(vec3, d0, d1, d2);
	}

	@Override
	public void dropItem(@Nullable Entity entity) {
		if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.playSound(SoundEvents.WOOD_BREAK, 1.0F, 1.0F);

			if (entity instanceof Player player && player.hasInfiniteMaterials()) {
				return;
			}

			this.spawnAtLocation(this.getFrameType().getItem());
		}
	}

	@Override
	public void playPlacementSound() {
		this.playSound(SoundEvents.WOOD_PLACE, 1.0F, 1.0F);
	}

	@Override
	public void moveTo(double x, double y, double z, float yaw, float pitch) {
		this.setPos(x, y, z);
	}

	@Override
	public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
		this.setPos(x, y, z);
	}

	@Override
	public Vec3 trackingPosition() {
		return Vec3.atLowerCornerOf(this.pos);
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
	public InteractionResult interact(Player player, InteractionHand hand) {
		if (this.level().isClientSide()) {
			BetweenlandsClient.openGalleryScreen(this);
			return InteractionResult.SUCCESS;
		}
		return super.interact(player, hand);
	}

	public enum Type implements StringRepresentable {
		VERY_LARGE(4, ItemRegistry.VERY_LARGE_GALLERY_FRAME),
		LARGE(2, ItemRegistry.LARGE_GALLERY_FRAME),
		SMALL(1, ItemRegistry.SMALL_GALLERY_FRAME);

		private final int blockSize;
		private final DeferredItem<Item> item;

		Type(int blockSize, DeferredItem<Item> item) {
			this.blockSize = blockSize;
			this.item = item;
		}

		public int getSize() {
			return this.blockSize;
		}

		public DeferredItem<Item> getItem() {
			return this.item;
		}

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
