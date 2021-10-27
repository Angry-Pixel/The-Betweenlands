package thebetweenlands.common.world.storage.location;

import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class TokenBucket {
	private World world;
	private BlockPos pos;

	private ResourceLocation bucketId;
	private float sizeX, sizeY, sizeZ;
	private int minTokensPerTick = 1;
	private int maxTokensPerTick = 1;
	private double limitMultiplier = 0.99D;
	private double tokensConsumedPerTick = 1;

	private long lastCheckedTime = Long.MIN_VALUE;
	private double consumedTokenFraction;

	public TokenBucket() { }

	public TokenBucket(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}
	
	public TokenBucket(ResourceLocation bucketId, float sizeX, float sizeY, float sizeZ, int minTokensPerTick, int maxTokensPerTick, double limitMultiplier, double tokensConsumedPerTick) {
		this.bucketId = bucketId;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.minTokensPerTick = minTokensPerTick;
		this.maxTokensPerTick = maxTokensPerTick;
		this.limitMultiplier = limitMultiplier;
		this.tokensConsumedPerTick = tokensConsumedPerTick;
	}

	public TokenBucket setWorld(World world) {
		this.world = world;
		if(this.lastCheckedTime == Long.MIN_VALUE) {
			this.lastCheckedTime = world.getTotalWorldTime();
		}
		return this;
	}

	public TokenBucket setPos(BlockPos pos) {
		this.pos = pos;
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("bucketId", this.bucketId.toString());
		nbt.setFloat("sizeX", this.sizeX);
		nbt.setFloat("sizeY", this.sizeY);
		nbt.setFloat("sizeZ", this.sizeZ);
		nbt.setInteger("minTokensPerTick", this.minTokensPerTick);
		nbt.setInteger("maxTokensPerTick", this.maxTokensPerTick);
		nbt.setDouble("limitMultiplier", this.limitMultiplier);
		nbt.setDouble("tokensConsumedPerTick", this.tokensConsumedPerTick);

		nbt.setLong("lastCheckedTime", this.lastCheckedTime);
		nbt.setDouble("consumedTokenFraction", this.consumedTokenFraction);

		return nbt;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		this.bucketId = new ResourceLocation(nbt.getString("bucketId"));
		this.sizeX = nbt.getFloat("sizeX");
		this.sizeY = nbt.getFloat("sizeY");
		this.sizeZ = nbt.getFloat("sizeZ");
		this.minTokensPerTick = nbt.getInteger("minTokensPerTick");
		this.maxTokensPerTick = nbt.getInteger("maxTokensPerTick");
		this.limitMultiplier = nbt.getDouble("limitMultiplier");
		this.tokensConsumedPerTick = nbt.getDouble("tokensConsumedPerTick");

		this.lastCheckedTime = nbt.getLong("lastCheckedTime");
		this.consumedTokenFraction = nbt.getDouble("consumedTokenFraction");
	}

	public long consume() {
		if(this.world == null || this.pos == null || this.bucketId == null) {
			return 0;
		}

		IWorldStorage storage = BetweenlandsWorldStorage.forWorld(this.world);
		ILocalStorageHandler handler = storage.getLocalStorageHandler();

		LocationTokenBucket tokenBucket = null;

		AxisAlignedBB posAabb = new AxisAlignedBB(this.pos);

		List<LocationTokenBucket> locations = handler.getLocalStorages(LocationTokenBucket.class, posAabb, location -> this.bucketId.equals(location.getBucketId()));
		if(!locations.isEmpty()) {
			tokenBucket = locations.get(0);
		}

		if(tokenBucket == null && !this.world.isRemote) {
			tokenBucket = new LocationTokenBucket(storage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(this.pos), posAabb.grow(this.sizeX * 0.5f - 0.49f, this.sizeY * 0.5f - 0.49f, this.sizeZ * 0.5f - 0.49f), this.bucketId);
			tokenBucket.setTokensPerTick(this.minTokensPerTick, this.maxTokensPerTick);
			tokenBucket.setLimitMultiplier(this.limitMultiplier);
			handler.addLocalStorage(tokenBucket);
		}

		if(tokenBucket != null) {
			long worldTime = this.world.getTotalWorldTime();
			long ticks = Math.max(worldTime - this.lastCheckedTime, 0);

			double consumedTokens = ticks * this.tokensConsumedPerTick + this.consumedTokenFraction;
			long wholeConsumedTokens = MathHelper.floor(consumedTokens);

			if(wholeConsumedTokens > 0) {
				this.lastCheckedTime = worldTime;
				this.consumedTokenFraction = consumedTokens - wholeConsumedTokens;

				tokenBucket.refreshTicket(new ResourceLocation(this.bucketId.getNamespace(), String.format("%d_%d_%d", this.pos.getX(), this.pos.getY(), this.pos.getZ())), posAabb);

				return tokenBucket.consumeTokens(wholeConsumedTokens);
			}
		}

		return 0;
	}
}
