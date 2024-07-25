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
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CORROSIVE = COMPONENTS.register("corrosive", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FISHING_ROD_BAIT = COMPONENTS.register("bait", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FLUID_COLOR = COMPONENTS.register("fluid_color", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> GLU = COMPONENTS.register("glu", () -> DataComponentType.<Unit>builder().persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> PESTLE_ACTIVE = COMPONENTS.register("active", () -> DataComponentType.<Unit>builder().persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> SIMULACRUM_EFFECT = COMPONENTS.register("simulacrum_effect", () -> DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> ROT_TIME = COMPONENTS.register("rot_time", () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

}
