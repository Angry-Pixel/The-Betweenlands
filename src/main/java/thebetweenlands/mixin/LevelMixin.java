package thebetweenlands.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.lighting.SkyLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thebetweenlands.client.BetweenlandsSpecialEffects;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.RiftEvent;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

import static thebetweenlands.client.BetweenlandsSpecialEffects.overworldSkyBrightness;

@Mixin(Level.class)
public abstract class LevelMixin {

	// For game logic & daylight detectors
	@Inject(method = "updateSkyBrightness", at = @At("HEAD"), cancellable = true, remap = false)
	private void updateBLSkyBrightness(CallbackInfo ci) {
		Level level = (Level) (Object) this;
		if (level.dimension() == DimensionRegistries.DIMENSION_KEY) {
			float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
			level.skyDarken = (int)((1.0f - BetweenlandsSpecialEffects.overworldSkyBrightness(level, partialTicks)) * 16.0f);
			ci.cancel();
		}
	}

}