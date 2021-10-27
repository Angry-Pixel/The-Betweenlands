package thebetweenlands.common.world.storage.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.config.BetweenlandsConfig;

public class LocationTokenBucket extends LocationStorage implements ITickable {

	private static class Ticket {
		private final ResourceLocation key;
		private final AxisAlignedBB area;

		private int counter;

		private float reservedTokenFraction;
		private long reservedTokens;

		private boolean loaded = true;

		private Ticket(ResourceLocation key, AxisAlignedBB area) {
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

	public LocationTokenBucket(IWorldStorage worldStorage, StorageID id, LocalRegion region, AxisAlignedBB aabb, ResourceLocation bucketId) {
		this(worldStorage, id, region);
		this.addBounds(aabb);
		this.bucketId = bucketId;
		this.lastCheckedTime = worldStorage.getWorld().getTotalWorldTime();
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

	public void refreshTicket(ResourceLocation key, AxisAlignedBB area) {
		if(area.intersects(this.getBoundingBox())) {
			Ticket ticket = this.key2tickets.get(key);
			if(ticket == null) {
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
		this.minTokensPerTick = MathHelper.clamp(min, 0, this.maxTokensPerTick);
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
		if(this.sharedTokens >= tokens) {
			this.sharedTokens -= tokens;
			this.markDirty();
			return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.bucketId = new ResourceLocation(nbt.getString("bucketId"));
		this.useWorldtime = nbt.getBoolean("fillWhenUnloaded");
		this.expirationTime = nbt.getInteger("expirationTime");
		this.minTokensPerTick = nbt.getInteger("minTokensPerTick");
		this.maxTokensPerTick = nbt.getInteger("maxTokensPerTick");

		this.lastCheckedTime = nbt.getLong("lastCheckedTime");
		this.sharedTokens = nbt.getLong("tokens");

		this.tickets.clear();
		this.key2tickets.clear();
		NBTTagList ticketsNbt = nbt.getTagList("tickets", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < ticketsNbt.tagCount(); i++) {
			NBTTagCompound ticketNbt = ticketsNbt.getCompoundTagAt(i);
			ResourceLocation key = new ResourceLocation(ticketNbt.getString("key"));
			Ticket ticket = new Ticket(key, new AxisAlignedBB(
					ticketNbt.getDouble("minX"), ticketNbt.getDouble("minY"), ticketNbt.getDouble("minZ"),
					ticketNbt.getDouble("maxX"), ticketNbt.getDouble("maxY"), ticketNbt.getDouble("maxZ")
					));
			ticket.counter = ticketNbt.getInteger("counter");
			ticket.reservedTokens = ticketNbt.getLong("reservedTokens");
			this.tickets.add(ticket);
			this.key2tickets.put(key, ticket);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);

		nbt.setString("bucketId", this.bucketId.toString());
		nbt.setBoolean("fillWhenUnloaded", this.useWorldtime);
		nbt.setInteger("expirationTime", this.expirationTime);
		nbt.setInteger("minTokensPerTick", this.minTokensPerTick);
		nbt.setInteger("maxTokensPerTick", this.maxTokensPerTick);

		nbt.setLong("lastCheckedTime", this.lastCheckedTime);
		nbt.setLong("tokens", this.sharedTokens);

		NBTTagList ticketsNbt = new NBTTagList();
		for(Ticket ticket : this.tickets) {
			NBTTagCompound ticketNbt = new NBTTagCompound();
			ticketNbt.setInteger("counter", ticket.counter);
			ticketNbt.setLong("reservedTokens", ticket.reservedTokens);
			ticketNbt.setString("key", ticket.key.toString());
			ticketNbt.setDouble("minX", ticket.area.minX);
			ticketNbt.setDouble("minY", ticket.area.minY);
			ticketNbt.setDouble("minZ", ticket.area.minZ);
			ticketNbt.setDouble("maxX", ticket.area.maxX);
			ticketNbt.setDouble("maxY", ticket.area.maxY);
			ticketNbt.setDouble("maxZ", ticket.area.maxZ);

			ticketsNbt.appendTag(ticketNbt);
		}
		nbt.setTag("tickets", ticketsNbt);

		return nbt;
	}

	@Override
	public void update() {
		World world = this.getWorldStorage().getWorld();

		if(world.isRemote) {
			return;
		}

		PooledMutableBlockPos checkPos1 = PooledMutableBlockPos.retain();
		PooledMutableBlockPos checkPos2 = PooledMutableBlockPos.retain();

		try {
			Iterator<Ticket> it = this.tickets.iterator();
			while(it.hasNext()) {
				Ticket ticket = it.next();

				checkPos1.setPos(ticket.area.minX, ticket.area.minY, ticket.area.minZ);
				checkPos2.setPos(ticket.area.maxX, ticket.area.maxY, ticket.area.maxZ);

				ticket.loaded = world.isAreaLoaded(checkPos1, checkPos2);

				if(ticket.loaded) {
					ticket.counter++;

					if(ticket.counter >= this.expirationTime) {
						it.remove();
						this.key2tickets.remove(ticket.key);
					}
				}
			}
		} finally {
			checkPos1.release();
			checkPos2.release();
		}

		if(this.tickets.isEmpty()) {
			this.getWorldStorage().getLocalStorageHandler().removeLocalStorage(this);
			return;
		}

		long worldTime = world.getTotalWorldTime();
		long ticks = this.useWorldtime ? Math.max(worldTime - this.lastCheckedTime, 0) : 1;
		this.lastCheckedTime = worldTime;

		long decay = Math.max(this.sharedTokens - (long)Math.floor(this.sharedTokens * this.limitMultiplier) - 1, 0);

		long newTokens = Math.max(this.generateTokens(ticks) - decay, 0);

		int n = this.tickets.size();

		if(newTokens < n) {
			for(int i = 0; i < newTokens; i++) {
				Ticket ticket = this.tickets.get(world.rand.nextInt(n));

				if(ticket.loaded) {
					this.sharedTokens++;
				} else {
					ticket.reservedTokens++;
				}
			}
		} else {
			long f = newTokens / n;
			long r = newTokens % n;

			int i = 0;
			for(Ticket ticket : this.tickets) {
				long ticketFraction;
				if(++i <= r) {
					ticketFraction = f + 1;
				} else {
					ticketFraction = f;
				}

				if(ticket.loaded) {
					this.sharedTokens += ticketFraction;
				} else {
					ticket.reservedTokens += ticketFraction;
				}
			}
		}

		if(BetweenlandsConfig.DEBUG.debug && worldTime % 20 == 0) {
			int reservedTokens = 0;
			for(Ticket ticket : this.tickets) {
				reservedTokens += ticket.reservedTokens;
			}
			this.setName("token_bucket_" + this.bucketId + "_" + this.sharedTokens + "_" + reservedTokens);
		}

		this.markDirty();
	}

	protected long generateTokens(long ticks) {
		if(ticks == 1) {
			return this.getWorldStorage().getWorld().rand.nextInt(Math.max(this.maxTokensPerTick - this.minTokensPerTick, 0) + 1) + this.minTokensPerTick;
		} else {
			float tokens = ticks * (this.maxTokensPerTick + this.minTokensPerTick) / 2.0f;
			return this.getWorldStorage().getWorld().rand.nextBoolean() ? MathHelper.floor(tokens) : MathHelper.ceil(tokens);
		}
	}

}
