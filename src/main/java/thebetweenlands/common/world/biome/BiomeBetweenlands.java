package thebetweenlands.common.world.biome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntriesProvider;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.api.entity.spawning.IWeightProvider;
import thebetweenlands.api.event.InitializeBetweenlandsBiomeEvent;
import thebetweenlands.common.entity.EntityCCGroundSpawner;
import thebetweenlands.common.entity.EntityWormGroundSpawner;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.entity.mobs.EntityPyrad;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.biome.spawning.spawners.ConditionalSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.LocationSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.WallSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;
import thebetweenlands.common.world.storage.location.EnumLocationType;

public class BiomeBetweenlands extends Biome implements IWeightProvider, ICustomSpawnEntriesProvider {
	private final List<ICustomSpawnEntry> blSpawnEntries = new ArrayList<>();
	private int grassColor = -1, foliageColor = -1, secondaryGrassColor = -1, secondaryFoliageColor = -1;
	private short biomeWeight;
	private BiomeGenerator biomeGenerator;
	private int[] fogColorRGB = new int[]{(int) 255, (int) 255, (int) 255};
	
	public BiomeBetweenlands(ResourceLocation registryName, BiomeProperties properties) {
		super(properties);
		
		this.setRegistryName(registryName);
		
		this.spawnableCreatureList.clear();
		this.spawnableMonsterList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();
		this.biomeWeight = 100;
		this.topBlock = BlockRegistry.SWAMP_GRASS.getDefaultState();
		this.fillerBlock = BlockRegistry.SWAMP_DIRT.getDefaultState();
		this.biomeGenerator = new BiomeGenerator(this);

		this.setFogColor(10, 30, 22);
		
		List<ICustomSpawnEntry> spawnEntries = new ArrayList<>();
		this.addSpawnEntries(spawnEntries);
		
		MinecraftForge.EVENT_BUS.post(new InitializeBetweenlandsBiomeEvent(this, spawnEntries));
		
		this.blSpawnEntries.addAll(spawnEntries);
	}

	/**
	 * Adds the entity spawn entries
	 */
	protected void addSpawnEntries(List<ICustomSpawnEntry> entries) {
		BiPredicate<World, BlockPos> bloodSkyPredicate = ConditionalSpawnEntry.createEventPredicate(new ResourceLocation(ModInfo.ID, "blood_sky"));
		BiPredicate<World, BlockPos> heavyRainPredictate = ConditionalSpawnEntry.createEventPredicate(new ResourceLocation(ModInfo.ID, "heavy_rain"));

		entries.add(new ConditionalSpawnEntry(800, new SurfaceSpawnEntry(-1, EntityFirefly.class, EntityFirefly::new, (short) 280), bloodSkyPredicate).setSpawnCheckRadius(16.0D).setGroupSize(1, 4));
		entries.add(new ConditionalSpawnEntry(801, new SurfaceSpawnEntry(-1, EntitySwampHag.class, EntitySwampHag::new, (short) 250), bloodSkyPredicate) {
			@Override
			public EntityLiving createEntity(World world) {
				EntityLiving entity = super.createEntity(world);
				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32D);
				entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
				return entity;
			}
		}.setHostile(true));
		entries.add(new ConditionalSpawnEntry(802, new SurfaceSpawnEntry(-1, EntityPeatMummy.class, EntityPeatMummy::new, (short) 65), bloodSkyPredicate) {
			@Override
			public EntityLiving createEntity(World world) {
				EntityLiving entity = super.createEntity(world);
				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(EntityPeatMummy.BASE_SPEED + 0.075D);
				entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(EntityPeatMummy.BASE_DAMAGE + 2.0D);
				return entity;
			}
		}.setHostile(true).setSpawnCheckRadius(20.0D));

		entries.add(new ConditionalSpawnEntry(804, new WallSpawnEntry(-1, EntityCCGroundSpawner.class, EntityCCGroundSpawner::new, (short) 120), bloodSkyPredicate).setSpawnCheckRadius(32.0D).setGroupSize(1, 1).setHostile(true));
		entries.add(new ConditionalSpawnEntry(805, new WallSpawnEntry(-1, EntityWormGroundSpawner.class, EntityWormGroundSpawner::new, (short) 120), heavyRainPredictate).setSpawnCheckRadius(32.0D).setGroupSize(1, 1).setHostile(true));

