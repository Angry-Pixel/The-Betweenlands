package thebetweenlands.common.world.storage.location;

import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

import javax.annotation.Nullable;

public class TokenBucket {

	@Nullable
	private Level level;
	@Nullable
	private BlockPos pos;
	@Nullable
	private ResourceLocation bucketId;
	private float sizeX, sizeY, sizeZ;
	private int minTokensPerTick = 1;
	private int maxTokensPerTick = 1;
	private double limitMultiplier = 0.99D;
	private double tokensConsumedPerTick = 1;

	private long lastCheckedTime = Long.MIN_VALUE;
	private double consumedTokenFraction;

	public TokenBucket() {
	}

	public TokenBucket(CompoundTag nbt) {
		this.readFromNBT(nbt);
	}

	public TokenBucket(@Nullable ResourceLocation bucketId, float sizeX, float sizeY, float sizeZ, int minTokensPerTick, int maxTokensPerTick, double limitMultiplier, double tokensConsumedPerTick) {
		this.bucketId = bucketId;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.minTokensPerTick = minTokensPerTick;
		this.maxTokensPerTick = maxTokensPerTick;
		this.limitMultiplier = limitMultiplier;
		this.tokensConsumedPerTick = tokensConsumedPerTick;
	}

	public TokenBucket setLevel(Level level) {
		this.level = level;
		if (this.lastCheckedTime == Long.MIN_VALUE) {
			this.lastCheckedTime = level.getGameTime();
		}
		return this;
	}

	public TokenBucket setPos(BlockPos pos) {
		this.pos = pos;
		return this;
	}

	public CompoundTag writeToNBT(CompoundTag tag) {
		tag.putString("bucketId", this.bucketId.toString());
		tag.putFloat("sizeX", this.sizeX);
		tag.putFloat("sizeY", this.sizeY);
		tag.putFloat("sizeZ", this.sizeZ);
		tag.putInt("minTokensPerTick", this.minTokensPerTick);
		tag.putInt("maxTokensPerTick", this.maxTokensPerTick);
		tag.putDouble("limitMultiplier", this.limitMultiplier);
		tag.putDouble("tokensConsumedPerTick", this.tokensConsumedPerTick);

		tag.putLong("lastCheckedTime", this.lastCheckedTime);
		tag.putDouble("consumedTokenFraction", this.consumedTokenFraction);

		return tag;
	}

	public void readFromNBT(CompoundTag tag) {
		this.bucketId = ResourceLocation.parse(tag.getString("bucketId"));
		this.sizeX = tag.getFloat("sizeX");
		this.sizeY = tag.getFloat("sizeY");
		this.sizeZ = tag.getFloat("sizeZ");
		this.minTokensPerTick = tag.getInt("minTokensPerTick");
		this.maxTokensPerTick = tag.getInt("maxTokensPerTick");
		this.limitMultiplier = tag.getDouble("limitMultiplier");
		this.tokensConsumedPerTick = tag.getDouble("tokensConsumedPerTick");

		this.lastCheckedTime = tag.getLong("lastCheckedTime");
		this.consumedTokenFraction = tag.getDouble("consumedTokenFraction");
	}

	public long consume() {
		if (this.level == null || this.pos == null || this.bucketId == null) {
			return 0;
		}

		IWorldStorage storage = BetweenlandsWorldStorage.get(this.level);
		if (storage != null) {
			ILocalStorageHandler handler = storage.getLocalStorageHandler();

			LocationTokenBucket tokenBucket = null;

			AABB posAabb = new AABB(this.pos);

			List<LocationTokenBucket> locations = handler.getLocalStorages(LocationTokenBucket.class, posAabb, location -> this.bucketId.equals(location.getBucketId()));
			if (!locations.isEmpty()) {
				tokenBucket = locations.getFirst();
			}

			if (tokenBucket == null && !this.level.isClientSide()) {
				tokenBucket = new LocationTokenBucket(storage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(this.pos), posAabb.inflate(this.sizeX * 0.5f - 0.49f, this.sizeY * 0.5f - 0.49f, this.sizeZ * 0.5f - 0.49f), this.bucketId, this.level.getGameTime());
				tokenBucket.setTokensPerTick(this.minTokensPerTick, this.maxTokensPerTick);
				tokenBucket.setLimitMultiplier(this.limitMultiplier);
				handler.addLocalStorage(tokenBucket);
			}

			if (tokenBucket != null) {
				long worldTime = this.level.getGameTime();
				long ticks = Math.max(worldTime - this.lastCheckedTime, 0);

				double consumedTokens = ticks * this.tokensConsumedPerTick + this.consumedTokenFraction;
				long wholeConsumedTokens = Mth.floor(consumedTokens);

				if (wholeConsumedTokens > 0) {
					this.lastCheckedTime = worldTime;
					this.consumedTokenFraction = consumedTokens - wholeConsumedTokens;

					tokenBucket.refreshTicket(ResourceLocation.fromNamespaceAndPath(this.bucketId.getNamespace(), String.format("%d_%d_%d", this.pos.getX(), this.pos.getY(), this.pos.getZ())), posAabb);

					return tokenBucket.consumeTokens(wholeConsumedTokens);
				}
			}
		}

		return 0;
	}
}
