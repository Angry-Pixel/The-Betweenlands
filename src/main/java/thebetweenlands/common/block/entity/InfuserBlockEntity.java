package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectContainerItem;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.items.LifeCrystalItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class InfuserBlockEntity extends NoMenuContainerBlockEntity implements IFluidHandler {

	public static final int MAX_INGREDIENTS = 6;

	private NonNullList<ItemStack> items = NonNullList.withSize(MAX_INGREDIENTS + 2, ItemStack.EMPTY);
	public final FluidTank waterTank = new FluidTank(FluidType.BUCKET_VOLUME * 3, stack -> stack.is(FluidRegistry.SWAMP_WATER_STILL.get()));

	private int infusionTime = 0;
	private int stirProgress = 90;
	private int temp = 0;
	private int evaporation = 0;
	private int itemBob = 0;
	private boolean countUp = true;
	private boolean hasInfusion = false;
	private boolean hasCrystal = false;
	private float crystalVelocity = 0.0F;
	private float crystalRotation = 0.0F;
	@Nullable
	private Holder<ElixirRecipe> infusingRecipe = null;
	private boolean updateRecipe = false;

	/**
	 * 0 = no progress, 1 = in progress, 2 = finished, 3 = failed
	 **/
	private int currentInfusionState = 0;
	private int prevInfusionState = 0;
	private int infusionColorGradientTicks = 0;

	public float[] prevInfusionColor = new float[4];
	public float[] currentInfusionColor = new float[4];

	public InfuserBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.INFUSER.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, InfuserBlockEntity entity) {
		if (entity.updateRecipe) {
			entity.updateInfusingRecipe();
			entity.updateRecipe = false;
		}
		boolean updateBlock = false;
		if (entity.hasInfusion && entity.infusingRecipe != null) {
			if (!level.isClientSide()) {
				entity.infusionTime++;
			} else {
				if (entity.prevInfusionState != entity.currentInfusionState) {
					entity.prevInfusionColor = entity.currentInfusionColor;
					entity.currentInfusionColor = ElixirRecipe.getInfusionColor(entity.infusingRecipe, entity.infusionTime);
				} else {
					entity.currentInfusionColor = ElixirRecipe.getInfusionColor(entity.infusingRecipe, entity.infusionTime);
				}
			}
			if (entity.prevInfusionState != entity.currentInfusionState && entity.currentInfusionState == 2) {
				level.playSound(null, pos, SoundRegistry.INFUSER_FINISHED.get(), SoundSource.BLOCKS, 1, 1);
			}
			entity.prevInfusionState = entity.currentInfusionState;
			if (!level.isClientSide()) {
				if (entity.infusionTime > entity.infusingRecipe.value().idealInfusionTime() + entity.infusingRecipe.value().infusionTimeVariation()) {
					//fail
					if (entity.currentInfusionState != 3)
						updateBlock = true;
					entity.currentInfusionState = 3;
				} else if (entity.infusionTime > entity.infusingRecipe.value().idealInfusionTime() - entity.infusingRecipe.value().infusionTimeVariation()
					&& entity.infusionTime < entity.infusingRecipe.value().idealInfusionTime() + entity.infusingRecipe.value().infusionTimeVariation()) {
					//finished
					if (entity.currentInfusionState != 2)
						updateBlock = true;
					entity.currentInfusionState = 2;
				} else {
					//progress
					if (entity.currentInfusionState != 1)
						updateBlock = true;
					entity.currentInfusionState = 1;
				}
			}
			if (entity.infusionColorGradientTicks > 0) {
				entity.infusionColorGradientTicks++;
			}
			if (!level.isClientSide() && entity.currentInfusionState != entity.prevInfusionState) {
				//start gradient animation
				entity.infusionColorGradientTicks = 1;
				updateBlock = true;
			}
			if (!level.isClientSide() && entity.infusionColorGradientTicks > 30) {
				entity.infusionColorGradientTicks = 0;
				updateBlock = true;
			}
			if (level.isClientSide() && entity.infusionColorGradientTicks > 0 && entity.currentInfusionState == 2) {
				float[] colors = entity.currentInfusionColor;
				for (int i = 0; i < 3 + level.getRandom().nextInt(5); i++) {
//					BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(world, pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F,
//						ParticleArgs.get()
//							.withMotion((level.getRandom().nextFloat() * 0.25F - 0.125f) * 0.09f, level.getRandom().nextFloat() * 0.02F + 0.01F, (level.getRandom().nextFloat() * 0.25F - 0.125f) * 0.09f)
//							.withScale(1f + level.getRandom().nextFloat() * 2.0F)
//							.withColor(colors[0], colors[1], colors[2], 1)
//							.withData(80, true, 0.01F, true)));
				}
			}
		} else {
			if (entity.currentInfusionState != 0)
				updateBlock = true;

			entity.infusionTime = 0;

			if (entity.hasIngredients() && entity.temp >= 100) {
				if (entity.infusionColorGradientTicks > 0) {
					entity.infusionColorGradientTicks++;
				}

				if (!level.isClientSide() && entity.infusionColorGradientTicks == 0 && entity.currentInfusionState == 0 && entity.stirProgress == 89) {
					//start gradient animation
					entity.infusionColorGradientTicks = 1;
					entity.currentInfusionState = 1;
					level.playSound(null, pos, SoundRegistry.INFUSER_FINISHED.get(), SoundSource.BLOCKS, 1, 1);
					updateBlock = true;
				}

				if (!level.isClientSide() && entity.infusionColorGradientTicks > 30) {
					entity.infusionColorGradientTicks = 0;
					entity.currentInfusionState = 2;
					updateBlock = true;
				}

				if (level.isClientSide() && (entity.infusionColorGradientTicks > 0 || entity.currentInfusionState == 2)) {
					entity.prevInfusionColor = new float[]{0.2F, 0.6F, 0.4F, 1.0F};
					entity.currentInfusionColor = new float[]{0.8F, 0.0F, 0.8F, 1.0F};
				}

				if (level.isClientSide() && entity.infusionColorGradientTicks > 0) {
					float[] colors = entity.currentInfusionColor;
					for (int i = 0; i < 3 + level.getRandom().nextInt(5); i++) {
//						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(world, pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F,
//							ParticleArgs.get()
//								.withMotion((level.getRandom().nextFloat() * 0.25F - 0.125f) * 0.09f, level.getRandom().nextFloat() * 0.02F + 0.01F, (level.getRandom().nextFloat() * 0.25F - 0.125f) * 0.09f)
//								.withScale(1f + level.getRandom().nextFloat() * 2.0F)
//								.withColor(colors[0], colors[1], colors[2], 1)
//								.withData(80, true, 0.01F, true)));
					}
				}
			} else {
				entity.currentInfusionState = 0;
				entity.currentInfusionColor = new float[]{0.2F, 0.6F, 0.4F, 1.0F};
				entity.prevInfusionColor = entity.currentInfusionColor;
			}
		}
		if (!level.isClientSide() && updateBlock) {
			entity.markForUpdate(level, pos, state);
		}
		if (level.isClientSide()) {
			if (entity.isValidCrystalInstalled()) {
				entity.crystalVelocity -= Math.signum(entity.crystalVelocity) * 0.05F;
				entity.crystalRotation += entity.crystalVelocity;
				if (entity.crystalRotation >= 360.0F) {
					entity.crystalRotation -= 360.0F;
				} else if (entity.crystalRotation <= 360.0F) {
					entity.crystalRotation += 360.0F;
				}
				if (Math.abs(entity.crystalVelocity) <= 1.0F && level.getRandom().nextInt(15) == 0) {
					entity.crystalVelocity = level.getRandom().nextFloat() * 18.0F - 9.0F;
				}
			}
			if (entity.countUp && entity.itemBob <= 20) {
				entity.itemBob++;
				if (entity.itemBob == 20)
					entity.countUp = false;
			}
			if (!entity.countUp && entity.itemBob >= 0) {
				entity.itemBob--;
				if (entity.itemBob == 0)
					entity.countUp = true;
			}
			return;
		}

		//To keep infusion time on client in sync
		if (entity.infusionTime > 0 && entity.infusionTime % 20 == 0) {
			entity.markForUpdate(level, pos, state);
		}

		if (entity.stirProgress < 90) {
			entity.stirProgress++;
			entity.markForUpdate(level, pos, state);
		}
		if (entity.stirProgress == 89) {
			if (entity.temp == 100 && !entity.hasInfusion) {
				if (entity.hasIngredients()) {
					entity.hasInfusion = true;
					entity.markForUpdate(level, pos, state);
				}
			}
			entity.evaporation = 0;
		}
		if (entity.isHeatSource(level.getBlockState(pos.below())) && entity.temp < 100 && entity.getWaterAmount() > 0) {
			if (level.getGameTime() % 12 == 0) {
				entity.temp++;
				entity.markForUpdate(level, pos, state);
			}
		}
		if (!entity.isHeatSource(level.getBlockState(pos.below())) && entity.temp > 0) {
			if (level.getGameTime() % 6 == 0) {
				entity.temp--;
				entity.markForUpdate(level, pos, state);
			}
		}
		if (entity.temp == 100) {
			entity.evaporation++;
			if (entity.evaporation == 600 && entity.getWaterAmount() >= FluidType.BUCKET_VOLUME) {
				entity.extractFluids(level, pos, state, new FluidStack(FluidRegistry.SWAMP_WATER_STILL.get(), FluidType.BUCKET_VOLUME));
			}
			entity.markForUpdate(level, pos, state);
		}
		if (entity.temp < 100 && entity.evaporation > 0) {
			entity.evaporation--;
			entity.markForUpdate(level, pos, state);
		}
		if (entity.isValidCrystalInstalled()) {
			if (entity.temp >= 100 && entity.evaporation >= 400 && entity.stirProgress >= 90 && entity.hasIngredients()) {
				entity.getItems().get(MAX_INGREDIENTS + 1).setDamageValue(entity.getItems().get(MAX_INGREDIENTS + 1).getDamageValue() + 1);
				entity.stirProgress = 0;
			}
			if (!entity.hasCrystal) {
				entity.hasCrystal = true;
				entity.markForUpdate(level, pos, state);
			}
		} else {
			if (entity.hasCrystal) {
				entity.hasCrystal = false;
				entity.markForUpdate(level, pos, state);
			}
		}
	}

	//TODO turn into tag
	private boolean isHeatSource(BlockState state) {
		return state.is(BlockRegistry.SMOULDERING_PEAT) || state.is(BlockTags.FIRE);
	}

	/**
	 * Returns the current infusing state:
	 * 0 = no progress, 1 = in progress, 2 = finished, 3 = failed
	 */
	public int getInfusingState() {
		return this.currentInfusionState;
	}

	/**
	 * Returns the infusion color gradient ticks
	 *
	 * @return
	 */
	public int getInfusionColorGradientTicks() {
		return this.infusionColorGradientTicks;
	}

	public void extractFluids(Level level, BlockPos pos, BlockState state, FluidStack fluid) {
		if (FluidStack.isSameFluidSameComponents(fluid, this.waterTank.getFluid())) {
			this.waterTank.drain(fluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
		}
		if (this.getWaterAmount() == 0) {
			if (this.hasInfusion) {
				for (int i = 0; i <= InfuserBlockEntity.MAX_INGREDIENTS; i++) {
					ItemStack stack = this.getItem(i);
					//TODO
//					if (!stack.isEmpty() && stack.is(ItemRegistry.ASPECT_VIAL)) {
//						//Return empty vials
//						ItemStack ret = ItemStack.EMPTY;
//						switch (stack.getItemDamage()) {
//							case 0:
//							default:
//								ret = new ItemStack(ItemRegistry.DENTROTHYST_VIAL, 1, 0);
//								break;
//							case 1:
//								ret = new ItemStack(ItemRegistry.DENTROTHYST_VIAL, 1, 2);
//								break;
//						}
//						ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, ret);
//						level.addFreshEntity(entity);
//					}
					this.setItem(i, ItemStack.EMPTY);
				}
				if (this.evaporation == 600) {
//					GasCloud gasCloud = new GasCloud(level);
//					if (this.infusingRecipe != null) {
//						float[] color = ElixirRecipe.getInfusionColor(this.infusingRecipe, this.infusionTime);
//						gasCloud.setGasColor((int)(color[0] * 255), (int)(color[1] * 255), (int)(color[2] * 255), 170);
//					}
//					gasCloud.moveTo(pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, Mth.wrapDegrees(level.getRandom().nextFloat() * 360.0F), 0.0F);
//					level.addFreshEntity(gasCloud);
				}
				this.infusingRecipe = null;
			}
			this.hasInfusion = false;
			this.temp = 0;
			this.waterTank.setFluid(new FluidStack(FluidRegistry.SWAMP_WATER_STILL, 0));
		}
		this.evaporation = 0;
		this.markForUpdate(level, pos, state);
	}

	public void markForUpdate(Level level, BlockPos pos, BlockState state) {
		level.sendBlockUpdated(pos, state, state, 2);
	}

	public int getWaterAmount() {
		return this.waterTank.getFluidAmount();
	}

	public int getTanksFullValue() {
		return this.waterTank.getCapacity();
	}

	public int getScaledWaterAmount(int scale) {
		return !this.waterTank.getFluid().isEmpty() ? (int) ((float) this.waterTank.getFluid().getAmount() / (float) this.waterTank.getCapacity() * scale) : 0;
	}

	public boolean isValidCrystalInstalled() {
		return !this.getItems().get(MAX_INGREDIENTS + 1).isEmpty() && this.getItems().get(MAX_INGREDIENTS + 1).getItem() instanceof LifeCrystalItem && this.getItems().get(MAX_INGREDIENTS + 1).getDamageValue() < this.getItems().get(MAX_INGREDIENTS + 1).getMaxDamage();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return !hasInfusion() && this.getItem(slot).isEmpty() && ((slot <= MAX_INGREDIENTS && !AspectContainerItem.fromItem(stack, AspectManager.get(level)).getAspects().isEmpty()) || (slot == MAX_INGREDIENTS + 1 && stack.getItem() instanceof LifeCrystalItem));
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		BlockState state = this.getLevel().getBlockState(this.getBlockPos());
		this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 2);
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
	public int getContainerSize() {
		return MAX_INGREDIENTS + 2;
	}

	public boolean hasIngredients() {
		for (int i = 0; i <= MAX_INGREDIENTS; i++) {
			if (!this.getItems().get(i).isEmpty()) return true;
		}
		return false;
	}

	public List<Holder<AspectType>> getInfusingAspects(Level level) {
		List<Holder<AspectType>> infusingAspects = new ArrayList<>();
		for (int i = 0; i <= MAX_INGREDIENTS; i++) {
			if (!this.getItems().get(i).isEmpty()) {
				AspectContainerItem container = AspectContainerItem.fromItem(this.getItems().get(i), AspectManager.get(level));
				for (Aspect aspect : container.getAspects()) {
					infusingAspects.add(aspect.type());
				}
			}
		}
		return infusingAspects;
	}

	public boolean hasFullIngredients() {
		for (int i = 0; i <= MAX_INGREDIENTS; i++) {
			if (this.getItems().get(i).isEmpty()) return false;
		}
		return true;
	}

	public int getInfusionTime() {
		return this.infusionTime;
	}

	public float getCrystalRotation() {
		return this.crystalRotation;
	}

	public int getEvaporation() {
		return this.evaporation;
	}

	public boolean hasInfusion() {
		return this.hasInfusion;
	}

	public int getItemBob() {
		return this.itemBob;
	}

	public int getStirProgress() {
		return this.stirProgress;
	}

	public int getTemperature() {
		return this.temp;
	}

	public void setStirProgress(int progress) {
		this.stirProgress = progress;
	}

	@Nullable
	public Holder<ElixirRecipe> getInfusingRecipe() {
		return this.infusingRecipe;
	}

	//TODO
	public void updateInfusingRecipe() {
		if (this.getLevel() != null) {
//			this.infusingRecipe = ElixirRecipes.getFromAspects(this.getInfusingAspects(this.getLevel()));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.put("water_tank", this.waterTank.writeToNBT(registries, new CompoundTag()));
		tag.putInt("stir_progress", this.stirProgress);
		tag.putInt("evaporation", this.evaporation);
		tag.putInt("temperature", this.temp);
		tag.putInt("infusion_time", this.infusionTime);
		tag.putBoolean("has_infusion", this.hasInfusion);
		tag.putBoolean("has_crystal", this.hasCrystal);
		tag.putInt("infusion_state", this.currentInfusionState);
		tag.putInt("infusion_color_gradient_ticks", this.infusionColorGradientTicks);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.waterTank.readFromNBT(registries, tag.getCompound("water_tank"));
		this.stirProgress = tag.getInt("stir_progress");
		this.evaporation = tag.getInt("evaporation");
		this.temp = tag.getInt("temperature");
		this.infusionTime = tag.getInt("infusion_time");
		this.hasInfusion = tag.getBoolean("has_infusion");
		this.hasCrystal = tag.getBoolean("has_crystal");
		this.currentInfusionState = tag.getInt("infusion_state");
		this.infusionColorGradientTicks = tag.getInt("infusion_color_gradient_ticks");
		this.updateRecipe = true;
	}

	@Override
	public int getTanks() {
		return this.waterTank.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.waterTank.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.waterTank.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return this.waterTank.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (this.hasInfusion) {
			return 0; //Don't allow refill when infusing has already started
		}
		int filled = this.waterTank.fill(resource, FluidAction.SIMULATE);
		if (filled == resource.getAmount() && action.execute()) {
			this.waterTank.fill(resource, FluidAction.EXECUTE);
			if (temp >= 3) {
				temp = temp - temp / 3;
				evaporation = 0;
			}

			if (action.execute()) {
				this.setChanged();
				this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
			}
		}
		return filled;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}
