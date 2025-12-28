package thebetweenlands.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import thebetweenlands.util.IBetweenlandsRandomStateExtension;

/**
 * @author JoshieGemFinder
 * Mixin that exposes the level seed from a RandomState
 */
@Mixin(RandomState.class)
public class RandomStateMixin implements IBetweenlandsRandomStateExtension {

	@Mutable
	@Unique
	@Final
	private long betweenlands$levelSeed;

	@Inject(method = "<init>(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/HolderGetter;J)V", at = @At("TAIL"))
	public void setSeed(NoiseGeneratorSettings settings, HolderGetter<?> noiseParametersGetter, final long levelSeed, CallbackInfo ci) {
		this.betweenlands$levelSeed = levelSeed;
	}

	@Override
	public long thebetweenlands$getLevelSeed() {
		return this.betweenlands$levelSeed;
	}

}
