package thebetweenlands.common.registries;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.item.CompostData;

public class DataComponentRegistry {

	public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(TheBetweenlands.ID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompostData>> COMPOST = COMPONENTS.register("compost", () -> DataComponentType.<CompostData>builder().persistent(CompostData.CODEC).networkSynchronized(CompostData.STREAM_CODEC).cacheEncoding().build());
}
