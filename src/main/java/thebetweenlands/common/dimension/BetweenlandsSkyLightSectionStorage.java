package thebetweenlands.common.dimension;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.SkyLightSectionStorage;
import thebetweenlands.TheBetweenlands;

import java.util.Arrays;

public class BetweenlandsSkyLightSectionStorage extends LayerLightSectionStorage<SkyLightSectionStorage.SkyDataLayerStorageMap> {
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final LongSet sectionsWithSources = new LongOpenHashSet();
    private final LongSet sectionsToAddSourcesTo = new LongOpenHashSet();
    private final LongSet sectionsToRemoveSourcesFrom = new LongOpenHashSet();
    private final LongSet columnsWithSkySources = new LongOpenHashSet();
    private volatile boolean hasSourceInconsistencies;

    protected BetweenlandsSkyLightSectionStorage(LightChunkGetter p_75868_) {
        super(LightLayer.SKY, p_75868_, new SkyLightSectionStorage.SkyDataLayerStorageMap(new Long2ObjectOpenHashMap<>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    protected int getLightValue(long p_75880_) {
        return this.getLightValue(p_75880_, false);
    }

    protected int getLightValue(long p_164458_, boolean p_164459_) {

        long i = SectionPos.blockToSection(p_164458_);
        int j = SectionPos.y(i);
        SkyLightSectionStorage.SkyDataLayerStorageMap skylightsectionstorage$skydatalayerstoragemap = p_164459_ ? this.updatingSectionData : this.visibleSectionData;
        int k = skylightsectionstorage$skydatalayerstoragemap.topSections.get(SectionPos.getZeroNode(i));
        if (k != skylightsectionstorage$skydatalayerstoragemap.currentLowestY && j < k) {
            DataLayer datalayer = this.getDataLayer(skylightsectionstorage$skydatalayerstoragemap, i);
            if (datalayer == null) {
                for(p_164458_ = BlockPos.getFlatIndex(p_164458_); datalayer == null; datalayer = this.getDataLayer(skylightsectionstorage$skydatalayerstoragemap, i)) {
                    ++j;
                    if (j >= k) {
                        return 15;
                    }

                    p_164458_ = BlockPos.offset(p_164458_, 0, 16, 0);
                    i = SectionPos.offset(i, Direction.UP);
                }
            }
            return datalayer.get(SectionPos.sectionRelative(BlockPos.getX(p_164458_)), SectionPos.sectionRelative(BlockPos.getY(p_164458_)), SectionPos.sectionRelative(BlockPos.getZ(p_164458_)));
        } else {
            return p_164459_ && !this.lightOnInSection(i) ? 0 : 15;
        }
    }

    protected void onNodeAdded(long p_75885_) {

        int i = SectionPos.y(p_75885_);
        if ((this.updatingSectionData).currentLowestY > i) {
            (this.updatingSectionData).currentLowestY = i;
            (this.updatingSectionData).topSections.defaultReturnValue((this.updatingSectionData).currentLowestY);
        }

        long j = SectionPos.getZeroNode(p_75885_);
        int k = (this.updatingSectionData).topSections.get(j);
        if (k < i + 1) {
            (this.updatingSectionData).topSections.put(j, i + 1);
            if (this.columnsWithSkySources.contains(j)) {
                this.queueAddSource(p_75885_);
                if (k > (this.updatingSectionData).currentLowestY) {
                    long l = SectionPos.asLong(SectionPos.x(p_75885_), k - 1, SectionPos.z(p_75885_));
                    this.queueRemoveSource(l);
                }

                this.recheckInconsistencyFlag();
            }
        }

    }

    private void queueRemoveSource(long p_75895_) {
        this.sectionsToRemoveSourcesFrom.add(p_75895_);
        this.sectionsToAddSourcesTo.remove(p_75895_);
    }

    private void queueAddSource(long p_75897_) {
        this.sectionsToAddSourcesTo.add(p_75897_);
        this.sectionsToRemoveSourcesFrom.remove(p_75897_);
    }

    private void recheckInconsistencyFlag() {
        this.hasSourceInconsistencies = !this.sectionsToAddSourcesTo.isEmpty() || !this.sectionsToRemoveSourcesFrom.isEmpty();
    }

    protected void onNodeRemoved(long p_75887_) {
        long i = SectionPos.getZeroNode(p_75887_);
        boolean flag = this.columnsWithSkySources.contains(i);
        if (flag) {
            this.queueRemoveSource(p_75887_);
        }

        int j = SectionPos.y(p_75887_);
        if ((this.updatingSectionData).topSections.get(i) == j + 1) {
            long k;
            for(k = p_75887_; !this.storingLightForSection(k) && this.hasSectionsBelow(j); k = SectionPos.offset(k, Direction.DOWN)) {
                --j;
            }

            if (this.storingLightForSection(k)) {
                (this.updatingSectionData).topSections.put(i, j + 1);
                if (flag) {
                    this.queueAddSource(k);
                }
            } else {
                (this.updatingSectionData).topSections.remove(i);
            }
        }

        if (flag) {
            this.recheckInconsistencyFlag();
        }

    }

    protected void enableLightSources(long p_75877_, boolean p_75878_) {
        this.runAllUpdates();
        if (p_75878_ && this.columnsWithSkySources.add(p_75877_)) {
            int i = (this.updatingSectionData).topSections.get(p_75877_);
            if (i != (this.updatingSectionData).currentLowestY) {
                long j = SectionPos.asLong(SectionPos.x(p_75877_), i - 1, SectionPos.z(p_75877_));
                this.queueAddSource(j);
                this.recheckInconsistencyFlag();
            }
        } else if (!p_75878_) {
            this.columnsWithSkySources.remove(p_75877_);
        }

    }

    protected boolean hasInconsistencies() {
        return super.hasInconsistencies() || this.hasSourceInconsistencies;
    }

    protected DataLayer createDataLayer(long p_75883_) {
        DataLayer datalayer = this.queuedSections.get(p_75883_);
        if (datalayer != null) {
            return datalayer;
        } else {
            long i = SectionPos.offset(p_75883_, Direction.UP);
            int j = (this.updatingSectionData).topSections.get(SectionPos.getZeroNode(p_75883_));
            if (j != (this.updatingSectionData).currentLowestY && SectionPos.y(i) < j) {
                DataLayer datalayer1;
                while((datalayer1 = this.getDataLayer(i, true)) == null) {
                    i = SectionPos.offset(i, Direction.UP);
                }

                return repeatFirstLayer(datalayer1);
            } else {
                return new DataLayer();
            }
        }
    }

    private static DataLayer repeatFirstLayer(DataLayer p_182513_) {
        if (p_182513_.isEmpty()) {
            return new DataLayer();
        } else {
            byte[] abyte = p_182513_.getData();
            byte[] abyte1 = new byte[2048];

            for(int i = 0; i < 16; ++i) {
                System.arraycopy(abyte, 0, abyte1, i * 128, 128);
            }

            return new DataLayer(abyte1);
        }
    }


    protected void markNewInconsistencies(LayerLightEngine<SkyLightSectionStorage.SkyDataLayerStorageMap, ?> p_75873_, boolean p_75874_, boolean p_75875_) {
        super.markNewInconsistencies(p_75873_, p_75874_, p_75875_);

        if (p_75874_) {
            if (!this.sectionsToAddSourcesTo.isEmpty()) {
                for(long i : this.sectionsToAddSourcesTo) {
                    int j = this.getLevel(i);
                    if (j != 2 && !this.sectionsToRemoveSourcesFrom.contains(i) && this.sectionsWithSources.add(i)) {
                        if (j == 1) {
                            this.clearQueuedSectionBlocks(p_75873_, i);
                            if (this.changedSections.add(i)) {
                                this.updatingSectionData.copyDataLayer(i);
                            }

                            Arrays.fill(this.getDataLayer(i, true).getData(), (byte)-1);
                            int i3 = SectionPos.sectionToBlockCoord(SectionPos.x(i));
                            int k3 = SectionPos.sectionToBlockCoord(SectionPos.y(i));
                            int i4 = SectionPos.sectionToBlockCoord(SectionPos.z(i));

                            for(Direction direction : HORIZONTALS) {
                                long j1 = SectionPos.offset(i, direction);
                                if ((this.sectionsToRemoveSourcesFrom.contains(j1) || !this.sectionsWithSources.contains(j1) && !this.sectionsToAddSourcesTo.contains(j1)) && this.storingLightForSection(j1)) {
                                    for(int k1 = 0; k1 < 16; ++k1) {
                                        for(int l1 = 0; l1 < 16; ++l1) {
                                            long i2;
                                            long j2;
                                            switch(direction) {
                                                case NORTH:
                                                    i2 = BlockPos.asLong(i3 + k1, k3 + l1, i4);
                                                    j2 = BlockPos.asLong(i3 + k1, k3 + l1, i4 - 1);
                                                    break;
                                                case SOUTH:
                                                    i2 = BlockPos.asLong(i3 + k1, k3 + l1, i4 + 16 - 1);
                                                    j2 = BlockPos.asLong(i3 + k1, k3 + l1, i4 + 16);
                                                    break;
                                                case WEST:
                                                    i2 = BlockPos.asLong(i3, k3 + k1, i4 + l1);
                                                    j2 = BlockPos.asLong(i3 - 1, k3 + k1, i4 + l1);
                                                    break;
                                                default:
                                                    i2 = BlockPos.asLong(i3 + 16 - 1, k3 + k1, i4 + l1);
                                                    j2 = BlockPos.asLong(i3 + 16, k3 + k1, i4 + l1);
                                            }

                                            p_75873_.checkEdge(i2, j2, p_75873_.computeLevelFromNeighbor(i2, j2, 15), true);
                                        }
                                    }
                                }
                            }

                            for(int j4 = 0; j4 < 16; ++j4) {
                                for(int k4 = 0; k4 < 16; ++k4) {
                                    long l4 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(i), j4), SectionPos.sectionToBlockCoord(SectionPos.y(i)), SectionPos.sectionToBlockCoord(SectionPos.z(i), k4));
                                    long i5 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(i), j4), SectionPos.sectionToBlockCoord(SectionPos.y(i)) - 1, SectionPos.sectionToBlockCoord(SectionPos.z(i), k4));
                                    p_75873_.checkEdge(l4, i5, p_75873_.computeLevelFromNeighbor(l4, i5, 0), true);
                                }
                            }
                        } else {
                            for(int k = 0; k < 16; ++k) {
                                for(int l = 0; l < 16; ++l) {
                                    long i1 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(i), k), SectionPos.sectionToBlockCoord(SectionPos.y(i), 15), SectionPos.sectionToBlockCoord(SectionPos.z(i), l));
                                    p_75873_.checkEdge(Long.MAX_VALUE, i1, 0, true);
                                }
                            }
                        }
                    }
                }
            }

            this.sectionsToAddSourcesTo.clear();
            if (!this.sectionsToRemoveSourcesFrom.isEmpty()) {
                for(long k2 : this.sectionsToRemoveSourcesFrom) {
                    if (this.sectionsWithSources.remove(k2) && this.storingLightForSection(k2)) {
                        for(int l2 = 0; l2 < 16; ++l2) {
                            for(int j3 = 0; j3 < 16; ++j3) {
                                long l3 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(k2), l2), SectionPos.sectionToBlockCoord(SectionPos.y(k2), 15), SectionPos.sectionToBlockCoord(SectionPos.z(k2), j3));
                                p_75873_.checkEdge(Long.MAX_VALUE, l3, 15, false);
                            }
                        }
                    }
                }
            }

            this.sectionsToRemoveSourcesFrom.clear();
            this.hasSourceInconsistencies = false;
        }
    }

    protected boolean hasSectionsBelow(int p_75871_) {
        return p_75871_ >= (this.updatingSectionData).currentLowestY;
    }

    protected boolean isAboveData(long p_75891_) {
        long i = SectionPos.getZeroNode(p_75891_);
        int j = (this.updatingSectionData).topSections.get(i);
        return j == (this.updatingSectionData).currentLowestY || SectionPos.y(p_75891_) >= j;
    }

    protected boolean lightOnInSection(long p_75893_) {
        long i = SectionPos.getZeroNode(p_75893_);
        return this.columnsWithSkySources.contains(i);
    }
}
