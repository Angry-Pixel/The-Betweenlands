package thebetweenlands.util;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

//Commented the world part as it's not needed at this time
public class FakeClientWorld/* extends World*/ {
    public static final WorldSettings worldSettings = new WorldSettings(0, GameType.SURVIVAL, true, false, WorldType.DEFAULT);
    public static final WorldInfo worldInfo = new WorldInfo(worldSettings, "the_betweenalands_fake");
    public static final FakeSaveHandler saveHandler = new FakeSaveHandler();
    /*public static final WorldProvider worldProvider = new WorldProvider() {
        @Override
        public DimensionType getDimensionType() {
            return DimensionType.OVERWORLD;
        }

        @Override
        public long getWorldTime() {
            return worldInfo.getWorldTime();
        }
    };

    public FakeClientWorld() {
        super(saveHandler, worldInfo, worldProvider, new Profiler(), true);
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return new IChunkProvider() {
            @Nullable
            @Override
            public Chunk getLoadedChunk(int x, int z) {
                return null;
            }

            @Override
            public Chunk provideChunk(int x, int z) {
                return null;
            }

            @Override
            public boolean tick() {
                return false;
            }

            @Override
            public String makeString() {
                return null;
            }

            @Override
            public boolean isChunkGeneratedAt(int p_191062_1_, int p_191062_2_) {
                return false;
            }
        };
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return false;
    }*/

    private static class FakeSaveHandler implements ISaveHandler {

        @Override
        public WorldInfo loadWorldInfo() {
            return worldInfo;
        }

        @Override
        public void checkSessionLock() throws MinecraftException {

        }

        @Override
        public IChunkLoader getChunkLoader(WorldProvider provider) {
            return new IChunkLoader() {
                @Nullable
                @Override
                public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
                    return null;
                }

                @Override
                public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {

                }

                @Override
                public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {

                }

                @Override
                public void chunkTick() {

                }

                @Override
                public void flush() {

                }

                @Override
                public boolean isChunkGeneratedAt(int p_191063_1_, int p_191063_2_) {
                    return false;
                }
            };
        }

        @Override
        public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {

        }

        @Override
        public void saveWorldInfo(WorldInfo worldInformation) {

        }

        @Override
        public IPlayerFileData getPlayerNBTManager() {
            return new IPlayerFileData() {
                @Override
                public void writePlayerData(EntityPlayer player) {

                }

                @Override
                public NBTTagCompound readPlayerData(EntityPlayer player) {
                    return new NBTTagCompound();
                }

                @Override
                public String[] getAvailablePlayerDat() {
                    return new String[0];
                }
            };
        }

        @Override
        public void flush() {

        }

        @Override
        public File getWorldDirectory() {
            return null;
        }

        @Override
        public File getMapFileFromName(String mapName) {
            return null;
        }

        @Override
        public TemplateManager getStructureTemplateManager() {
            return null;
        }
    }
}