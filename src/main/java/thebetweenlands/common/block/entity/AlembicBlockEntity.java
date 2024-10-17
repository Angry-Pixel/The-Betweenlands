package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.block.container.AlembicBlock;
import thebetweenlands.common.block.container.CenserBlock;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.component.item.ElixirContents;
import thebetweenlands.common.component.item.InfusionBucketData;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.item.herblore.DentrothystVialItem;
import thebetweenlands.common.registries.AspectTypeRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlembicBlockEntity extends SyncedBlockEntity {

	public static final int DISTILLING_TIME = 4800; //4 Minutes

	public static final int AMOUNT_PER_VIAL = Amounts.VIAL;

	public static final float ISOLATION_LOSS_MULTIPLIER = 0.15F;

	private boolean running = false;
	private int progress = 0;
	private ItemStack infusionBucket = ItemStack.EMPTY;
	private int producedAmount = 0;
	private int producableAmount = 0;
	private int producableStrength;
	private int producableDuration;
	@Nullable
	private Holder<ElixirEffect> producableElixir = null;
	private final List<Aspect> producableItemAspects = new ArrayList<>();
	@Nullable
	private Holder<ElixirRecipe> recipe = null;
	private int bucketInfusionTime;

	private boolean loadInfusionData = false;

	public AlembicBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.ALEMBIC.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, AlembicBlockEntity entity) {
		if (state.getValue(AlembicBlock.WATER_TYPE) != SwampWaterLoggable.WaterType.NONE) return;
		if (entity.loadInfusionData) {
			entity.loadFromInfusion(level);
			entity.loadInfusionData = false;
		}

		if (entity.isFull() && !entity.hasFinished()) {
			entity.progress++;
			if (!level.isClientSide()) {
				if (!entity.running || entity.progress % 20 == 0) {
					entity.setChanged();
				}
				entity.running = true;
				if (entity.hasFinished()) {
					entity.producedAmount = entity.producableAmount;
				}
			}
		} else {
			if (!level.isClientSide()) {
				if (entity.running) {
					entity.setChanged();
				}
				entity.running = false;
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!this.infusionBucket.isEmpty())
			tag.put("infusion_bucket", this.infusionBucket.save(registries));
		tag.putInt("progress", this.progress);
		tag.putInt("produced_amount", this.producedAmount);
		tag.putBoolean("running", this.running);
		ListTag aspectList = new ListTag();
		for (Aspect aspect : this.producableItemAspects) {
			CompoundTag aspectCompound = new CompoundTag();
			aspect.writeToNBT(aspectCompound, registries);
			aspectList.add(aspectCompound);
		}
		tag.put("producible_item_aspects", aspectList);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("infusion_bucket"))
			this.infusionBucket = ItemStack.parseOptional(registries, tag.getCompound("infusion_bucket"));
		this.loadInfusionData = true;
		this.progress = tag.getInt("progress");
		this.producedAmount = tag.getInt("produced_amount");
		this.running = tag.getBoolean("running");
		if (tag.contains("producible_item_aspects")) {
			this.producableItemAspects.clear();
			ListTag aspectList = tag.getList("producible_item_aspects", Tag.TAG_COMPOUND);
			for (int i = 0; i < aspectList.size(); i++) {
				CompoundTag aspectCompound = aspectList.getCompound(i);
				Aspect aspect = Aspect.readFromNBT(aspectCompound, registries);
				this.producableItemAspects.add(aspect);
			}
		}
	}

	public void addInfusion(Level level, ItemStack bucket) {
		this.infusionBucket = bucket.copy();
		this.loadFromInfusion(level);
		this.setChanged();
	}

	@Override
	public void setChanged() {
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
		super.setChanged();
	}

	@Nullable
	public Holder<ElixirRecipe> getElixirRecipe() {
		return this.recipe;
	}

	public int getInfusionTime() {
		return this.bucketInfusionTime;
	}

	public float getProgress() {
		return (float) this.progress / (float) DISTILLING_TIME;
	}

	private void loadFromInfusion(Level level) {
		this.recipe = null;
		if (this.infusionBucket.isEmpty() || !this.infusionBucket.has(DataComponentRegistry.INFUSION_BUCKET_DATA)) return;
		InfusionBucketData data = this.infusionBucket.get(DataComponentRegistry.INFUSION_BUCKET_DATA);
		int infusionTime = data.infusionTime();
		this.bucketInfusionTime = infusionTime;
		if (data.ingredients().isEmpty()) {
			this.addInvalidInfusion(level);
			return;
		}
		List<ItemStack> infusionIngredients = data.ingredients();
		List<Holder<AspectType>> infusionAspects = this.getInfusionAspects(level, infusionIngredients);
		Holder<ElixirRecipe> holder = ElixirRecipe.getFromAspects(infusionAspects, level.registryAccess());
		this.recipe = holder;
		if (this.recipe == null || infusionTime < holder.value().idealInfusionTime() - holder.value().infusionTimeVariation() || infusionTime > holder.value().idealInfusionTime() + holder.value().infusionTimeVariation()) {
			this.addInvalidInfusion(level);
			return;
		}
		ElixirRecipe recipe = holder.value();
		List<Aspect> infusionItemAspects = this.getInfusionItemAspects(level, infusionIngredients);
		int totalAmount = Amounts.VERY_LOW; //Base amount
		int strengthAspectAmount = 0;
		int durationAspectAmount = 0;
		for (Aspect a : infusionItemAspects) {
			totalAmount += a.amount();
			if (recipe.strengthAspect().isPresent() && a.type().is(recipe.strengthAspect().get()))
				strengthAspectAmount += a.amount();
			if (recipe.durationAspect().isPresent() && a.type().is(recipe.durationAspect().get()))
				durationAspectAmount += a.amount();
		}
		int recipeByariis = 0;
		for (ResourceKey<AspectType> a : recipe.aspects()) {
			if (a == AspectTypeRegistry.BYARIIS) {
				recipeByariis++;
			}
		}
		this.producableAmount = totalAmount;
		boolean isPositive = true;
		for (Holder<AspectType> a : infusionAspects) {
			if (a.is(AspectTypeRegistry.BYARIIS)) {
				if (recipeByariis <= 0) {
					isPositive = !isPositive;
				} else {
					recipeByariis--;
				}
			}
		}
		this.producableElixir = isPositive ? recipe.positiveElixir() : recipe.negativeElixir();
		float relStrengthAmount = strengthAspectAmount / (float) Amounts.MAX_ASPECT_AMOUNT;
		float relDurationAmount = durationAspectAmount / (float) Amounts.MAX_ASPECT_AMOUNT;
		this.producableStrength = Mth.floor(relStrengthAmount * ElixirEffect.VIAL_INFUSION_MAX_POTENCY);
		if (isPositive) {
			this.producableDuration = recipe.baseDuration() + Mth.floor(recipe.durationModifier() * relDurationAmount);
		} else {
			this.producableDuration = recipe.negativeBaseDuration() + Mth.floor(recipe.negativeDurationModifier() * relDurationAmount);
		}
	}

	private void addInvalidInfusion(Level level) {
		//Invalid recipe or infusion too short or too long
		this.producableElixir = null;
		this.producableAmount = 0;
		this.producableDuration = 0;
		this.producableStrength = 0;
		this.producableItemAspects.clear();
		if (!this.infusionBucket.isEmpty() && this.infusionBucket.has(DataComponentRegistry.INFUSION_BUCKET_DATA)) {
			List<ItemStack> infusionIngredients = this.infusionBucket.get(DataComponentRegistry.INFUSION_BUCKET_DATA).ingredients();
			List<Aspect> infusionAspects = this.getInfusionItemAspects(level, infusionIngredients);
			for (Aspect aspect : infusionAspects) {
				this.producableItemAspects.add(new Aspect(aspect.type(), Mth.floor((aspect.amount() * (1.0F - ISOLATION_LOSS_MULTIPLIER)) / 3.0F)));
			}
		}
	}

	public List<Holder<AspectType>> getInfusionAspects(Level level, List<ItemStack> ingredients) {
		List<Holder<AspectType>> infusingAspects = new ArrayList<>();
		for (ItemStack ingredient : ingredients) {
			List<Aspect> container = AspectContents.getAllAspectsForItem(ingredient, level.registryAccess(), AspectManager.get(level));
			for (Aspect aspect : container) {
				infusingAspects.add(aspect.type());
			}
		}
		return infusingAspects;
	}

	private List<Aspect> getInfusionItemAspects(Level level, List<ItemStack> ingredients) {
		List<Aspect> infusingItemAspects = new ArrayList<>();
		for (ItemStack ingredient : ingredients) {
			List<Aspect> container = AspectContents.getAllAspectsForItem(ingredient, level.registryAccess(), AspectManager.get(level));
			infusingItemAspects.addAll(container);
		}
		return infusingItemAspects;
	}

	public boolean isFull() {
		return !this.infusionBucket.isEmpty();
	}

	public boolean hasFinished() {
		return this.progress >= DISTILLING_TIME;
	}

	public boolean hasElixir() {
		return this.producedAmount > 0.0F;
	}

	public boolean isRunning() {
		return this.running;
	}

	/**
	 * Creates an item stack with the elixir in the alembic.
	 *
	 * @param vial
	 * @return
	 */
	@Nullable
	public ItemStack getElixir(Level level, BlockPos pos, BlockState state, DentrothystVialItem vial) {
		if (this.isFull() && this.hasFinished()) {
			if (this.producableElixir != null) {
				ItemStack elixir = ItemStack.EMPTY;
				if (this.hasElixir()) {
					elixir = this.createElixir(this.producableElixir, this.producableStrength, this.producableDuration, vial);
				}
				this.producedAmount -= AMOUNT_PER_VIAL;
				if (this.producedAmount <= 0.0F || !this.hasElixir()) {
					this.reset(level, pos, state);
				}
				return elixir;
			} else {
				ItemStack aspectVial = ItemStack.EMPTY;
				if (!this.producableItemAspects.isEmpty()) {
					Aspect aspect = this.producableItemAspects.getFirst();
					this.producableItemAspects.removeFirst();
					int removedAmount = aspect.amount();
					Iterator<Aspect> itemAspectIT = this.producableItemAspects.iterator();
					while (itemAspectIT.hasNext()) {
						Aspect currentAspect = itemAspectIT.next();
						if (currentAspect.type() == aspect.type()) {
							removedAmount += currentAspect.amount();
							itemAspectIT.remove();
						}
					}
					if (removedAmount > Amounts.VIAL) {
						this.producableItemAspects.add(new Aspect(aspect.type(), removedAmount - Amounts.VIAL));
						removedAmount = Amounts.VIAL;
					}
					aspectVial = AspectContents.createItemStack(vial.getFullAspectBottle().value(), aspect.type(), removedAmount);
				}
				if (this.producableItemAspects.isEmpty()) {
					this.reset(level, pos, state);
				}
				return aspectVial;
			}
		}
		return null;
	}

	public void reset(Level level, BlockPos pos, BlockState state) {
		this.producableItemAspects.clear();
		this.infusionBucket = ItemStack.EMPTY;
		this.producableAmount = 0;
		this.producableDuration = 0;
		this.producableElixir = null;
		this.producableStrength = 0;
		this.producedAmount = 0;
		this.progress = 0;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	private ItemStack createElixir(Holder<ElixirEffect> elixir, int strength, int duration, DentrothystVialItem vial) {
		return ElixirContents.createItemStack(vial.getFullElixirBottle().value(), elixir, duration, strength);
	}
}
