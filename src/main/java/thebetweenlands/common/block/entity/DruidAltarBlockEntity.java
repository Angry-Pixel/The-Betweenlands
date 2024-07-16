package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.api.recipes.DruidAltarRecipe;
import thebetweenlands.common.block.DruidAltarBlock;
import thebetweenlands.common.block.DruidStoneBlock;
import thebetweenlands.common.items.recipe.MultiStackInput;
import thebetweenlands.common.network.UpdateDruidAltarProgressPacket;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

public class DruidAltarBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

	public final static double FINAL_HEIGHT = 2.0D;
	// 14.25 seconds crafting time
	public static final int DEFAULT_CRAFTING_TIME = 20 * 14 + 5;
	private static final float ROTATION_SPEED = 2.0F;
	public float rotation;
	public float prevRotation;
	public float renderYOffset;
	public float prevRenderYOffset;
	public int craftingProgress = 0;
	private boolean circleShouldRevert = true;
	@Nullable
	private DruidAltarRecipe currentRecipe;

	protected NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);

	public DruidAltarBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.DRUID_ALTAR.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, DruidAltarBlockEntity entity) {
		if (!level.isClientSide() && entity.circleShouldRevert) {
			entity.checkDruidCircleBlocks(level, pos);
			entity.circleShouldRevert = false;
		}
		if (level.isClientSide()) {
			entity.prevRotation = entity.rotation;
			entity.rotation += ROTATION_SPEED;
			if (entity.rotation >= 360.0F) {
				entity.rotation -= 360.0F;
				entity.prevRotation -= 360.0F;
			}
			if (entity.craftingProgress != 0) {
				entity.craftingProgress++;
			}
			entity.prevRenderYOffset = entity.renderYOffset;
			entity.renderYOffset = (float) ((double) entity.craftingProgress / (double) (entity.currentRecipe != null ? entity.currentRecipe.processTime() : DruidAltarBlockEntity.DEFAULT_CRAFTING_TIME) * (FINAL_HEIGHT - 0.2D) + 1.2D);
		} else {
			if (entity.currentRecipe != null) {
				MultiStackInput input = new MultiStackInput(entity.getItems().subList(1, 5));
				// Sync clients every second
				if (entity.craftingProgress % 20 == 0 || entity.craftingProgress == 1) {
					entity.sendCraftingProgressPacket(level, pos);
				}
				entity.craftingProgress++;
				if (!entity.currentRecipe.matches(input, level) || !entity.getItems().getFirst().isEmpty()) {
					entity.stopCraftingProcess(level, pos, state);
				} else {
					entity.currentRecipe.onCrafting(level, pos, input);
					if (entity.craftingProgress >= entity.currentRecipe.processTime()) {
						ItemStack stack = entity.currentRecipe.assemble(input, level.registryAccess());
						entity.getItems().clear();
						entity.getItems().set(0, stack);
						entity.currentRecipe.onCrafted(level, pos, input, stack);
						entity.stopCraftingProcess(level, pos, state);
					}
				}
			}
		}
	}

	private void startCraftingProcess(Level level, BlockPos pos, BlockState state) {
		level.setBlockAndUpdate(pos, state.setValue(DruidAltarBlock.ACTIVE, true));
		this.craftingProgress = 1;
		// Packet to start sound
		PacketDistributor.sendToPlayersNear((ServerLevel) level, null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64.0D, new UpdateDruidAltarProgressPacket(this, -1));
		// Sets client crafting progress to 1
		PacketDistributor.sendToPlayersNear((ServerLevel) level, null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64.0D, new UpdateDruidAltarProgressPacket(this, 1));
		// Does the metadata stuff for the circle animated textures
		this.checkDruidCircleBlocks(level, pos);

		AABB aabb = new AABB(pos).inflate(8, 6, 8);
//		List<DarkDruid> druids = level.getEntitiesOfClass(DarkDruid.class, aabb);
//		for(DarkDruid druid : druids) {
//			druid.kill();
//		}
//
//		MobSpawnerLogicBetweenlands logic = BlockMobSpawnerBetweenlands.getLogic(level, pos.down());
//		if(logic != null) {
//			//Don't spawn druids while crafting
//			logic.setDelay(this.currentRecipe != null ? this.currentRecipe.processTime() : DEFAULT_CRAFTING_TIME + 20);
//		}
	}

	private void stopCraftingProcess(Level level, BlockPos pos, BlockState state) {
		level.setBlockAndUpdate(pos, state.setValue(DruidAltarBlock.ACTIVE, false));
		this.craftingProgress = 0;
		// Packet to cancel sound
		PacketDistributor.sendToPlayersNear((ServerLevel) level, null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64.0D, new UpdateDruidAltarProgressPacket(this, -2));
		// Sets client crafting progress to 0
		PacketDistributor.sendToPlayersNear((ServerLevel) level, null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64.0D, new UpdateDruidAltarProgressPacket(this, 0));
		// Does the metadata stuff for the circle animated textures
		this.checkDruidCircleBlocks(level, pos);
		this.currentRecipe = null;
	}

	public void sendCraftingProgressPacket(Level level, BlockPos pos) {
		PacketDistributor.sendToPlayersNear((ServerLevel) level, null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64.0D, new UpdateDruidAltarProgressPacket(this));
	}

	private void checkDruidCircleBlocks(Level level, BlockPos pos) {
		int baseRadius = 6;
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		int posX = pos.getX(), posY = pos.getY(), posZ = pos.getZ();
		for (int y = 0; y < 6; y++) {
			for (int x = -baseRadius; x <= baseRadius; x++) {
				for (int z = -baseRadius; z <= baseRadius; z++) {
					int dSq = x * x + z * z;
					if (Math.round(Math.sqrt(dSq)) <= baseRadius) {
						mutable.set(posX + x, posY + y, posZ + z);
						BlockState state = level.getBlockState(mutable);
						if (state.getBlock() instanceof DruidStoneBlock) {
							if ((this.craftingProgress == 0 || this.circleShouldRevert) && state.getValue(DruidStoneBlock.ACTIVE)) {
								level.setBlockAndUpdate(mutable, state.setValue(DruidStoneBlock.ACTIVE, false));
							} else if (this.craftingProgress == 1 && !state.getValue(DruidStoneBlock.ACTIVE)) {
								level.setBlockAndUpdate(mutable, state.setValue(DruidStoneBlock.ACTIVE, true));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.getItems().set(slot, stack);
		stack.limitSize(this.getMaxStackSize(stack));
		MultiStackInput input = new MultiStackInput(this.getItems().subList(1, 5));
		Optional<RecipeHolder<DruidAltarRecipe>> holder = RecipeRegistry.getRecipeForInterface(DruidAltarRecipe.class, input, this.getLevel());
		if (!this.getLevel().isClientSide() && holder.isPresent() && !stack.isEmpty() && this.getItem(0).isEmpty() && this.craftingProgress == 0) {
			this.currentRecipe = holder.get().value();
			this.currentRecipe.onStartCrafting(this.getLevel(), this.getBlockPos(), input);
			this.startCraftingProcess(this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("menu.thebetweenlands.druid_altar");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	//TODO
	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return null;
	}

	@Override
	public int getContainerSize() {
		return 5;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.DOWN ? new int[]{0} : new int[]{1, 2, 3, 4};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
		return index > 0;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index == 0;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putInt("crafting_progress", this.craftingProgress);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.craftingProgress = tag.getInt("crafting_progress");
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		this.loadAdditional(packet.getTag(), registries);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		this.saveAdditional(tag, registries);
		return tag;
	}
}
