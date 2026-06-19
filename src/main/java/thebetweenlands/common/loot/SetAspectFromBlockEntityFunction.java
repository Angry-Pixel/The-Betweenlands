package thebetweenlands.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.IAspectBlockEntity;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.LootFunctionRegistry;

import java.util.List;

public class SetAspectFromBlockEntityFunction extends LootItemConditionalFunction {

    private final Item fallbackItem;

    public static final MapCodec<SetAspectFromBlockEntityFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        commonFields(instance).and(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("fallback_item").forGetter(o -> o.fallbackItem)
        ).apply(instance, SetAspectFromBlockEntityFunction::new));

    public SetAspectFromBlockEntityFunction(List<LootItemCondition> conditions, Item fallbackItem) {
        super(conditions);
        this.fallbackItem = fallbackItem;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity be = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (be instanceof IAspectBlockEntity blockEntity) {
            Aspect aspect = blockEntity.getAspect();
            TheBetweenlands.LOGGER.info("SAFBEF ran with aspect " + aspect + " and amount " + (aspect == null ? 0 : aspect.amount()));
            if (aspect != null && aspect.amount() > 0) {
                stack.set(DataComponentRegistry.ASPECT_CONTENTS.get(), new AspectContents(aspect.type(), aspect.amount()));
                return stack;
            }
        } 
        return new ItemStack(fallbackItem);
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return LootFunctionRegistry.SET_ASPECT_FROM_BLOCK_ENTITY.get();
    }

    public static LootItemFunction.Builder setAspectFromBlockEntity(Item fallbackItem) {
        return simpleBuilder(conditions -> new SetAspectFromBlockEntityFunction(conditions, fallbackItem));
    }
}