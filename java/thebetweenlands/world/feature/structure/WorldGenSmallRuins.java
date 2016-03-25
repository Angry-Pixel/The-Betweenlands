package thebetweenlands.world.feature.structure;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.loot.LootTables;
import thebetweenlands.world.loot.LootUtil;

import java.util.ArrayList;
import java.util.Random;

public class WorldGenSmallRuins extends WorldGenerator {

    private static final boolean markReplaceableCheck = false;
    private static final int[] stairSequence = new int[]{0, 3, 1, 2};
    private static final int[] upsideDownStairSequence = new int[]{4, 7, 5, 6};
    private static final int[] logSequence = new int[]{4, 8};
    private static final Block angryBetweenstone = BLBlockRegistry.angryBetweenstone;
    private static final Block betweenstoneTiles = BLBlockRegistry.betweenstoneTiles;
    private static final Block betweenstoneBricks = BLBlockRegistry.betweenstoneBricks;
    private static final Block betweenstoneBrickStairs = BLBlockRegistry.betweenstoneBrickStairs;
    private static final Block betweenstoneBrickSlab = BLBlockRegistry.betweenstoneBrickSlab;
    private static final Block chiseledBetweenstone = BLBlockRegistry.chiseledBetweenstone;
    private static final Block betweenstonePillar = BLBlockRegistry.betweenstonePillar;
    private static final Block smoothBetweenstoneWall = BLBlockRegistry.smoothBetweenstoneWall;
    private static final Block weedwoodLog = BLBlockRegistry.weedwoodLog;
    private static final Block weedwoodPlankStairs = BLBlockRegistry.weedwoodPlankStairs;
    private static final Block weedwoodPlankSlab = BLBlockRegistry.weedwoodPlankSlab;
    private ArrayList<RuinLocation> ruinLocations = new ArrayList<>();
    private int width = -1;
    private int depth = -1;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        ruinLocations.clear();
        int attempts = 40;
        while (attempts >= 0) {
            x = x + random.nextInt(20) - 10;
            z = z + random.nextInt(20) - 10;
            y = y + random.nextInt(8) - 3;
            int randomInt = random.nextInt(7);
            switch (randomInt) {
                case 0:
                    if (structure1(world, random, x, y, z, false))
                        ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
                    break;
                case 1:
                    if (structure2(world, random, x, y, z, false))
                        ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
                    break;
                case 2:
                    if (structure3(world, random, x, y, z, false))
                        ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
                    break;
                case 3:
                    if (structure4(world, random, x, y, z, false))
                        ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
                    break;
                case 4:
                    if (structure5(world, random, x, y, z, false))
                        ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
                    break;
                case 5:
                    if (structure6(world, random, x, y, z, false))
                        ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
                    break;
                case 6:
                    if (structure7(world, random, x, y, z, false))
                        ruinLocations.add(new RuinLocation(world, random, x, y, z, randomInt));
                    break;
            }
            attempts--;
        }

