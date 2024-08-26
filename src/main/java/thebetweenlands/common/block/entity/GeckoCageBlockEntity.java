package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.aspect.AspectType;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

public class GeckoCageBlockEntity extends SyncedBlockEntity {

	private int ticks = 0;
	private int prevTicks = 0;
	private int recoverTicks = 0;
	private Optional<Holder<AspectType>> aspectType = Optional.empty();
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
				if (entity.aspectType.isPresent() && entity.geckoUsages == 0) {
					entity.geckoName = "";
					level.sendBlockUpdated(pos, state, state, 2);
				}
				if (entity.aspectType.isPresent()) {
					entity.aspectType = Optional.empty();
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
	public Holder<AspectType> getAspectType() {
		return this.aspectType.orElse(null);
	}

	public void setAspectType(Level level, BlockPos pos, BlockState state, @Nullable Holder<AspectType> type, int recoverTime) {
		--this.geckoUsages;
		this.aspectType = Optional.ofNullable(type);
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
		this.aspectType.ifPresent(aspectTypeHolder -> AspectType.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), aspectTypeHolder).ifSuccess(tag1 -> tag.put("aspect", tag1)));
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
		if (tag.contains("aspect")) {
			AspectType.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("aspect")).ifSuccess(aspectTypeHolder -> this.aspectType = Optional.of(aspectTypeHolder));
		}
		this.ticks = tag.getInt("ticks");
	}
}