		entries.add(new LocationSpawnEntry(803, EntityPyrad.class, EntityPyrad::new, (short) 120, EnumLocationType.GIANT_TREE) {
			@Override
			public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState blockState, IBlockState surfaceBlockState) {
				return !blockState.isNormalCube() && SurfaceType.MIXED_GROUND.matches(surfaceBlockState);
			};
		}.setHostile(true).setSpawnCheckRadius(50.0D).setSpawningInterval(5000));
	}

	/**
	 * Sets the biome generator
	 * @param generator
	 * @return
	 */
	protected final BiomeBetweenlands setBiomeGenerator(BiomeGenerator generator) {
		if(generator.getBiome() != this)
			throw new RuntimeException("Generator was assigned to a different biome!");
		this.biomeGenerator = generator;
		return this;
	}

	/**
	 * Returns the biome generator.
	 * If no generator was specified the default biome generator is returned
	 * @return
	 */
	public final BiomeGenerator getBiomeGenerator() {
		return this.biomeGenerator;
	}

	@Override
	public final List<ICustomSpawnEntry> getCustomSpawnEntries() {
		return Collections.unmodifiableList(this.blSpawnEntries);
	}

	/**
	 * Sets Biome specific weighted probability.
	 * The default weight is 100.
	 * @param weight
	 */
	protected final BiomeBetweenlands setWeight(int weight) {
		this.biomeWeight = (short) weight;
		return this;
	}

	/**
	 * Sets the grass and foliage colors
	 * @param grassColor
	 * @param foliageColor
	 * @return
	 */
	public final BiomeBetweenlands setFoliageColors(int grassColor, int foliageColor) {
		this.grassColor = grassColor;
		this.foliageColor = foliageColor;
		return this;
	}
	
	/**
	 * Sets the secondary grass and foliage color. Will be applied to patches
	 * using noise
	 * @param grassColor
	 * @param foliageColor
	 * @return
	 */
	public final BiomeBetweenlands setSecondaryFoliageColors(int grassColor, int foliageColor) {
		this.secondaryGrassColor = grassColor;
		this.secondaryFoliageColor = foliageColor;
		return this;
	}

	/**
	 * Sets the biome fog color
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public final BiomeBetweenlands setFogColor(int red, int green, int blue) {
		this.fogColorRGB[0] = red;
		this.fogColorRGB[1] = green;
		this.fogColorRGB[2] = blue;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getGrassColorAtPos(BlockPos pos) {
		if(this.secondaryGrassColor < 0) {
			if(this.grassColor < 0) {
				return super.getGrassColorAtPos(pos);
			}
			return this.grassColor;
		}
		double noise = GRASS_COLOR_NOISE.getValue((double)pos.getX() * 0.0225D, (double)pos.getZ() * 0.0225D);
		if(noise < -0.1) {
			if(this.grassColor < 0) {
				return super.getGrassColorAtPos(pos);
			}
			return this.grassColor;
		} else {
			if(this.secondaryGrassColor < 0) {
				return super.getGrassColorAtPos(pos);
			}
			return this.secondaryGrassColor;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getFoliageColorAtPos(BlockPos pos) {
		if(this.secondaryFoliageColor < 0) {
			if(this.foliageColor < 0) {
				return super.getGrassColorAtPos(pos);
			}
			return this.foliageColor;
		}
		double noise = GRASS_COLOR_NOISE.getValue((double)pos.getX() * 0.0225D, (double)pos.getZ() * 0.0225D);
		if(noise < -0.1) {
			if(this.foliageColor < 0) {
				return super.getGrassColorAtPos(pos);
			}
			return this.foliageColor;
		} else {
			if(this.secondaryFoliageColor < 0) {
				return super.getGrassColorAtPos(pos);
			}
			return this.secondaryFoliageColor;
		}
	}

	/**
	 * Returns Biome specific weighted probability.
	 */
	@Override
	public final short getWeight() {
		return this.biomeWeight;
	}

	/**
	 * Returns the distance where the fog starts to build up.
	 * @param farPlaneDistance Maximum render distance
	 * @return float
	 */
	@SideOnly(Side.CLIENT)
	public float getFogStart(float farPlaneDistance, int mode) {
		return mode == -1 ? 0.0F : farPlaneDistance * 0.5F;
	}

	/**
	 * Returns the distance where the fog is fully opaque.
	 * @param farPlaneDistance Maximum render distance
	 * @return float
	 */
	@SideOnly(Side.CLIENT)
	public float getFogEnd(float farPlaneDistance, int mode) {
		return farPlaneDistance;
	}

	/**
	 * Returns the fog RGB color.
	 * @return int[3]
	 */
	@SideOnly(Side.CLIENT)
	public int[] getFogRGB() {
		return this.fogColorRGB;
	}

	/**
	 * Called to update the fog range and color
	 */
	public void updateFog() {

	}

	public void addTypes(){

	}
}
