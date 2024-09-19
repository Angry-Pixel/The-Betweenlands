package thebetweenlands.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.items.BLItemFrameItem;
import thebetweenlands.common.registries.*;

public class BLItemFrame extends ItemFrame {

	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(BLItemFrame.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> IS_GLOWING = SynchedEntityData.defineId(BLItemFrame.class, EntityDataSerializers.BOOLEAN);
	private static final String TAG_COLOR = "color";

	public BLItemFrame(EntityType<? extends ItemFrame> type, Level level) {
		super(type, level);
	}

	public BLItemFrame(Level level, BlockPos pos, Direction facing, int color) {
		super(EntityRegistry.ITEM_FRAME.get(), level, pos, facing);
		this.getEntityData().set(COLOR, color);
		this.getEntityData().set(IS_GLOWING, false);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(COLOR, 0);
		builder.define(IS_GLOWING, false);
	}

	@Override
	protected ItemStack getFrameItemStack() {
		for (BLItemFrameItem frame : BuiltInRegistries.ITEM.stream().filter(item -> item instanceof BLItemFrameItem).map(BLItemFrameItem.class::cast).toList()) {
			if (frame.getColor() == this.getEntityData().get(COLOR)) {
				return new ItemStack(frame);
			}
		}
		return ItemRegistry.DULL_LAVENDER_ITEM_FRAME.toStack();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt(TAG_COLOR, this.getColor());
		compound.putBoolean("glowing", this.getEntityData().get(IS_GLOWING));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.getEntityData().set(COLOR, compound.getInt(TAG_COLOR));
		this.getEntityData().set(IS_GLOWING, compound.getBoolean("glowing"));
	}

	public int getColor() {
		return this.getEntityData().get(COLOR);
	}

	public boolean isFrameGlowing() {
		return this.getEntityData().get(IS_GLOWING);
	}

	public void setGlowing(boolean glowing) {
		this.getEntityData().set(IS_GLOWING, glowing);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);

		if (player.isShiftKeyDown()) {
			if (itemstack.has(DataComponentRegistry.ASPECT_CONTENTS) && !this.isInvisible()) {
				var contents = itemstack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY);
				if (contents.aspect().isPresent() && contents.aspect().get().is(AspectTypeRegistry.FREIWYNN) && contents.amount() > 100) {
					itemstack.set(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.drainAspect(itemstack, 100));
					this.setInvisible(true);
					if (player instanceof ServerPlayer sp) {
						AdvancementCriteriaRegistry.ITEM_FRAME_INVISIBLE.get().trigger(sp);
					}
					return InteractionResult.sidedSuccess(this.level().isClientSide());
				}
			} else if (itemstack.is(BlockRegistry.GLOWING_GOOP.asItem()) && !this.isFrameGlowing()) {
				itemstack.shrink(1);
				this.setGlowing(true);
				if (player instanceof ServerPlayer sp) {
					AdvancementCriteriaRegistry.ITEM_FRAME_GLOWING.get().trigger(sp);
				}
				return InteractionResult.sidedSuccess(this.level().isClientSide());
			}
		}

		return super.interact(player, hand);
	}
}
