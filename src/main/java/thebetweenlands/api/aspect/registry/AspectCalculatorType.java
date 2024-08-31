package thebetweenlands.api.aspect.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.Aspect;

import java.util.List;
import java.util.function.Function;

public interface AspectCalculatorType {

	Codec<AspectCalculatorType> CODEC = BLRegistries.ASPECT_CALCULATOR_TYPE.byNameCodec().dispatch(AspectCalculatorType::codec, Function.identity());

	List<Aspect> getAspects(HolderLookup.Provider provider, LegacyRandomSource random);

	MapCodec<? extends AspectCalculatorType> codec();
}
