package thebetweenlands.common.registries;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.item.CompostData;

public class DataComponentRegistry {

	public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(TheBetweenlands.ID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompostData>> COMPOST = COMPONENTS.register("compost", () -> DataComponentType.<CompostData>builder().persistent(CompostData.CODEC).networkSynchronized(CompostData.STREAM_CODEC).cacheEncoding().build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> PESTLE_ACTIVE = COMPONENTS.register("active", () -> DataComponentType.<Unit>builder().persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).cacheEncoding().build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> SIMULACRUM_EFFECT = COMPONENTS.register("simulacrum_effect", () -> DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> ROT_TIME = COMPONENTS.register("rot_time", () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

}