        if (ruinLocations.size() >= 3) {
            for (RuinLocation location : ruinLocations) {
                switch (location.structureID) {
                    case 0:
                        structure1(location.world, location.random, location.x, location.y, location.z, true);
                        break;
                    case 1:
                        structure2(location.world, location.random, location.x, location.y, location.z, true);
                        break;
                    case 2:
                        structure3(location.world, location.random, location.x, location.y, location.z, true);
                        break;
                    case 3:
                        structure4(location.world, location.random, location.x, location.y, location.z, true);
                        break;
                    case 4:
                        structure5(location.world, location.random, location.x, location.y, location.z, true);
                        break;
                    case 5:
                        structure6(location.world, location.random, location.x, location.y, location.z, true);
                        break;
                    case 6:
                        structure7(location.world, location.random, location.x, location.y, location.z, true);
                        break;
                }
            }
            return true;
        } else
            return false;
    }

    private boolean structure1(World world, Random random, int x, int y, int z, boolean doGen) {
        int height = 9 + random.nextInt(2);
        width = 8;
        depth = 1;
        int direction = random.nextInt(4);
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;

        if (doGen) {
            rotatedCubeVolume(world, x, y, z, 2, 0, 0, betweenstoneTiles, 0, 1, height - 5, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 0, 0, betweenstoneTiles, 0, 1, height - 5, 1, direction);

            rotatedCubeVolume(world, x, y, z, 2, height - 5, 0, betweenstoneBricks, 0, 1, 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, height - 5, 0, betweenstoneBricks, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 1, height - 5, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 0, height - 4, 0, betweenstoneBrickSlab, 0, 2, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 2, height - 3, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 3, height - 3, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 4, height - 3, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 5, height - 3, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 3, height - 2, 0, betweenstoneBrickSlab, 0, 3, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 6, height - 1, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, height - 1, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);

            if (random.nextInt(5) == 0)
                rotatedLoot(world, random, x, y, z, 4, height - 2, 0, direction);
        }
        return true;
    }

    private boolean structure2(World world, Random random, int x, int y, int z, boolean doGen) {
        int height = 13 + random.nextInt(2);
        width = 7;
        depth = 1;
        int direction = random.nextInt(4);
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;
        if (doGen) {
            rotatedCubeVolume(world, x, y, z, 2, 0, 0, betweenstoneTiles, 0, 1, height - 9, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 0, 0, betweenstoneTiles, 0, 1, height - 9, 1, direction);

            rotatedCubeVolume(world, x, y, z, 2, height - 9, 0, betweenstoneBricks, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, height - 9, 0, betweenstoneBricks, 0, 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 2, height - 8, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, height - 8, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 2, height - 7, 0, betweenstoneBricks, 0, 1, 5, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, height - 7, 0, betweenstoneBricks, 0, 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 3, height - 6, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 4, height - 6, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 5, height - 6, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, height - 6, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 3, height - 5, 0, betweenstoneBrickSlab, 0, 3, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 2, height - 2, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 1, height - 2, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, betweenstoneBrickSlab, 0, 2, 1, 1, direction);

            if (random.nextInt(5) == 0) {
                rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, betweenstoneBricks, 0, 1, 1, 1, direction);
                rotatedLoot(world, random, x, y, z, 0, height, 0, direction);
            }
        }
        return true;
    }

    private boolean structure3(World world, Random random, int x, int y, int z, boolean doGen) {
        width = 7;
        depth = 5;
        int direction = random.nextInt(4);
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 7, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 2, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 2, -1, 4, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, 6, -1, 4, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;
        if (doGen) {
            rotatedCubeVolume(world, x, y, z, 2, 0, 0, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 0, 0, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 2, 0, 4, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 0, 4, betweenstoneTiles, 0, 1, 4, 1, direction);

            rotatedCubeVolume(world, x, y, z, 1, 4, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 1, 4, 4, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 2, 4, 0, betweenstoneBricks, 0, 1, 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 4, 0, betweenstoneBricks, 0, 1, 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 2, 4, 4, betweenstoneBricks, 0, 1, 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 4, 4, betweenstoneBricks, 0, 1, 2, 1, direction);

            rotatedCubeVolume(world, x, y, z, 0, 5, 0, betweenstoneBrickSlab, 0, 2, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 3, 5, 0, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 3, 5, 4, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 5, 5, 0, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 5, 5, 4, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 2, 5, 1, betweenstoneBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 5, 1, betweenstoneBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 2, 5, 3, betweenstoneBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 5, 3, betweenstoneBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 3, 6, 0, betweenstoneBrickSlab, 0, 3, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 3, 6, 4, betweenstoneBrickSlab, 0, 3, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 2, 6, 1, betweenstoneBrickSlab, 0, 1, 1, 3, direction);
            rotatedCubeVolume(world, x, y, z, 6, 6, 1, betweenstoneBrickSlab, 0, 1, 1, 3, direction);
            if (random.nextInt(5) == 0)
                rotatedLoot(world, random, x, y, z, 2, 6, 0, direction);
            if (random.nextInt(5) == 0)
                rotatedLoot(world, random, x, y, z, 6, 6, 0, direction);
            if (random.nextInt(5) == 0)
                rotatedLoot(world, random, x, y, z, 2, 6, 4, direction);
            if (random.nextInt(5) == 0)
                rotatedLoot(world, random, x, y, z, 6, 6, 4, direction);
        }
        return true;
    }

    //TODO switch this to the new system at some point....
    private boolean structure4(World world, Random random, int x, int y, int z, boolean doGen) {
        int height = 9 + random.nextInt(2);
        int width = 6;
        for (int zz = z; zz < z + width; zz++)
            for (int yy = y; yy < y + height; yy++)
                for (int xx = x; xx > x - width; xx--)
                    if (!(world.getBlock(xx, yy, zz) == Blocks.air || (world.getBlock(xx, yy, zz) == BLBlockRegistry.swampWater && yy < y + height - 2)))
                        return false;

        if (!SurfaceType.MIXED.matchBlock(world.getBlock(x - width, y - 1, z + 1)) || !SurfaceType.MIXED.matchBlock(world.getBlock(x - width, y - 1, z + width - 1)) || !SurfaceType.MIXED.matchBlock(world.getBlock(x - 1, y - 1, z + width)) || !SurfaceType.MIXED.matchBlock(world.getBlock(x - 1, y - 1, z + width)))
            return false;
        if (doGen) {
            for (int yy = y; yy < y + height; yy++) {
                if (yy <= y + height - 5) {
                    if (yy == y)
                        world.setBlock(x - width, yy, z + 1, BLBlockRegistry.betweenstoneTiles);
                    else
                        world.setBlock(x - width, yy, z + 1, BLBlockRegistry.betweenstoneBrickWall);
                    world.setBlock(x - width + 1, yy, z + 1, BLBlockRegistry.betweenstoneTiles);
                } else if (yy == y + height - 4) {
                    world.setBlock(x - width, yy, z + 1, BLBlockRegistry.betweenstoneBrickWall);
                    world.setBlock(x - width + 1, yy, z + 1, BLBlockRegistry.betweenstoneBricks);
                } else if (yy == y + height - 3) {
                    world.setBlock(x - width + 1, yy, z + 1, BLBlockRegistry.betweenstoneBrickStairs, 2, 3);
                    world.setBlock(x - width + 1, yy, z + 2, BLBlockRegistry.betweenstoneBrickStairs, 7, 3);
                    world.setBlock(x - width + 1, yy + 1, z + 2, BLBlockRegistry.betweenstoneBrickSlab);
                    int zz;
                    for (zz = z + 3; zz <= z + width - 3; zz++) {
                        world.setBlock(x - width + 1, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                        world.setBlock(x - width + 1, yy, zz, BLBlockRegistry.betweenstoneBrickSlab, 9, 3);
                    }
                    world.setBlock(x - width + 1, yy, zz, BLBlockRegistry.betweenstoneBrickStairs, 6, 3);
                    world.setBlock(x - width + 1, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }

            for (int yy = y; yy < y + height; yy++) {
                if (yy <= y + height - 5) {
                    if (yy == y) {
                        world.setBlock(x - width, yy, z + width - 1, BLBlockRegistry.betweenstoneTiles);
                        world.setBlock(x - width + 1, yy, z + width, BLBlockRegistry.betweenstoneTiles);
                    } else {
                        world.setBlock(x - width, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickWall);
                        world.setBlock(x - width + 1, yy, z + width, BLBlockRegistry.betweenstoneBrickWall);
                    }
                    world.setBlock(x - width + 1, yy, z + width - 1, BLBlockRegistry.betweenstoneTiles);
                } else if (yy <= y + height - 2) {
                    if (yy == y + height - 4) {
                        world.setBlock(x - width, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickWall);
                        world.setBlock(x - width + 1, yy, z + width, BLBlockRegistry.betweenstoneBrickWall);
                    }
                    world.setBlock(x - width + 1, yy, z + width - 1, BLBlockRegistry.betweenstoneBricks);
                } else if (yy <= y + height - 1) {
                    world.setBlock(x - width + 1, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs);
                    world.setBlock(x - width + 2, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                    world.setBlock(x - width + 2, yy + 1, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab);
                    int xx;
                    for (xx = x - width + 3; xx <= x - 3; xx++) {
                        world.setBlock(xx, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab, 9, 3);
                        if (random.nextInt(8) == 0) {
                            generateLoot(world, random, xx, yy + 1, z + width - 1);
                        } else
                            world.setBlock(xx, yy + 1, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab);
                    }
                    world.setBlock(xx, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 4, 3);
                    world.setBlock(xx, yy + 1, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }

            for (int yy = y; yy < y + height; yy++) {
                if (yy <= y + height - 5) {
                    if (yy == y) {
                        world.setBlock(x - 1, yy, z + width, BLBlockRegistry.betweenstoneTiles);
                    } else {
                        world.setBlock(x - 1, yy, z + width, BLBlockRegistry.betweenstoneBrickWall);
                    }
                    world.setBlock(x - 1, yy, z + width - 1, BLBlockRegistry.betweenstoneTiles);
                } else if (yy <= y + height - 2) {
                    if (yy == y + height - 4) {
                        world.setBlock(x - 1, yy, z + width, BLBlockRegistry.betweenstoneBrickWall);
                    }
                    if (yy == y + height - 3) {
                        world.setBlock(x, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                    }
                    world.setBlock(x - 1, yy, z + width - 1, BLBlockRegistry.betweenstoneBricks);
                } else if (yy == y + height - 1) {
                    world.setBlock(x - 1, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 1, 3);
                }
            }
        }
        return true;
    }

    private boolean structure5(World world, Random random, int x, int y, int z, boolean doGen) {
        int height = 5 + random.nextInt(2);
        width = 1;
        depth = 1;
        int direction = 0;
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, height, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;
        if (doGen) {
            rotatedCubeVolume(world, x, y, z, 0, 0, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 0, 1, 0, betweenstonePillar, 0, 1, height - 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 0, height - 1, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);
            if (random.nextInt(5) == 0) {
                rotatedLoot(world, random, x, y, z, 0, height, 0, direction);
            }
        }
        return true;
    }

    private boolean structure6(World world, Random random, int x, int y, int z, boolean doGen) {
        width = 5 + random.nextInt(2);
        depth = 1;
        int direction = random.nextInt(4);
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 1, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED) || !rotatedCubeMatches(world, x, y, z, width - 1, -1, 0, 1, 1, 1, direction, SurfaceType.MIXED))
            return false;
        if (doGen) {
            rotatedCubeVolume(world, x, y, z, 0, 0, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 1, 0, 0, betweenstonePillar, direction == 0 || direction == 2 ? 7 : 8, width - 2, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, width - 1, 0, 0, chiseledBetweenstone, 0, 1, 1, 1, direction);

            if (random.nextInt(5) == 0) {
                rotatedLoot(world, random, x, y, z, 0, 1, 0, direction);
            }
            if (random.nextInt(5) == 0) {
                rotatedLoot(world, random, x, y, z, width - 1, 1, 0, direction);
            }
        }
        return true;
    }

    private boolean structure7(World world, Random random, int x, int y, int z, boolean doGen) {
        width = 12;
        depth = 12;
        int direction = random.nextInt(4);
        if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 13, depth, direction))
            return false;
        if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, width, 1, depth, direction, SurfaceType.MIXED))
            return false;
        if (doGen) {
            rotatedCubeVolume(world, x, y, z, 0, 0, 0, betweenstoneBricks, 0, 1, 1, 4, direction);
            rotatedCubeVolume(world, x, y, z, 0, 1, 0, betweenstoneBricks, 0, 1, 1, 3, direction);
            rotatedCubeVolume(world, x, y, z, 0, 1, 3, betweenstoneBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 0, 2, 0, betweenstoneBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 0, 2, 1, betweenstoneBricks, 0, 1, 1, 2, direction);
            rotatedCubeVolume(world, x, y, z, 0, 2, 3, betweenstoneBrickSlab, 0, 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 6, 0, 1, betweenstoneTiles, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 1, 1, smoothBetweenstoneWall, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 0, 1, betweenstoneTiles, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 1, 1, smoothBetweenstoneWall, 0, 1, 4, 1, direction);

            rotatedCubeVolume(world, x, y, z, 6, 0, 2, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 4, 2, betweenstoneBricks, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 8, 2, betweenstoneBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 0, 2, betweenstoneBricks, 0, 1, 7, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 7, 2, betweenstoneBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 8, 0, 2, betweenstoneBricks, 0, 1, 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 8, 2, 2, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 8, 3, 2, betweenstoneBricks, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 9, 0, 2, betweenstoneBricks, 0, 1, 5, 1, direction);
            rotatedCubeVolume(world, x, y, z, 9, 5, 2, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 0, 2, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 4, 2, betweenstoneBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 11, 0, 2, betweenstoneTiles, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 11, 1, 2, smoothBetweenstoneWall, 0, 1, 3, 1, direction);

            rotatedCubeVolume(world, x, y, z, 6, 5, 3, weedwoodPlankStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 8, 3, betweenstoneBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 9, 3, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 5, 3, weedwoodPlankSlab, 8, 3, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 0, 3, betweenstoneBricks, 0, 1, 6, 3, direction);
            rotatedCubeVolume(world, x, y, z, 10, 3, 3, betweenstoneBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 6, 3, betweenstoneBrickSlab, 0, 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 6, 5, 4, weedwoodPlankSlab, 8, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 8, 4, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 9, 4, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 3, 4, BLBlockRegistry.rope, 0, 1, 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 5, 4, weedwoodLog, getMetaFromDirection(0, direction, logSequence), 5, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 9, 4, 4, BLBlockRegistry.weedwoodPlankFence, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 2, 4, chiseledBetweenstone, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 6, 4, betweenstoneBricks, 0, 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 6, 5, 5, weedwoodPlankStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 8, 5, betweenstoneBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 9, 5, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 5, 5, weedwoodPlankSlab, 8, 3, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 6, 5, betweenstoneBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 2, 0, 6, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 2, 4, 6, betweenstoneBricks, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 2, 8, 6, betweenstoneBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 3, 8, 6, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 3, 9, 6, betweenstoneBrickSlab, 0, 3, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 4, 8, 6, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 5, 8, 6, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 0, 6, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 4, 6, betweenstoneBricks, 0, 1, 6, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 10, 6, betweenstoneBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 5, 6, weedwoodPlankSlab, 8, 3, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 9, 0, 6, betweenstoneBricks, 0, 1, 1, 1, direction);
            rotatedLoot(world, random, x, y, z, 9, 1, 6, direction);
            rotatedCubeVolume(world, x, y, z, 10, 0, 6, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 4, 6, betweenstoneBricks, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 5, 6, betweenstoneBrickStairs, getMetaFromDirection(1, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 11, 0, 6, betweenstoneTiles, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 11, 1, 6, smoothBetweenstoneWall, 0, 1, 4, 1, direction);

            rotatedCubeVolume(world, x, y, z, 6, 8, 7, betweenstoneBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 9, 7, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 0, 7, betweenstoneBricks, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 8, 5, 7, weedwoodPlankSlab, 8, 2, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 0, 7, betweenstoneBricks, 0, 1, 6, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 6, 7, betweenstoneBrickSlab, 0, 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 5, 0, 8, betweenstoneBricks, 0, 4, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 8, 8, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 9, 8, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 9, 4, 8, BLBlockRegistry.weedwoodPlankFence, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 9, 5, 8, weedwoodLog, getMetaFromDirection(0, direction, logSequence), 3, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 0, 8, betweenstoneBricks, 0, 1, 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 2, 8, chiseledBetweenstone, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 3, 8, betweenstoneBricks, 0, 1, 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 6, 8, betweenstoneBrickSlab, 0, 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 3, 0, 9, betweenstoneBricks, 0, 4, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 8, 9, betweenstoneBrickStairs, getMetaFromDirection(1, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 9, 9, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 0, 9, angryBetweenstone, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 8, 0, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
            rotatedLoot(world, random, x, y, z, 9, 1, 9, direction);
            rotatedCubeVolume(world, x, y, z, 10, 0, 9, betweenstoneBricks, 0, 1, 5, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 5, 9, betweenstoneBrickStairs, getMetaFromDirection(3, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 6, 9, betweenstoneBrickSlab, 0, 1, 1, 1, direction);

            rotatedCubeVolume(world, x, y, z, 2, 0, 10, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 2, 4, 10, betweenstoneBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 3, 0, 10, betweenstoneBricks, 0, 7, 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 3, 2, 10, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 4, 2, 10, betweenstoneBricks, 0, 2, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 5, 3, 10, betweenstoneBrickStairs, getMetaFromDirection(0, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 5, 8, 10, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 5, 9, 10, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 5, 11, 10, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 4, 12, 10, betweenstoneBrickSlab, 0, 2, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 0, 10, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 4, 10, betweenstoneBrickStairs, getMetaFromDirection(3, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 5, 10, betweenstoneBricks, 0, 1, 6, 1, direction);
            rotatedCubeVolume(world, x, y, z, 6, 11, 10, betweenstoneBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 2, 10, betweenstoneBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 6, 10, betweenstoneBrickStairs, getMetaFromDirection(2, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 7, 7, 10, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 8, 2, 10, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 8, 6, 10, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 8, 7, 10, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 9, 2, 10, betweenstoneBricks, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 9, 6, 10, betweenstoneBrickStairs, getMetaFromDirection(0, direction, upsideDownStairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 9, 7, 10, betweenstoneBrickSlab, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 0, 10, betweenstoneTiles, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 4, 10, betweenstoneBricks, 0, 1, 2, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 6, 10, betweenstoneBrickStairs, getMetaFromDirection(2, direction, stairSequence), 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 11, 0, 10, betweenstoneTiles, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 11, 1, 10, smoothBetweenstoneWall, 0, 1, 4, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 0, 11, betweenstoneTiles, 0, 1, 1, 1, direction);
            rotatedCubeVolume(world, x, y, z, 10, 1, 11, smoothBetweenstoneWall, 0, 1, 4, 1, direction);
        }
        return true;
    }

    private void rotatedCubeVolume(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, Block blockType, int blockMeta, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
                        for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++)
                            world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
            case 1:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++)
                            world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
            case 2:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--)
                            world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
            case 3:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--)
                            world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
        }
    }

    private void rotatedLoot(World world, Random rand, int x, int y, int z, int offsetA, int offsetB, int offsetC, int direction) {
        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0:
                generateLoot(world, rand, x + offsetA, y + offsetB, z + offsetC);
                break;
            case 1:
                generateLoot(world, rand, x + offsetC, y + offsetB, z + depth - offsetA - 1);
                break;
            case 2:
                generateLoot(world, rand, x + width - offsetA - 1, y + offsetB, z + depth - offsetC - 1);
                break;
            case 3:
                generateLoot(world, rand, x + width - offsetC - 1, y + offsetB, z + offsetA);
                break;
        }
    }

    private boolean rotatedCubeCantReplace(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
        x -= width / 2;
        z -= depth / 2;
        boolean replaceable = true;
        switch (direction) {
            case 0:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
                        for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                            if (markReplaceableCheck)
                                world.setBlock(xx, yy, zz, Blocks.wool);
                        }
                break;
            case 1:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                            if (markReplaceableCheck)
                                world.setBlock(xx, yy, zz, Blocks.wool);
                        }
                break;
            case 2:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                            if (markReplaceableCheck)
                                world.setBlock(xx, yy, zz, Blocks.wool);
                        }
                break;
            case 3:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
                            if (!world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz))
                                replaceable = false;
                            if (markReplaceableCheck)
                                world.setBlock(xx, yy, zz, Blocks.wool);
                        }
                break;
        }
        return !replaceable;
    }

    private boolean rotatedCubeMatches(World world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction, SurfaceType type) {
        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
                        for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
                            if (!type.matchBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
            case 1:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
                            if (!type.matchBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
            case 2:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
                            if (!type.matchBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
            case 3:
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
                            if (!type.matchBlock(world.getBlock(xx, yy, zz)))
                                return false;
                        }
                break;
        }
        return true;
    }

    private void generateLoot(World world, Random random, int x, int y, int z) {
        int randDirection = random.nextInt(4) + 2;
        world.setBlock(x, y, z, getRandomBlock(random), randDirection, 3);
        TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x, y, z);
        if (lootPot != null)
            LootUtil.generateLoot(lootPot, random, LootTables.COMMON_POT_LOOT, 1, 2);
    }

    private Block getRandomBlock(Random rand) {
        switch (rand.nextInt(3)) {
            case 0:
                return BLBlockRegistry.lootPot1;
            case 1:
                return BLBlockRegistry.lootPot2;
            case 2:
                return BLBlockRegistry.lootPot3;
            default:
                return BLBlockRegistry.lootPot1;
        }
    }

    private int getMetaFromDirection(int start, int direction, int[] sequence) {
        return sequence[(direction + start) % sequence.length];
    }

    private static class RuinLocation {
        World world;
        Random random;
        int x;
        int y;
        int z;
        int structureID;

        RuinLocation(World world, Random random, int x, int y, int z, int structureID) {
            this.world = world;
            this.random = random;
            this.x = x;
            this.y = y;
            this.z = z;
            this.structureID = structureID;
        }

    }

}
