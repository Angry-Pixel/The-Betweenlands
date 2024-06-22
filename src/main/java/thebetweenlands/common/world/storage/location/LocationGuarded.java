package thebetweenlands.common.world.storage.location;

public class LocationGuarded extends LocationStorage implements ITickable {
	private BlockLocationGuard guard = new BlockLocationGuard() {
		@Override
		public boolean setGuarded(World world, BlockPos pos, boolean guarded) {
			if (super.setGuarded(world, pos, guarded)) {
				LocationGuarded.this.setDirty(true);
				if (!LocationGuarded.this.getWatchers().isEmpty()) {
					LocationGuarded.this.queuedChanges.add(new BlockPos(pos.getX() / 16, pos.getY() / 16, pos.getZ() / 16));
				}
				return true;
			}
			return false;
		}

		@Override
		public void clear(World world) {
			super.clear(world);
			LocationGuarded.this.setDirty(true);
			if (!LocationGuarded.this.getWatchers().isEmpty()) {
				LocationGuarded.this.queuedChanges.clear();
				LocationGuarded.this.queuedClear = true;
			}
		}
	};

	private Set<BlockPos> queuedChanges = new HashSet<>();
	private boolean queuedClear;

	public LocationGuarded(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		super(worldStorage, id, region);
	}

	public LocationGuarded(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region, String name, EnumLocationType type) {
		super(worldStorage, id, region, name, type);
	}

	@Override
	public BlockLocationGuard getGuard() {
		return this.guard;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.readGuardNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		this.writeGuardNBT(nbt);
		return nbt;
	}

	@Override
	public void onWatched(EntityPlayerMP player) {
		super.onWatched(player);
		MessageBlockGuardData message = new MessageBlockGuardData(this);
		this.sendDataToPlayer(message, player);
	}

	@Override
	public void update() {
		if (this.queuedClear) {
			MessageClearBlockGuard message = new MessageClearBlockGuard(this);
			for (EntityPlayerMP watcher : this.getWatchers()) {
				this.sendDataToPlayer(message, watcher);
			}
			this.queuedClear = false;
		} else if (!this.queuedChanges.isEmpty()) {
			if (this.getWatchers().isEmpty()) {
				this.queuedChanges.clear();
			} else {
				Iterator<BlockPos> it = this.queuedChanges.iterator();
				while (it.hasNext()) {
					BlockPos pos = it.next();
					BlockPos worldPos = new BlockPos(pos.getX() * 16, pos.getY() * 16, pos.getZ() * 16);
					GuardChunkSection section = this.guard.getSection(worldPos);
					MessageBlockGuardSectionChange message = new MessageBlockGuardSectionChange(this, worldPos, section);
					for (EntityPlayerMP watcher : this.getWatchers()) {
						this.sendDataToPlayer(message, watcher);
					}
					it.remove();
				}
			}
		}
	}
}
