package thebetweenlands.common.world.storage.location;

import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.common.entity.EntitySplodeshroom;
import thebetweenlands.common.entity.EntityTinyWormEggSac;
import thebetweenlands.common.entity.EntityTriggeredFallingBlock;
import thebetweenlands.common.entity.EntityTriggeredSludgeWallJet;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;
import thebetweenlands.common.entity.mobs.EntityMovingSpawnerHole;
import thebetweenlands.common.entity.mobs.EntityShambler;
import thebetweenlands.common.entity.mobs.EntityWallLamprey;
import thebetweenlands.common.entity.mobs.EntityWallLivingRoot;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.AreaMobSpawner.BLSpawnEntry;
import thebetweenlands.common.world.biome.spawning.BoxMobSpawner;
import thebetweenlands.common.world.biome.spawning.spawners.ConditionalSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.WallSpawnEntry;
import thebetweenlands.common.world.storage.location.LocationAmbience.EnumLocationAmbience;

public class LocationSludgeWormDungeon extends LocationGuarded {
	protected static final DataParameter<Float> GROUND_FOG_STRENGTH = GenericDataManager.createKey(LocationSludgeWormDungeon.class, DataSerializers.FLOAT);

	private BlockPos structurePos;

	private final BoxMobSpawner mazeMobSpawner;
	private final BoxMobSpawner walkwaysMobSpawner;

	private boolean defeated = false;

	protected static final int MAX_FLOORS = 7;

	public LocationSludgeWormDungeon(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		super(worldStorage, id, region, "sludge_worm_dungeon", EnumLocationType.SLUDGE_WORM_DUNGEON);

		this.dataManager.register(GROUND_FOG_STRENGTH, 1.0F);

		this.setAmbience(new LocationAmbience(EnumLocationAmbience.SLUDGE_WORM_DUNGEON)
				.setFogColor(new int[] {120, 120, 120}).setFogRange(4.0f, 45.0f)
				.setCaveFog(false));

		this.mazeMobSpawner = new BoxMobSpawner() {
			private boolean[] playerOccupancy = new boolean[MAX_FLOORS];

			@Override
			protected void updateSpawnerChunks(WorldServer world, Set<ChunkPos> spawnerChunks) {
				super.updateSpawnerChunks(world, spawnerChunks);

				for(int i = 0; i < this.playerOccupancy.length; i++) {
					this.playerOccupancy[i] = false;
				}

				for(EntityPlayer player : world.playerEntities) {
					int floor = LocationSludgeWormDungeon.this.getFloor(player.getPosition());

					if(floor >= 0 && floor < this.playerOccupancy.length && LocationSludgeWormDungeon.this.isInside(player)) {
						this.playerOccupancy[floor] = true;
					}
				}
			}

			@Override
			public boolean isInsideSpawningArea(World world, BlockPos pos, boolean entityCount) {
				if(super.isInsideSpawningArea(world, pos, entityCount)) {
					int floor = LocationSludgeWormDungeon.this.getFloor(pos);
					return floor >= 0 && floor < this.playerOccupancy.length && this.playerOccupancy[floor];
				}
				return false;
			}
		};
		this.mazeMobSpawner.setMaxAreaEntities(80);
		this.mazeMobSpawner.setEntityCountFilter(entity -> entity instanceof EntityTriggeredFallingBlock == false); //Ignore falling blocks

		//floor 1
		this.mazeMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(0, new WallSpawnEntry(0, EntityMovingSpawnerHole.class, EntityMovingSpawnerHole::new, (short) 90), ConditionalSpawnEntry.createSludgeDungeonPredicate(0))
				.setGroupSize(1, 1).setSpawnCheckRadius(8.0D).setSpawningInterval(5 * 20).setHostile(true));

