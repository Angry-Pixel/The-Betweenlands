package thebetweenlands.common.registries;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.loot.AnadiaBodyPredicate;
import thebetweenlands.common.loot.AnadiaHeadPredicate;
import thebetweenlands.common.loot.AnadiaTailPredicate;

public class EntityPredicateRegistry {

	public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> PREDICATES = DeferredRegister.create(Registries.ENTITY_SUB_PREDICATE_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<AnadiaHeadPredicate>> ANADIA_HEAD = PREDICATES.register("anadia_head_type", () -> AnadiaHeadPredicate.CODEC);
	public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<AnadiaBodyPredicate>> ANADIA_BODY = PREDICATES.register("anadia_body_type", () -> AnadiaBodyPredicate.CODEC);
	public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<AnadiaTailPredicate>> ANADIA_TAIL = PREDICATES.register("anadia_tail_type", () -> AnadiaTailPredicate.CODEC);

}
