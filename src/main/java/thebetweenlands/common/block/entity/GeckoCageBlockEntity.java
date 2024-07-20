package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.AspectType;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class GeckoCageBlockEntity extends SyncedBlockEntity {

	private int ticks = 0;
	private int prevTicks = 0;
	private int recoverTicks = 0;
	@Nullable
	private AspectType aspectType = null;
	private int geckoUsages = 0;
	@Nullable
	private String geckoName;

	public GeckoCageBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.GECKO_CAGE.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, GeckoCageBlockEntity entity) {
		entity.prevTicks = entity.ticks;
		++entity.ticks;
		if (!level.isClientSide()) {
			if (entity.recoverTicks > 0) {
				--entity.recoverTicks;
				if (entity.recoverTicks == 0) {
					level.sendBlockUpdated(pos, state, state, 2);
				}
			} else {
				if (entity.aspectType != null && entity.geckoUsages == 0) {
					entity.geckoName = "";
					level.sendBlockUpdated(pos, state, state, 2);
				}
				if (entity.aspectType != null) {
					entity.aspectType = null;
					level.sendBlockUpdated(pos, state, state, 2);
				}
			}
		}
	}

	public int getTicks() {
		return this.ticks;
	}

	public float getInterpolatedTicks(float delta) {
		return this.prevTicks + (this.ticks - this.prevTicks) * delta;
	}

	@Nullable
	public AspectType getAspectType() {
		return this.aspectType;
	}

	public void setAspectType(Level level, BlockPos pos, BlockState state, AspectType type, int recoverTime) {
		--this.geckoUsages;
		this.aspectType = type;
		this.recoverTicks = recoverTime;
		level.sendBlockUpdated(pos, state, state, 2);
		if (!this.hasGecko())
			this.recoverTicks = 0;
	}

	public boolean hasGecko() {
		return this.geckoUsages > 0;
	}

	public void setGeckoUsages(Level level, BlockPos pos, BlockState state, int usages) {
		this.geckoUsages = usages;
		this.setChanged();
		level.sendBlockUpdated(pos, state, state, 2);
	}

	public int getGeckoUsages() {
		return this.geckoUsages;
	}

	@Nullable
	public String getGeckoName() {
		return this.geckoName;
	}

	public void addGecko(Level level, BlockPos pos, BlockState state, int usages, @Nullable String name) {
		this.geckoUsages = usages;
		this.geckoName = name;
		this.ticks = 0;
		this.setChanged();
		level.sendBlockUpdated(pos, state, state, 2);
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("recover_ticks", this.recoverTicks);
		tag.putInt("gecko_usages", this.geckoUsages);
		if (this.geckoName != null) {
			tag.putString("gecko_name", this.geckoName);
		}
		tag.putString("aspect_type", this.aspectType == null ? "" : this.aspectType.getName());
		tag.putInt("ticks", this.ticks);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.recoverTicks = tag.getInt("recover_ticks");
		this.geckoUsages = tag.getInt("gecko_usages");
		if (tag.contains("gecko_name", Tag.TAG_STRING)) {
			this.geckoName = tag.getString("gecko_name");
		} else {
			this.geckoName = null;
		}
		this.aspectType = BLRegistries.ASPECTS.get(ResourceLocation.tryParse(tag.getString("aspect_type")));
		this.ticks = tag.getInt("ticks");
	}
}
