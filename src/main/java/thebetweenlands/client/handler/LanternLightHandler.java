package thebetweenlands.client.handler;

import java.util.function.Predicate;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import thebetweenlands.client.renderer.entity.FireflyRenderer;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.block.misc.BLLanternBlock;


public final class LanternLightHandler {//TODO: make this work in multiplayer
    private static final LongSet LANTERN_POSITIONS = new LongOpenHashSet();
    private static final int VIEW_DISTANCE = 12*16; //maybe make this be based on user's render distance? but probably not necessary
    private static final Predicate<BlockState> IS_LANTERN = state -> state.getBlock() instanceof BLLanternBlock;

    private LanternLightHandler() {}

    public static void init() {
        NeoForge.EVENT_BUS.addListener(LanternLightHandler::onRenderLevel);
        NeoForge.EVENT_BUS.addListener(LanternLightHandler::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(LanternLightHandler::onChunkUnload);
        NeoForge.EVENT_BUS.addListener(LanternLightHandler::onBlockPlace);
        NeoForge.EVENT_BUS.addListener(LanternLightHandler::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(LanternLightHandler::onLevelUnload);
    }

    static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) LANTERN_POSITIONS.clear();
    }

    static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel().isClientSide())) return;
        scanChunk((LevelChunk) event.getChunk());
    }

    static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel().isClientSide())) return;
        removeChunk((LevelChunk) event.getChunk());
    }

    static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getPlacedBlock().getBlock() instanceof BLLanternBlock) {
            LANTERN_POSITIONS.add(event.getPos().asLong());
        }
    }

    static void onBlockBreak(BlockEvent.BreakEvent event) {
        LANTERN_POSITIONS.remove(event.getPos().asLong());
    }

    private static void scanChunk(LevelChunk chunk) {//TODO: might be worth looking at making this more efficient if possible, as it runs every time a new chunk is loaded
        int baseX = chunk.getPos().getMinBlockX();
        int baseZ = chunk.getPos().getMinBlockZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int sectionIndex = 0; sectionIndex < chunk.getSections().length; sectionIndex++) {
            LevelChunkSection section = chunk.getSections()[sectionIndex];
            if (section == null || section.hasOnlyAir()) continue;
            if (!section.maybeHas(IS_LANTERN)) continue;

            int baseY = chunk.getSectionYFromSectionIndex(sectionIndex) << 4;

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (IS_LANTERN.test(state)) {
                            pos.set(baseX + x, baseY + y, baseZ + z);
                            LANTERN_POSITIONS.add(pos.asLong());
                        }
                    }
                }
            }
        }
    }

    private static void removeChunk(LevelChunk chunk) {
        int minX = chunk.getPos().getMinBlockX();
        int maxX = chunk.getPos().getMaxBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        int maxZ = chunk.getPos().getMaxBlockZ();

        LANTERN_POSITIONS.removeIf(packed -> {
            BlockPos pos = BlockPos.of(packed);
            return pos.getX() >= minX && pos.getX() <= maxX
                && pos.getZ() >= minZ && pos.getZ() <= maxZ;
        });
    }

    static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || !ShaderHelper.INSTANCE.isWorldShaderActive()) return;

        float partialTick = event.getPartialTick().getGameTimeDeltaTicks();
        float time = level.getGameTime() + partialTick;
        Vec3 cam = event.getCamera().getPosition();
        double maxDistSq = VIEW_DISTANCE * VIEW_DISTANCE;

        ShaderHelper.INSTANCE.require();

        for (long packed : LANTERN_POSITIONS) {
            BlockPos pos = BlockPos.of(packed);
            double dx = pos.getX() + 0.5 - cam.x;
            double dy = pos.getY() + 0.5 - cam.y;
            double dz = pos.getZ() + 0.5 - cam.z;
            if (dx * dx + dy * dy + dz * dz > maxDistSq) continue;

            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof BLLanternBlock)) continue;

            double x = pos.getX() + 0.5;
            double y = pos.getY() + (state.getValue(LanternBlock.HANGING) ? 0.55 : 0.3);
            double z = pos.getZ() + 0.5;

            float glowStrength = getGlow(pos, time);
            float radius = glowStrength * 7.0F;
            if (radius > 0.1F) {
                FireflyRenderer.addFireflyLight(x, y, z, radius);
            }
        }
    }

    private static float getGlow(BlockPos pos, float time) {
        float phase = (pos.hashCode() & 0xFF) / 255.0F * Mth.TWO_PI; //makes it so they slightly fade in and out, but not all at the same time
        return 0.6F + 0.4F * ((Mth.sin(time / 20.0F + phase) + 1.0F) * 0.5F);
    }
}