		//floor 2
		this.mazeMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(1, new BLSpawnEntry(1, EntityShambler.class, EntityShambler::new, (short) 50), ConditionalSpawnEntry.createSludgeDungeonPredicate(1))
				.setGroupSize(1, 3).setSpawnCheckRadius(14.0D).setSpawningInterval(10 * 20).setHostile(true));

		//floor 3
		this.mazeMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(2, new WallSpawnEntry(2, EntityWallLamprey.class, EntityWallLamprey::new, (short) 100), ConditionalSpawnEntry.createSludgeDungeonPredicate(2))
				.setGroupSize(1, 1).setSpawnCheckRadius(8.0D).setSpawningInterval(2 * 20).setHostile(true));
		this.mazeMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(3, new BLSpawnEntry(3, EntityTinyWormEggSac.class, EntityTinyWormEggSac::new, (short) 100), ConditionalSpawnEntry.createSludgeDungeonPredicate(2))
				.setGroupSize(1, 1).setSpawnCheckRadius(8.0D).setSpawningInterval(8 * 20).setHostile(true));

		//floor 4
		this.mazeMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(4, new BLSpawnEntry(4, EntityTriggeredSludgeWallJet.class, EntityTriggeredSludgeWallJet::new, (short) 100), ConditionalSpawnEntry.createSludgeDungeonPredicate(3))
				.setGroupSize(1, 1).setSpawnCheckRadius(10.0D).setSpawningInterval(2 * 20).setHostile(true));

		//floor 5
		this.mazeMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(5, new WallSpawnEntry(5, EntityWallLivingRoot.class ,EntityWallLivingRoot::new, (short) 100), ConditionalSpawnEntry.createSludgeDungeonPredicate(4))
				.setGroupSize(1, 1).setSpawnCheckRadius(8.0D).setSpawningInterval(2 * 20).setHostile(true));
		this.mazeMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(6, new BLSpawnEntry(6, EntitySplodeshroom.class, EntitySplodeshroom::new, (short) 100), ConditionalSpawnEntry.createSludgeDungeonPredicate(4))
				.setGroupSize(1, 1).setSpawnCheckRadius(8.0D).setSpawningInterval(2 * 20).setHostile(true));

		//floor 6
		this.mazeMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(7, new BLSpawnEntry(7, EntityCryptCrawler.class, EntityCryptCrawler::new, (short) 50), ConditionalSpawnEntry.createSludgeDungeonPredicate(5))
				.setGroupSize(1, 3).setSpawnCheckRadius(12.0D).setSpawningInterval(12 * 20).setHostile(true));


		this.walkwaysMobSpawner = new BoxMobSpawner();
		this.walkwaysMobSpawner.setMaxAreaEntities(12);
		this.walkwaysMobSpawner.setEntityCountFilter(entity -> entity instanceof EntityCryptCrawler == false);

		this.walkwaysMobSpawner.addSpawnEntry(new BLSpawnEntry(1, EntityCryptCrawler.class, EntityCryptCrawler::new, (short) 100).setGroupSize(1, 2).setSpawnCheckRadius(8.0D).setSpawningInterval(10 * 20).setHostile(true));
	}

	/**
	 * Sets the structure entrance
	 * @param pos
	 */
	public void setStructurePos(BlockPos pos) {
		this.structurePos = pos;
		this.setDirty(true);
	}

	/**
	 * Returns the structure entrance
	 * @return
	 */
	public BlockPos getStructurePos() {
		return this.structurePos;
	}

	public boolean hasGroundFog(BlockPos pos) {
		BlockPos structurePos = this.getStructurePos();

		AxisAlignedBB mazeAabb = new AxisAlignedBB(structurePos.getX(), structurePos.getY(), structurePos.getZ(), structurePos.getX() + 29, structurePos.getY() - 8 * 5 - 3, structurePos.getZ() + 29);

		return mazeAabb.intersects(new AxisAlignedBB(pos)) && this.dataManager.get(GROUND_FOG_STRENGTH) > 0.01F;
	}

	public void setDefeated(boolean defeated) {
		this.defeated = defeated;
		this.setDirty(true);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setFloat("groundFogStrength", this.dataManager.get(GROUND_FOG_STRENGTH));
		nbt.setLong("structurePos", this.structurePos.toLong());
		nbt.setBoolean("defeated", this.defeated);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.dataManager.set(GROUND_FOG_STRENGTH, nbt.getFloat("groundFogStrength"));
		this.structurePos = BlockPos.fromLong(nbt.getLong("structurePos"));
		this.defeated = nbt.getBoolean("defeated");
	}

	@Override
	protected void writeSharedNbt(NBTTagCompound nbt) {
		super.writeSharedNbt(nbt);
		nbt.setLong("structurePos", this.structurePos.toLong());
	}

	@Override
	protected void readSharedNbt(NBTTagCompound nbt) {
		super.readSharedNbt(nbt);
		this.structurePos = BlockPos.fromLong(nbt.getLong("structurePos"));
	}

	@Override
	public void update() {
		super.update();

		World world = this.getWorldStorage().getWorld();

		if(!world.isRemote) {
			float fogStrength = this.dataManager.get(GROUND_FOG_STRENGTH);

			if(this.defeated && fogStrength > 0.0f) {
				this.dataManager.set(GROUND_FOG_STRENGTH, Math.max(0, fogStrength - 0.01f));
				this.setDirty(true);
			} else if(!this.defeated && fogStrength < 1.0f) {
				this.dataManager.set(GROUND_FOG_STRENGTH, Math.min(1, fogStrength + 0.01f));
				this.setDirty(true);
			}
		}

		//TODO Only spawn when player is nearby? (maybe even per floor, for performance reasons)

		if(!this.defeated && world instanceof WorldServer && world.provider instanceof WorldProviderBetweenlands && world.getGameRules().getBoolean("doMobSpawning") && world.getTotalWorldTime() % 4 == 0) {
			boolean spawnHostiles = ((WorldProviderBetweenlands)world.provider).getCanSpawnHostiles();
			boolean spawnAnimals = ((WorldProviderBetweenlands)world.provider).getCanSpawnAnimals();

			BlockPos pos = this.getStructurePos();

			this.mazeMobSpawner.clearAreas();
			this.mazeMobSpawner.addArea(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 29, pos.getY() - 8 * 5 - 3, pos.getZ() + 29));
			this.mazeMobSpawner.populate((WorldServer) world, spawnHostiles, spawnAnimals);

			this.walkwaysMobSpawner.clearAreas();
			this.walkwaysMobSpawner.addArea(new AxisAlignedBB(pos.getX() - 3, pos.getY() - 43, pos.getZ() - 3, pos.getX(), pos.getY() - 24, pos.getZ() + 29));
			this.walkwaysMobSpawner.addArea(new AxisAlignedBB(pos.getX(), pos.getY() - 43, pos.getZ() - 3, pos.getX() + 29 , pos.getY() - 24, pos.getZ()));
			this.walkwaysMobSpawner.populate((WorldServer) world, spawnHostiles, spawnAnimals);
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean addGroundFogVolumesToShader(WorldShader shader) {
		float globalStrength = this.dataManager.get(GROUND_FOG_STRENGTH);

		if(globalStrength > 0) {
			for(int floor = 0; floor < MAX_FLOORS; floor++) {
				float floorStrength = globalStrength / (float) MAX_FLOORS * (floor + 1);

				float fogBrightness = 0.25F;
				float inScattering = 0.035F - 0.015F * floorStrength;
				float extinction = 6.0F - 4.2F * floorStrength;

				float height = 4.0f + 8.0f * floorStrength;

				shader.addGroundFogVolume(new GroundFogVolume(new Vec3d(this.structurePos.getX(), this.structurePos.getY() - 5.2D - floor * 6, this.structurePos.getZ()), new Vec3d(29, height, 29), inScattering, extinction, fogBrightness, fogBrightness, fogBrightness));
			}

			return true;
		}

		return false;
	}

	public int getFloor(BlockPos pos) {
		return (this.structurePos.getY() - 1 - pos.getY()) / (MAX_FLOORS - 1);
	}

	public void removeLocations() {
		ILocalStorageHandler handler = this.getWorldStorage().getLocalStorageHandler();
		handler.removeLocalStorage(this);
		handler.getLocalStorages(LocationStorage.class, this.getEnclosingBounds(), l -> l.getType() == EnumLocationType.SLUDGE_WORM_DUNGEON).forEach(location -> handler.removeLocalStorage(location));
	}
}
