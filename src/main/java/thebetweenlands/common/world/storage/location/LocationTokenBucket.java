package thebetweenlands.common.world.storage.location;

import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.ITickable;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.config.BetweenlandsConfig;

public class LocationTokenBucket extends LocationStorage implements ITickable {

	private static class Ticket {
		private final ResourceLocation key;
		private final AABB area;

		private int counter;

		private float reservedTokenFraction;
		private long reservedTokens;

		private boolean loaded = true;

		private Ticket(ResourceLocation key, AABB area) {
			this.key = key;
			this.area = area;
		}
	}

	private ResourceLocation bucketId;

	private boolean useWorldtime = true;
	private int expirationTime = 1200;
	private int minTokensPerTick = 1, maxTokensPerTick = 1;
	private double limitMultiplier = 0.99D;

	private final List<Ticket> tickets = new ArrayList<>();
	private final Map<ResourceLocation, Ticket> key2tickets = new HashMap<>();

	private long lastCheckedTime;

	private float sharedTokenFraction;
	private long sharedTokens;

	public LocationTokenBucket(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region, "token_bucket", EnumLocationType.NONE);
	}

	public LocationTokenBucket(IWorldStorage worldStorage, StorageID id, LocalRegion region, AABB aabb, ResourceLocation bucketId) {
		this(worldStorage, id, region);
		this.addBounds(aabb);
		this.bucketId = bucketId;
		this.lastCheckedTime = worldStorage.getLevel().getGameTime();
	}

	public ResourceLocation getBucketId() {
		return this.bucketId;
	}

	public LocationTokenBucket setUseWorldTime(boolean useWorldTime) {
		this.useWorldtime = useWorldTime;
		this.markDirty();
		return this;
	}

	public LocationTokenBucket setExpirationTime(int time) {
		this.expirationTime = time;
		this.markDirty();
		return this;
	}

	public void refreshTicket(ResourceLocation key, AABB area) {
		if (area.intersects(this.getBoundingBox())) {
			Ticket ticket = this.key2tickets.get(key);
			if (ticket == null) {
				ticket = new Ticket(key, area);
				this.tickets.add(ticket);
				this.key2tickets.put(key, ticket);
			}
			ticket.counter = 0;
			this.sharedTokens += ticket.reservedTokens;
			ticket.reservedTokens = 0;
			this.markDirty();
		}
	}

	public LocationTokenBucket setMinTokensPerTick(int min) {
		this.minTokensPerTick = Mth.clamp(min, 0, this.maxTokensPerTick);
		this.markDirty();
		return this;
	}

	public LocationTokenBucket setMaxTokensPerTick(int max) {
		this.maxTokensPerTick = Math.max(this.minTokensPerTick, max);
		this.markDirty();
		return this;
	}

	public LocationTokenBucket setTokensPerTick(int min, int max) {
		this.minTokensPerTick = Math.max(0, min);
		this.setMaxTokensPerTick(max);
		this.markDirty();
		return this;
	}

	public LocationTokenBucket setLimitMultiplier(double multiplier) {
		this.limitMultiplier = multiplier;
		return this;
	}

	public long getTokens() {
		return this.sharedTokens;
	}

	public long consumeTokens(long tokens) {
		long consumed = Math.max(0, Math.min(tokens, this.sharedTokens));
		this.sharedTokens -= consumed;
		this.markDirty();
		return consumed;
	}

	public boolean consumeTokensAtomically(long tokens) {
		if (this.sharedTokens >= tokens) {
			this.sharedTokens -= tokens;
			this.markDirty();
			return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		super.readFromNBT(tag);

		this.bucketId = ResourceLocation.parse(tag.getString("bucketId"));
		this.useWorldtime = tag.getBoolean("fillWhenUnloaded");
		this.expirationTime = tag.getInt("expirationTime");
		this.minTokensPerTick = tag.getInt("minTokensPerTick");
		this.maxTokensPerTick = tag.getInt("maxTokensPerTick");

		this.lastCheckedTime = tag.getLong("lastCheckedTime");
		this.sharedTokens = tag.getLong("tokens");

		this.tickets.clear();
		this.key2tickets.clear();
		ListTag ticketsTag = tag.getList("tickets", Tag.TAG_COMPOUND);
		for (int i = 0; i < ticketsTag.size(); i++) {
			CompoundTag ticketTag = ticketsTag.getCompound(i);
			ResourceLocation key = ResourceLocation.parse(ticketTag.getString("key"));
			Ticket ticket = new Ticket(key, new AABB(
				ticketTag.getDouble("minX"), ticketTag.getDouble("minY"), ticketTag.getDouble("minZ"),
				ticketTag.getDouble("maxX"), ticketTag.getDouble("ticketTag"), ticketTag.getDouble("maxZ")
			));
			ticket.counter = ticketTag.getInt("counter");
			ticket.reservedTokens = ticketTag.getLong("reservedTokens");
			this.tickets.add(ticket);
			this.key2tickets.put(key, ticket);
		}
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag) {
		tag = super.writeToNBT(tag);

		tag.putString("bucketId", this.bucketId.toString());
		tag.putBoolean("fillWhenUnloaded", this.useWorldtime);
		tag.putInt("expirationTime", this.expirationTime);
		tag.putInt("minTokensPerTick", this.minTokensPerTick);
		tag.putInt("maxTokensPerTick", this.maxTokensPerTick);

		tag.putLong("lastCheckedTime", this.lastCheckedTime);
		tag.putLong("tokens", this.sharedTokens);

		ListTag ticketsTag = new ListTag();
		for (Ticket ticket : this.tickets) {
			CompoundTag ticketTag = new CompoundTag();
			ticketTag.putInt("counter", ticket.counter);
			ticketTag.putLong("reservedTokens", ticket.reservedTokens);
			ticketTag.putString("key", ticket.key.toString());
			ticketTag.putDouble("minX", ticket.area.minX);
			ticketTag.putDouble("minY", ticket.area.minY);
			ticketTag.putDouble("minZ", ticket.area.minZ);
			ticketTag.putDouble("maxX", ticket.area.maxX);
			ticketTag.putDouble("maxY", ticket.area.maxY);
			ticketTag.putDouble("maxZ", ticket.area.maxZ);

			ticketsTag.add(ticketTag);
		}
		tag.put("tickets", ticketsTag);

		return tag;
	}

	@Override
	public void tick() {
		Level level = this.getWorldStorage().getLevel();

		if (level.isClientSide()) {
			return;
		}

		BlockPos.MutableBlockPos checkPos1 = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos checkPos2 = new BlockPos.MutableBlockPos();

		Iterator<Ticket> it = this.tickets.iterator();
		while (it.hasNext()) {
			Ticket ticket = it.next();

			checkPos1.set(ticket.area.minX, ticket.area.minY, ticket.area.minZ);
			checkPos2.set(ticket.area.maxX, ticket.area.maxY, ticket.area.maxZ);

			@SuppressWarnings("deprecation")
			boolean areaLoaded = level.hasChunksAt(checkPos1, checkPos2);
			ticket.loaded = areaLoaded;

			if (ticket.loaded) {
				ticket.counter++;

				if (ticket.counter >= this.expirationTime) {
					it.remove();
					this.key2tickets.remove(ticket.key);
				}
			}
		}

		if (this.tickets.isEmpty()) {
			this.getWorldStorage().getLocalStorageHandler().removeLocalStorage(this);
			return;
		}

		long worldTime = level.getGameTime();
		long ticks = this.useWorldtime ? Math.max(worldTime - this.lastCheckedTime, 0) : 1;
		this.lastCheckedTime = worldTime;

		long decay = Math.max(this.sharedTokens - (long) Math.floor(this.sharedTokens * this.limitMultiplier) - 1, 0);

		long newTokens = Math.max(this.generateTokens(ticks) - decay, 0);

		int n = this.tickets.size();

		if (newTokens < n) {
			for (int i = 0; i < newTokens; i++) {
				Ticket ticket = this.tickets.get(level.getRandom().nextInt(n));

				if (ticket.loaded) {
					this.sharedTokens++;
				} else {
					ticket.reservedTokens++;
				}
			}
		} else {
			long f = newTokens / n;
			long r = newTokens % n;

			int i = 0;
			for (Ticket ticket : this.tickets) {
				long ticketFraction;
				if (++i <= r) {
					ticketFraction = f + 1;
				} else {
					ticketFraction = f;
				}

				if (ticket.loaded) {
					this.sharedTokens += ticketFraction;
				} else {
					ticket.reservedTokens += ticketFraction;
				}
			}
		}

		if (BetweenlandsConfig.debug && worldTime % 20 == 0) {
			long reservedTokens = 0;
			for (Ticket ticket : this.tickets) {
				reservedTokens += ticket.reservedTokens;
			}
			this.setName("token_bucket_" + this.bucketId + "_" + this.sharedTokens + "_" + reservedTokens);
		}

		this.markDirty();
	}

	protected long generateTokens(long ticks) {
		if (ticks == 1) {
			return this.getWorldStorage().getLevel().getRandom().nextInt(Math.max(this.maxTokensPerTick - this.minTokensPerTick, 0) + 1) + this.minTokensPerTick;
		} else {
			float tokens = ticks * (this.maxTokensPerTick + this.minTokensPerTick) / 2.0f;
			return this.getWorldStorage().getLevel().getRandom().nextBoolean() ? Mth.floor(tokens) : Mth.ceil(tokens);
		}
	}

}
