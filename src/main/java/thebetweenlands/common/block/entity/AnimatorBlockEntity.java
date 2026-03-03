package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.audio.AnimatorSoundInstance;
import thebetweenlands.common.inventory.AnimatorMenu;
import thebetweenlands.common.item.misc.LifeCrystalItem;
import thebetweenlands.common.registries.*;

import javax.annotation.Nullable;

public class AnimatorBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

	/**
	 * Slot that contains the item to animate + recipe output once animation is finished
	 */
	public static final int FOCAL_SLOT = 0;
	/**
	 * Slot that contains the life crystal
	 */
	public static final int LIFE_CRYSTAL_SLOT = 1;
	/**
	 * Slot that contains the fuel (sulphur)
	 */
	public static final int FUEL_SLOT = 2;

	private static final int[] SLOTS_FOR_VERTICAL = new int[] {FOCAL_SLOT};
	private static final int[] SLOTS_FOR_HORIZONTAL = new int[] {LIFE_CRYSTAL_SLOT, FUEL_SLOT};
	
	public ItemStack itemToAnimate = ItemStack.EMPTY;
	public int fuelBurnProgress;
	public int lifeCrystalLife;
	public int fuelConsumed = 0;
	public int requiredFuelCount = 32;
	public int requiredLifeCount = 32;
	public boolean itemAnimated = false;
	private int prevStackSize = 0;
	private ItemStack prevItem = ItemStack.EMPTY;

	public float oRot;
	public float rot;
	public float tRot;

	private boolean running = false;

	private boolean soundPlaying = false;
	public final ContainerData data = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case 0 -> AnimatorBlockEntity.this.fuelBurnProgress;
				case 1 -> AnimatorBlockEntity.this.lifeCrystalLife;
				case 2 -> AnimatorBlockEntity.this.itemAnimated ? 1 : 0;
				case 3 -> AnimatorBlockEntity.this.fuelConsumed;
				case 4 -> AnimatorBlockEntity.this.requiredFuelCount;
				case 5 -> AnimatorBlockEntity.this.requiredLifeCount;
				default -> 0;
			};
		}

		public void set(int index, int value) {
			switch (index) {
				case 0 -> AnimatorBlockEntity.this.fuelBurnProgress = value;
				case 1 -> AnimatorBlockEntity.this.lifeCrystalLife = value;
				case 2 -> AnimatorBlockEntity.this.itemAnimated = value == 1;
				case 3 -> AnimatorBlockEntity.this.fuelConsumed = value;
				case 4 -> AnimatorBlockEntity.this.requiredFuelCount = value;
				case 5 -> AnimatorBlockEntity.this.requiredLifeCount = value;
			}
		}

		public int getCount() {
			return 6;
		}
	};

	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	public final RecipeManager.CachedCheck<SingleRecipeInput, AnimatorRecipe> quickCheck = RecipeManager.createCheck(RecipeRegistry.ANIMATOR_RECIPE.get());

	public AnimatorBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.ANIMATOR.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, AnimatorBlockEntity entity) {
		if (!level.isClientSide()) {
			if (entity.isValidFocalItem(level)) {
				entity.itemToAnimate = entity.getItem(FOCAL_SLOT);
				SingleRecipeInput input = new SingleRecipeInput(entity.itemToAnimate);
				RecipeHolder<AnimatorRecipe> recipe = entity.quickCheck.getRecipeFor(input, level).orElse(null);
				if (recipe != null) {
					entity.requiredFuelCount = recipe.value().getRequiredFuel(input);
					entity.requiredLifeCount = recipe.value().getRequiredLife(input);
				}
			} else {
				entity.itemToAnimate = ItemStack.EMPTY;
			}

			if (entity.isCrystalInSlot())
				entity.lifeCrystalLife = entity.getCrystalPower();
			if (entity.getItems().subList(0, 3).stream().anyMatch(ItemStack::isEmpty)) {
				entity.fuelBurnProgress = 0;
				entity.fuelConsumed = 0;
			}

			if (!entity.itemToAnimate.isEmpty() && entity.isCrystalInSlot() && entity.isSulfurInSlot() && entity.fuelConsumed < entity.requiredFuelCount && entity.isValidFocalItem(level)) {
				if (entity.lifeCrystalLife >= entity.requiredLifeCount) {
					entity.fuelBurnProgress++;
					if (entity.fuelBurnProgress >= 42) {
						entity.fuelBurnProgress = 0;
						entity.getItem(FUEL_SLOT).shrink(1);
						entity.fuelConsumed++;
						entity.setChanged();
					}
					entity.itemAnimated = false;
				}
			}

			if (!entity.getItem(FUEL_SLOT).isEmpty() && !entity.itemAnimated) {
				if (entity.getItem(FOCAL_SLOT).isEmpty() || entity.getItem(LIFE_CRYSTAL_SLOT).isEmpty()) {
					entity.fuelBurnProgress = 0;
					entity.fuelConsumed = 0;
				}
			}

			if (entity.fuelConsumed >= entity.requiredFuelCount && !entity.getItem(FOCAL_SLOT).isEmpty() && !entity.getItem(LIFE_CRYSTAL_SLOT).isEmpty() && !entity.itemAnimated) {
				SingleRecipeInput recipeInput = new SingleRecipeInput(entity.getItem(FOCAL_SLOT));
				RecipeHolder<AnimatorRecipe> recipe = entity.quickCheck.getRecipeFor(recipeInput, level).orElse(null);
				if (recipe != null) {
					ItemStack input = entity.getItem(FOCAL_SLOT).copy();
					ItemStack result = recipe.value().onAnimated((ServerLevel) level, pos, recipeInput);
					if (result.isEmpty()) result = recipe.value().assemble(recipeInput, level.registryAccess());
					if (!result.isEmpty()) {
						entity.setItem(FOCAL_SLOT, result.copy());

						for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, new AABB(pos).inflate(12), EntitySelector.NO_SPECTATORS)) {
							if (player.distanceToSqr(Vec3.atCenterOf(pos)) <= 144) {
								AdvancementCriteriaRegistry.ANIMATE.get().trigger(player, input, result.copy());
							}
						}
					}
				}
				entity.getItem(LIFE_CRYSTAL_SLOT).setDamageValue(entity.getItem(LIFE_CRYSTAL_SLOT).getDamageValue() + entity.requiredLifeCount);
				entity.setChanged();
				entity.itemAnimated = true;
			}
			if (entity.prevStackSize != entity.getItem(FOCAL_SLOT).getCount())
				entity.setChanged();
			if (entity.prevItem != entity.getItem(FOCAL_SLOT))
				entity.setChanged();
			entity.prevItem = entity.getItem(FOCAL_SLOT);
			entity.prevStackSize = entity.getItem(FOCAL_SLOT).getCount();

			boolean shouldBeRunning = !entity.getItem(FOCAL_SLOT).isEmpty() && entity.isCrystalInSlot() && entity.isSulfurInSlot() && entity.fuelConsumed < entity.requiredFuelCount && entity.lifeCrystalLife >= entity.requiredLifeCount && entity.isValidFocalItem(level);
			if (entity.running != shouldBeRunning) {
				entity.running = shouldBeRunning;
				entity.setChanged();
			}
		} else {
			entity.updateEntityRotation();
			if (entity.isRunning() && !entity.soundPlaying) {
				BetweenlandsClient.playLocalSound(new AnimatorSoundInstance(SoundRegistry.ANIMATOR.get(), SoundSource.BLOCKS, entity));
				entity.soundPlaying = true;
			} else if (!entity.isRunning()) {
				entity.soundPlaying = false;
			}
		}
	}

	private void updateEntityRotation() {
		this.oRot = this.rot;
		double d0 = BetweenlandsClient.getClientPlayer().getX() - this.getBlockPos().getX() - 0.5D;
		double d1 = BetweenlandsClient.getClientPlayer().getZ() - this.getBlockPos().getZ() - 0.5D;
		this.tRot = (float) Mth.atan2(d1, d0);

		while (this.rot >= Mth.PI) {
			this.rot -= Mth.TWO_PI;
		}

		while (this.rot < -Mth.PI) {
			this.rot += Mth.TWO_PI;
		}

		while (this.tRot >= Mth.PI) {
			this.tRot -= Mth.TWO_PI;
		}

		while (this.tRot < -Mth.PI) {
			this.tRot += Mth.TWO_PI;
		}

		float f2 = this.tRot - this.rot;

		while (f2 >= Mth.PI) {
			f2 -= Mth.TWO_PI;
		}

		while (f2 < -Mth.PI) {
			f2 += Mth.TWO_PI;
		}

		this.rot += f2 * 0.4F;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}

	public boolean isValidLifeCrystal(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof LifeCrystalItem;
	}

	public boolean isValidFuel(ItemStack stack) {
		return !stack.isEmpty() && stack.is(ItemRegistry.SULFUR);
	}
	
	public boolean isCrystalInSlot() {
		final ItemStack stack = this.getItem(LIFE_CRYSTAL_SLOT);
		return this.isValidLifeCrystal(stack) && stack.getDamageValue() < stack.getMaxDamage();
	}

	public int getCrystalPower() {
		if (this.isCrystalInSlot())
			return this.getItem(LIFE_CRYSTAL_SLOT).getMaxDamage() - this.getItem(LIFE_CRYSTAL_SLOT).getDamageValue();
		return 0;
	}

	public boolean isSulfurInSlot() {
		return this.isValidFuel(this.getItem(FUEL_SLOT));
	}

	public boolean isValidFocalItem(Level level) {
		if (!this.getItem(FOCAL_SLOT).isEmpty()) {
			SingleRecipeInput recipeInput = new SingleRecipeInput(this.getItem(FOCAL_SLOT));
			return this.quickCheck.getRecipeFor(recipeInput, level).isPresent();
		}
		return false;
	}

	public boolean isRunning() {
		return this.running;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.animator");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
		// I think this should technically be happening in setChanged()
		if (slot == LIFE_CRYSTAL_SLOT) {
			this.lifeCrystalLife = this.getCrystalPower();
		}
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new AnimatorMenu(containerId, inventory, this, this.data);
	}

	@Override
	public int getContainerSize() {
		return 3;
	}
	
	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if(slot == LIFE_CRYSTAL_SLOT) {
			// Only allow inserting life crystals
			return this.isValidLifeCrystal(stack);
		} else if(slot == FUEL_SLOT) {
			// Only allow inserting fuel
			return this.isValidFuel(stack);
		}
		// Do not allow placing items into the focal slot if it's working as an output right now
		if(slot == FOCAL_SLOT && itemAnimated) {
			return false;
		}
		return super.canPlaceItem(slot, stack);
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		if(side.getAxis().isVertical()) {
			return SLOTS_FOR_VERTICAL;
		} else {
			return SLOTS_FOR_HORIZONTAL;
		}
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		if(index == FOCAL_SLOT) {
			// Deny the top face access to the focal slot if it's an output right now
			if(direction == Direction.UP && itemAnimated) {
				return false;
			}
			// Don't allow inserting through the bottom face
			else if(direction == Direction.DOWN) {
				return false;
			}
		}
		return this.canPlaceItem(index, itemStack);
	}
	
	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if(index == FOCAL_SLOT) {
			// Deny the top face access to the focal slot if it's an output right now
			if(direction == Direction.UP && itemAnimated) {
				return false;
			}
			// Deny the bottom face access to the focal slot if it's an input right now
			else if(direction == Direction.DOWN && !itemAnimated) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putInt("life", this.lifeCrystalLife);
		tag.putInt("progress", this.fuelBurnProgress);
		tag.putInt("items_consumed", this.fuelConsumed);
		tag.putBoolean("life_depleted", this.itemAnimated);
		if (!this.itemToAnimate.isEmpty()) {
			tag.put("to_animate", this.itemToAnimate.save(registries));
		}
		tag.putBoolean("running", this.running);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.lifeCrystalLife = tag.getInt("life");
		this.fuelBurnProgress = tag.getInt("progress");
		this.fuelConsumed = tag.getInt("items_consumed");
		this.itemAnimated = tag.getBoolean("life_depleted");
		if (tag.contains("to_animate", Tag.TAG_COMPOUND))
			this.itemToAnimate = ItemStack.parseOptional(registries, tag.getCompound("to_animate"));
		else
			this.itemToAnimate = ItemStack.EMPTY;
		this.running = tag.getBoolean("running");
	}


	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveCustomOnly(registries);
	}
}
