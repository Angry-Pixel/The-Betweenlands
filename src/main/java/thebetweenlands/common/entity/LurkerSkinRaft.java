package thebetweenlands.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class LurkerSkinRaft extends Boat {

	private ItemStack shield = ItemStack.EMPTY;
	private boolean updating = false;

	public LurkerSkinRaft(EntityType<? extends Boat> entityType, Level level) {
		super(entityType, level);
	}

	public LurkerSkinRaft(Level level, double x, double y, double z, ItemStack shield) {
		this(EntityRegistry.LURKER_SKIN_RAFT.get(), level);
		this.setPos(x, y, z);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.shield = shield.copy();
	}

	@Override
	public Item getDropItem() {
		return ItemRegistry.LURKER_SKIN_SHIELD.get();
	}

	protected ItemStack getBoatDrop() {
		if (!this.shield.isEmpty()) {
			return this.shield.copy();
		}
		return new ItemStack(this.getDropItem());
	}

	@Override
	public @Nullable ItemEntity spawnAtLocation(ItemStack stack, float offsetY) {
		if (stack.is(this.getDropItem())) {
			return super.spawnAtLocation(this.getBoatDrop(), offsetY);
		}
		return null;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
		//Don't break raft
		this.fallDistance = 0;

		super.checkFallDamage(y, onGround, state, pos);
	}

	@Override
	protected void removePassenger(Entity passenger) {
		if (!this.level().isClientSide() && this.isAlive() && this.getPassengers().indexOf(passenger) == 0) {
			ItemStack drop = this.getBoatDrop();

			boolean itemReturned = false;

			if (passenger instanceof Player player) {

				if (player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
					player.setItemInHand(InteractionHand.OFF_HAND, drop);
					itemReturned = true;
				} else if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
					player.setItemInHand(InteractionHand.MAIN_HAND, drop);
					itemReturned = true;
				} else {
					itemReturned = player.getInventory().add(drop);
				}
			}

			if (itemReturned) {
				this.discard();
			}
		}

		super.removePassenger(passenger);
	}

	@Override
	protected float getSinglePassengerXOffset() {
		return -0.2F;
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
		float f = this.getSinglePassengerXOffset();
		if (this.getPassengers().size() > 1) {
			int i = this.getPassengers().indexOf(entity);
			if (i == 0) {
				f = 0.3F;
			} else {
				f = -0.4F;
			}

			if (entity instanceof Animal) {
				f += 0.2F;
			}
		}

		return new Vec3(0.0, dimensions.height() * 0.8888889F, f).yRot(-this.getYRot() * Mth.DEG_TO_RAD);
	}

	@Override
	public void tick() {
		this.updating = true;
		super.tick();
		this.updating = false;
	}

	@Override
	public void move(MoverType type, Vec3 movement) {
		if (this.updating) {
			movement = movement.multiply(new Vec3(0.4D, 1.0D, 0.4D));
		}
		super.move(type, movement);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		if (!this.shield.isEmpty()) {
			compound.put("shield", this.shield.save(this.level().registryAccess()));
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		this.shield = ItemStack.EMPTY;
		if (compound.contains("shield", Tag.TAG_COMPOUND)) {
			this.shield = ItemStack.parseOptional(this.level().registryAccess(), compound.getCompound("shield"));
		}
	}
}
