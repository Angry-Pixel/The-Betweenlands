package thebetweenlands.common.registries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.frog.FrogVariant;

import java.util.List;
import java.util.Optional;

public class EntityDataSerializerRegistry {

	public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, TheBetweenlands.ID);

	public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<List<Optional<BlockPos>>>> OPTIONAL_POS_LIST = DATA_SERIALIZERS.register("optional_pos_list", () -> EntityDataSerializer.forValueType(BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional).apply(ByteBufCodecs.list())));
	public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<List<Direction>>> DIRECTION_LIST = DATA_SERIALIZERS.register("direction_list", () -> EntityDataSerializer.forValueType(Direction.STREAM_CODEC.apply(ByteBufCodecs.list())));
	public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Holder<FrogVariant>>> FROG_VARIANT = DATA_SERIALIZERS.register("frog_variant", () -> EntityDataSerializer.forValueType(ByteBufCodecs.holderRegistry(BLRegistries.Keys.FROG_VARIANT)));

}
