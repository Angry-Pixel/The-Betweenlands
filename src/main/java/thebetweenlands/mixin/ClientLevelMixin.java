package thebetweenlands.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thebetweenlands.client.BetweenlandsSpecialEffects;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.RiftEvent;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

	// For level lighting
	@Inject(method = "getSkyDarken", at = @At("RETURN"), cancellable = true, remap = false)
	private void getBLSkyDarken(float partialTick, CallbackInfoReturnable<Float> cir) {
		Level level = (Level) (Object) this;
		if (level.dimension() == DimensionRegistries.DIMENSION_KEY) {
			float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
			cir.setReturnValue(BetweenlandsSpecialEffects.overworldSkyBrightness(level, partialTicks));
			cir.cancel();
		}
	}
}
