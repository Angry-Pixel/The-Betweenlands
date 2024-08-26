package thebetweenlands.common.block.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.client.audio.AnimatorSoundInstance;
import thebetweenlands.common.items.LifeCrystalItem;
import thebetweenlands.common.registries.*;

import javax.annotation.Nullable;
import java.util.Optional;

public class AnimatorBlockEntity extends BaseContainerBlockEntity {

	public ItemStack itemToAnimate = ItemStack.EMPTY;
	public int fuelBurnProgress, lifeCrystalLife, fuelConsumed = 0, requiredFuelCount = 32, requiredLifeCount = 32;
	public boolean itemAnimated = false;
	private int prevStackSize = 0;
	private ItemStack prevItem = ItemStack.EMPTY;

	private boolean running = false;

	private boolean soundPlaying = false;
	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
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

	public AnimatorBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.ANIMATOR.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, AnimatorBlockEntity entity) {
		if (entity.isSlotInUse(0) && entity.isValidFocalItem(level)) {
			entity.itemToAnimate = entity.getItem(0);
			if (!level.isClientSide()) {
				SingleRecipeInput input = new SingleRecipeInput(entity.itemToAnimate);
				Optional<RecipeHolder<AnimatorRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.ANIMATOR_RECIPE.get(), input, level);
				if (recipe.isPresent()) {
					entity.requiredFuelCount = recipe.get().value().getRequiredFuel(input);
					entity.requiredLifeCount = recipe.get().value().getRequiredLife(input);
				}
			}
		} else {
			entity.itemToAnimate = ItemStack.EMPTY;
		}
		if (!level.isClientSide()) {
			if (entity.isCrystalInSlot())
				entity.lifeCrystalLife = entity.getCrystalPower();
			if (!entity.isSlotInUse(0) || !entity.isSlotInUse(1) || !entity.isSlotInUse(2)) {
				entity.fuelBurnProgress = 0;
				entity.fuelConsumed = 0;
			}

			if (!entity.itemToAnimate.isEmpty() && entity.isCrystalInSlot() && entity.isSulfurInSlot() && entity.fuelConsumed < entity.requiredFuelCount && entity.isValidFocalItem(level)) {
				if (entity.lifeCrystalLife >= entity.requiredLifeCount) {
					entity.fuelBurnProgress++;
					if (entity.fuelBurnProgress >= 42) {
						entity.fuelBurnProgress = 0;
						entity.getItem(2).shrink(1);
						entity.fuelConsumed++;
						entity.setChanged();
					}
					entity.itemAnimated = false;
				}
			}

			if (entity.isSlotInUse(2) && !entity.itemAnimated) {
				if (!entity.isSlotInUse(0) || !entity.isSlotInUse(1)) {
					entity.fuelBurnProgress = 0;
					entity.fuelConsumed = 0;
				}
			}

			if (entity.fuelConsumed >= entity.requiredFuelCount && entity.isSlotInUse(0) && entity.isSlotInUse(1) && !entity.itemAnimated) {
				SingleRecipeInput recipeInput = new SingleRecipeInput(entity.getItem(0));
				Optional<RecipeHolder<AnimatorRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.ANIMATOR_RECIPE.get(), recipeInput, level);
				if (recipe.isPresent()) {
					ItemStack input = entity.getItem(0).copy();
					ItemStack result = recipe.get().value().onAnimated(level, pos, recipeInput);
					if (result.isEmpty()) result = recipe.get().value().assemble(recipeInput, level.registryAccess());
					if (!result.isEmpty()) {
						entity.setItem(0, result.copy());

						AABB aabb = new AABB(pos).inflate(12);
						for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, aabb, EntitySelector.NO_SPECTATORS)) {
							if (player.distanceToSqr(Vec3.atCenterOf(pos)) <= 144) {
								AdvancementCriteriaRegistry.ANIMATE.get().trigger(player, input, result.copy());
							}
						}
					}
				}
				entity.getItem(1).setDamageValue(entity.getItem(1).getDamageValue() + entity.requiredLifeCount);
				entity.setChanged();
				entity.itemAnimated = true;
			}
			if (entity.prevStackSize != (entity.isSlotInUse(0) ? entity.getItem(0).getCount() : 0))
				entity.setChanged();
			if (entity.prevItem != (entity.isSlotInUse(0) ? entity.getItem(0) : ItemStack.EMPTY))
				entity.setChanged();
			entity.prevItem = entity.isSlotInUse(0) ? entity.getItem(0) : ItemStack.EMPTY;
			entity.prevStackSize = entity.isSlotInUse(0) ? entity.getItem(0).getCount() : 0;

			boolean shouldBeRunning = entity.isSlotInUse(0) && entity.isCrystalInSlot() && entity.isSulfurInSlot() && entity.fuelConsumed < entity.requiredFuelCount && entity.lifeCrystalLife >= entity.requiredLifeCount && entity.isValidFocalItem(level);
			if (entity.running != shouldBeRunning) {
				entity.running = shouldBeRunning;
				entity.setChanged();
			}
		} else {
			if (entity.isRunning() && !entity.soundPlaying) {
				entity.playAnimatorSound();
				entity.soundPlaying = true;
			} else if (!entity.isRunning()) {
				entity.soundPlaying = false;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void playAnimatorSound() {
		Minecraft.getInstance().getSoundManager().play(new AnimatorSoundInstance(SoundRegistry.ANIMATOR.get(), SoundSource.BLOCKS, this));
	}

	public boolean isCrystalInSlot() {
		return this.isSlotInUse(1) && this.getItem(1).getItem() instanceof LifeCrystalItem && this.getItem(1).getDamageValue() < this.getItem(1).getMaxDamage();
	}

	public int getCrystalPower() {
		if (this.isCrystalInSlot())
			return this.getItem(1).getMaxDamage() - this.getItem(1).getDamageValue();
		return 0;
	}

	public boolean isSulfurInSlot() {
		return this.isSlotInUse(2) && this.getItem(2).is(ItemRegistry.SULFUR);
	}

	public boolean isSlotInUse(int slot) {
		return !this.getItem(slot).isEmpty();
	}

	public boolean isValidFocalItem(Level level) {
		if (!this.getItem(0).isEmpty()) {
			SingleRecipeInput recipeInput = new SingleRecipeInput(this.getItem(0));
			return level.getRecipeManager().getRecipeFor(RecipeRegistry.ANIMATOR_RECIPE.get(), recipeInput, level).isPresent();
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

	//TODO
	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return null;
	}

	@Override
	public int getContainerSize() {
		return 3;
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
