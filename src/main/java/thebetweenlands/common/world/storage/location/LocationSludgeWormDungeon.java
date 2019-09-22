package thebetweenlands.common.world.storage.location;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

	private final BoxMobSpawner dungeonMobSpawner;

	public LocationSludgeWormDungeon(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		super(worldStorage, id, region, "sludge_worm_dungeon", EnumLocationType.DUNGEON);

		this.dataManager.register(GROUND_FOG_STRENGTH, 1.0F);

		this.setAmbience(new LocationAmbience(EnumLocationAmbience.SLUDGE_WORM_DUNGEON).setCaveFog(false));

		this.dungeonMobSpawner = new BoxMobSpawner();
		this.dungeonMobSpawner.setMaxAreaEntities(80);
		this.dungeonMobSpawner.setEntityCountFilter(entity -> entity instanceof EntityTriggeredFallingBlock == false); //Ignore falling blocks

		//floor 1
		this.dungeonMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(0, new WallSpawnEntry(0, EntityMovingSpawnerHole.class, EntityMovingSpawnerHole::new, (short) 90), ConditionalSpawnEntry.createSludgeDungeonPredicate(0))
				.setGroupSize(1, 1).setSpawnCheckRadius(8.0D).setSpawningInterval(5 * 20).setHostile(true));

		//floor 2
		this.dungeonMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(1, new BLSpawnEntry(1, EntityShambler.class, EntityShambler::new, (short) 50), ConditionalSpawnEntry.createSludgeDungeonPredicate(1))
				.setGroupSize(1, 3).setSpawnCheckRadius(14.0D).setSpawningInterval(10 * 20).setHostile(true));

		//floor 3
		this.dungeonMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(2, new WallSpawnEntry(2, EntityWallLamprey.class, EntityWallLamprey::new, (short) 100), ConditionalSpawnEntry.createSludgeDungeonPredicate(2))
				.setGroupSize(1, 1).setSpawnCheckRadius(8.0D).setSpawningInterval(2 * 20).setHostile(true));
		this.dungeonMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(3, new BLSpawnEntry(3, EntityTinyWormEggSac.class, EntityTinyWormEggSac::new, (short) 100), ConditionalSpawnEntry.createSludgeDungeonPredicate(2))
				.setGroupSize(1, 1).setSpawnCheckRadius(8.0D).setSpawningInterval(8 * 20).setHostile(true));

		//floor 4
		this.dungeonMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(4, new BLSpawnEntry(4, EntityTriggeredSludgeWallJet.class, EntityTriggeredSludgeWallJet::new, (short) 100), ConditionalSpawnEntry.createSludgeDungeonPredicate(3))
				.setGroupSize(1, 1).setSpawnCheckRadius(10.0D).setSpawningInterval(2 * 20).setHostile(true));

		//floor 5
		this.dungeonMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(5, new WallSpawnEntry(5, EntityWallLivingRoot.class ,EntityWallLivingRoot::new, (short) 100), ConditionalSpawnEntry.createSludgeDungeonPredicate(4))
				.setGroupSize(1, 1).setSpawnCheckRadius(8.0D).setSpawningInterval(2 * 20).setHostile(true));
		this.dungeonMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(6, new BLSpawnEntry(6, EntitySplodeshroom.class, EntitySplodeshroom::new, (short) 100), ConditionalSpawnEntry.createSludgeDungeonPredicate(4))
				.setGroupSize(1, 1).setSpawnCheckRadius(8.0D).setSpawningInterval(2 * 20).setHostile(true));

		//floor 6
		this.dungeonMobSpawner.addSpawnEntry(new ConditionalSpawnEntry(7, new BLSpawnEntry(7, EntityCryptCrawler.class, EntityCryptCrawler::new, (short) 50), ConditionalSpawnEntry.createSludgeDungeonPredicate(5))
				.setGroupSize(1, 3).setSpawnCheckRadius(12.0D).setSpawningInterval(12 * 20).setHostile(true));
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
		//TODO Check if pos is in maze bounding box
		return this.dataManager.get(GROUND_FOG_STRENGTH) > 0.01F;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setFloat("groundFogStrength", this.dataManager.get(GROUND_FOG_STRENGTH));
		nbt.setLong("structurePos", this.structurePos.toLong());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.dataManager.set(GROUND_FOG_STRENGTH, nbt.getFloat("groundFogStrength"));
		this.structurePos = BlockPos.fromLong(nbt.getLong("structurePos"));
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

		if(world instanceof WorldServer && world.provider instanceof WorldProviderBetweenlands && world.getGameRules().getBoolean("doMobSpawning") && world.getTotalWorldTime() % 4 == 0) {
			boolean spawnHostiles = ((WorldProviderBetweenlands)world.provider).getCanSpawnHostiles();
			boolean spawnAnimals = ((WorldProviderBetweenlands)world.provider).getCanSpawnAnimals();

			this.dungeonMobSpawner.clearAreas();
			//TODO May need different AABBs for spawning purposes
			for(AxisAlignedBB aabb : this.getBounds()) {
				this.dungeonMobSpawner.addArea(aabb);
			}

			this.dungeonMobSpawner.populate((WorldServer) world, spawnHostiles, spawnAnimals);
		}

		//TODO Clear fog strength when dungeon is conquered
	}

	@SideOnly(Side.CLIENT)
	public boolean addGroundFogVolumesToShader(WorldShader shader) {
		float globalStrength = this.dataManager.get(GROUND_FOG_STRENGTH);

		if(globalStrength > 0) {
			for(int floor = 0; floor < 7; floor++) {
				float floorStrength = globalStrength / 7.0f * (floor + 1);

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
		return (this.structurePos.getY() - 1 - pos.getY()) / 6;
	}
}
