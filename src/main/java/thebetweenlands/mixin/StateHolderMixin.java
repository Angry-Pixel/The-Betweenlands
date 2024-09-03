package thebetweenlands.mixin;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StateHolder.class)
public class StateHolderMixin<S> {

	@Shadow(remap = false)
	@Final
	private Reference2ObjectArrayMap<Property<?>, Comparable<?>> values;

	@Inject(method = "setValue", at = @At("HEAD"), cancellable = true, remap = false)
	@SuppressWarnings("unchecked")
	public <T extends Comparable<T>, V extends T> void ignoreWaterloggedProperty(Property<T> property, V value, CallbackInfoReturnable<S> cir) {
		if (property == BlockStateProperties.WATERLOGGED && this.values.get(property) == null) {
			cir.setReturnValue((S) this);
		}
	}
}
