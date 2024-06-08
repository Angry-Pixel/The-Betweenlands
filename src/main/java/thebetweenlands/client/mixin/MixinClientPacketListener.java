package thebetweenlands.client.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.netty.buffer.Unpooled;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.ClientTelemetryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.world.BetweenlandsClientLevel;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Final @Shadow Minecraft minecraft;
    @Shadow private RegistryAccess.Frozen registryAccess = RegistryAccess.BUILTIN.get();
    @Final @Shadow private Connection connection;
    @Shadow private Set<ResourceKey<Level>> levels;
    @Shadow private ClientLevel level;
    @Final @Shadow private ClientTelemetryManager telemetryManager;
    @Shadow private int serverChunkRadius;
    @Shadow private int serverSimulationDistance;
    @Shadow private ClientLevel.ClientLevelData levelData;

    // Handel custom dim clientlevel creation on login
    @Inject(method = "handleLogin", at = @At("HEAD"), cancellable = true)
    public void handleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        // TODO: move custom dim registry into standalone lib mod
        if (packet.dimension().compareTo(DimensionRegistries.BETWEENLANDS_DIMENSION_KEY) != 0) {
            return;
        }

        // Do Client level inject
        PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener) (Object)this, this.minecraft);
        this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, (ClientPacketListener) (Object)this);
        this.registryAccess = packet.registryHolder();
        if (!this.connection.isMemoryConnection()) {
            this.registryAccess.registries().forEach((p_205542_) -> {
                p_205542_.value().resetTags();
            });
        }

        List<ResourceKey<Level>> list = Lists.newArrayList(packet.levels());
        Collections.shuffle(list);
        this.levels = Sets.newLinkedHashSet(list);
        ResourceKey<Level> resourcekey = packet.dimension();
        Holder<DimensionType> holder = packet.dimensionType();
        this.serverChunkRadius = packet.chunkRadius();
        this.serverSimulationDistance = packet.simulationDistance();
        boolean flag = packet.isDebug();
        boolean flag1 = packet.isFlat();

        // Add dimension specified level data type
        ClientLevel.ClientLevelData clientlevel$clientleveldata = new ClientLevel.ClientLevelData(Difficulty.NORMAL, packet.hardcore(), flag1);
        this.levelData = clientlevel$clientleveldata;

        // Add dimension specified level type and set level
        this.level = new BetweenlandsClientLevel((ClientPacketListener) (Object)this, clientlevel$clientleveldata, resourcekey, holder, this.serverChunkRadius, this.serverSimulationDistance, this.minecraft::getProfiler, this.minecraft.levelRenderer, flag, packet.seed());
        this.minecraft.setLevel(this.level);
        if (this.minecraft.player == null) {
            this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook());
            this.minecraft.player.setYRot(-180.0F);
            if (this.minecraft.getSingleplayerServer() != null) {
                this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
            }
        }

        this.minecraft.debugRenderer.clear();
        this.minecraft.player.resetPos();
        net.minecraftforge.client.ForgeHooksClient.firePlayerLogin(this.minecraft.gameMode, this.minecraft.player, this.minecraft.getConnection().getConnection());
        int i = packet.playerId();
        this.minecraft.player.setId(i);
        this.level.addPlayer(i, this.minecraft.player);
        this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
        this.minecraft.cameraEntity = this.minecraft.player;
        this.minecraft.setScreen(new ReceivingLevelScreen());
        this.minecraft.player.setReducedDebugInfo(packet.reducedDebugInfo());
        this.minecraft.player.setShowDeathScreen(packet.showDeathScreen());
        this.minecraft.gameMode.setLocalMode(packet.gameType(), packet.previousGameType());
        this.minecraft.options.setServerRenderDistance(packet.chunkRadius());
        net.minecraftforge.network.NetworkHooks.sendMCRegistryPackets(connection, "PLAY_TO_SERVER");
        this.minecraft.options.broadcastOptions();
        this.connection.send(new ServerboundCustomPayloadPacket(ServerboundCustomPayloadPacket.BRAND, (new FriendlyByteBuf(Unpooled.buffer())).writeUtf(ClientBrandRetriever.getClientModName())));
        this.minecraft.getGame().onStartGameSession();
        this.telemetryManager.onPlayerInfoReceived(packet.gameType(), packet.hardcore());

        // Cancel
        ci.cancel();
        return;
    }

    // Handel custom dim clientlevel creation on respawn
    @Inject(method = "handleRespawn", at = @At("HEAD"), cancellable = true)
    public void handleRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        if (packet.getDimension().compareTo(DimensionRegistries.BETWEENLANDS_DIMENSION_KEY) != 0) {
            return;
        }

        // Do Client level inject
        PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener) (Object)this, this.minecraft);
        ResourceKey<Level> resourcekey = packet.getDimension();
        Holder<DimensionType> holder = packet.getDimensionType();
        LocalPlayer localplayer = this.minecraft.player;
        int i = localplayer.getId();
        if (resourcekey != localplayer.level.dimension()) {
            Scoreboard scoreboard = this.level.getScoreboard();
            Map<String, MapItemSavedData> map = this.level.getAllMapData();
            boolean flag = packet.isDebug();
            boolean flag1 = packet.isFlat();

            // Add dimension specified level data type
            ClientLevel.ClientLevelData clientlevel$clientleveldata = new ClientLevel.ClientLevelData(this.levelData.getDifficulty(), this.levelData.isHardcore(), flag1);
            this.levelData = clientlevel$clientleveldata;

            // Add dimension specified level type
            this.level = new BetweenlandsClientLevel((ClientPacketListener) (Object)this, clientlevel$clientleveldata, resourcekey, holder, this.serverChunkRadius, this.serverSimulationDistance, this.minecraft::getProfiler, this.minecraft.levelRenderer, flag, packet.getSeed());

            this.level.setScoreboard(scoreboard);
            this.level.addMapData(map);
            this.minecraft.setLevel(this.level);
            this.minecraft.setScreen(new ReceivingLevelScreen());
        }

        String s = localplayer.getServerBrand();
        this.minecraft.cameraEntity = null;
        LocalPlayer localplayer1 = this.minecraft.gameMode.createPlayer(this.level, localplayer.getStats(), localplayer.getRecipeBook(), localplayer.isShiftKeyDown(), localplayer.isSprinting());
        localplayer1.setId(i);
        this.minecraft.player = localplayer1;
        if (resourcekey != localplayer.level.dimension()) {
            this.minecraft.getMusicManager().stopPlaying();
        }

        this.minecraft.cameraEntity = localplayer1;
        localplayer1.getEntityData().assignValues(localplayer.getEntityData().getAll());
        if (packet.shouldKeepAllPlayerData()) {
            localplayer1.getAttributes().assignValues(localplayer.getAttributes());
        }

        localplayer1.updateSyncFields(localplayer); // Forge: fix MC-10657
        localplayer1.resetPos();
        localplayer1.setServerBrand(s);
        net.minecraftforge.client.ForgeHooksClient.firePlayerRespawn(this.minecraft.gameMode, localplayer, localplayer1, localplayer1.connection.getConnection());
        this.level.addPlayer(i, localplayer1);
        localplayer1.setYRot(-180.0F);
        localplayer1.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(localplayer1);
        localplayer1.setReducedDebugInfo(localplayer.isReducedDebugInfo());
        localplayer1.setShowDeathScreen(localplayer.shouldShowDeathScreen());
        if (this.minecraft.screen instanceof DeathScreen) {
            this.minecraft.setScreen((Screen)null);
        }

        this.minecraft.gameMode.setLocalMode(packet.getPlayerGameType(), packet.getPreviousPlayerGameType());
    }
}
