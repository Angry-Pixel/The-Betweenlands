package thebetweenlands.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.common.block.entity.AspectrusCropBlockEntity;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.LootFunctionRegistry;

import java.util.List;

public class SetAspectFromCropFunction extends LootItemConditionalFunction {

    public static final MapCodec<SetAspectFromCropFunction> CODEC =
        RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .apply(instance, SetAspectFromCropFunction::new));

    public SetAspectFromCropFunction(List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity be = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (be instanceof AspectrusCropBlockEntity crop) {
            Aspect aspect = crop.getAspect();
            if (aspect != null) {
                stack.set(DataComponentRegistry.ASPECT_CONTENTS.get(),
                    new AspectContents(aspect.type(), aspect.amount()));
            }
        }
        return stack;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return LootFunctionRegistry.SET_ASPECT_FROM_CROP.get();
    }

    public static LootItemFunction.Builder setAspectFromCrop() {
        return simpleBuilder(SetAspectFromCropFunction::new);
    }
}