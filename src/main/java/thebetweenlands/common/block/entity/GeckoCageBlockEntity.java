package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.AspectType;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class GeckoCageBlockEntity extends BlockEntity {

	private int ticks = 0;
	private int prevTicks = 0;
	private int recoverTicks = 0;
	private AspectType aspectType = null;
	private int geckoUsages = 0;
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
		tag.putInt("RecoverTicks", this.recoverTicks);
		tag.putInt("GeckoUsages", this.geckoUsages);
		if (this.geckoName != null) {
			tag.putString("GeckoName", this.geckoName);
		}
		tag.putString("AspectType", this.aspectType == null ? "" : this.aspectType.getName());
		tag.putInt("Ticks", this.ticks);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.recoverTicks = tag.getInt("RecoverTicks");
		this.geckoUsages = tag.getInt("GeckoUsages");
		if (tag.contains("GeckoName", Tag.TAG_STRING)) {
			this.geckoName = tag.getString("GeckoName");
		} else {
			this.geckoName = null;
		}
		this.aspectType = BLRegistries.ASPECTS.get(ResourceLocation.tryParse(tag.getString("AspectType")));
		this.ticks = tag.getInt("Ticks");
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		CompoundTag tag = packet.getTag();
		this.geckoUsages = tag.getInt("GeckoUsages");
		this.aspectType = BLRegistries.ASPECTS.get(ResourceLocation.tryParse(tag.getString("AspectType")));
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag nbt = super.getUpdateTag(registries);
		nbt.putInt("GeckoUsages", this.geckoUsages);
		nbt.putString("AspectType", this.aspectType == null ? "" : this.aspectType.getName());
		return nbt;
	}
}
