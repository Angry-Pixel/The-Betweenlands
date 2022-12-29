package thebetweenlands.common.advancments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import thebetweenlands.common.lib.ModInfo;

public class RightClickBlockTrigger extends BLTrigger<RightClickBlockTrigger.Instance, RightClickBlockTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "right_click_block");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public RightClickBlockTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new RightClickBlockTrigger.Listener(playerAdvancements);
    }

    @Override
    public RightClickBlockTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        JsonElement sides = json.get("sides");
        EnumFacing[] facings = null;
        if (sides != null && !sides.isJsonNull()) {

        }
        BlockPredicate[] blockPredicates = BlockPredicate.deserializeArray(json.getAsJsonArray("blocks"));
        LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("location"));
        ItemPredicate[] itemPredicate = ItemPredicate.deserializeArray(json.get("items"));
        return new RightClickBlockTrigger.Instance(itemPredicate, blockPredicates, locationpredicate, facings);
    }

    public void trigger(ItemStack stack, EntityPlayerMP player, BlockPos pos, IBlockState state, EnumFacing face) {
        RightClickBlockTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(stack, state, pos, face, player.getServerWorld());
        }
    }

    public static class Instance extends AbstractCriterionInstance {
        private final ItemPredicate[] items;
        private final BlockPredicate[] blocks;
        private final LocationPredicate location;
        private final EnumFacing[] facings;

        public Instance(ItemPredicate[] items, BlockPredicate[] blocks, LocationPredicate location, EnumFacing[] facings) {
            super(RightClickBlockTrigger.ID);
            this.items = items;
            this.blocks = blocks;
            this.location = location;
            this.facings = facings;
        }

        public boolean test(ItemStack stack, IBlockState state, BlockPos pos, EnumFacing face, WorldServer world) {
            List<BlockPredicate> blockList = Lists.newArrayList(this.blocks);
            List<ItemPredicate> itemList = Lists.newArrayList(this.items);
            int blockAmount = blockList.size();
            int itemAmount = itemList.size();
            blockList.removeIf(predicate -> predicate.test(state));
            itemList.removeIf(predicate -> predicate.test(stack));
            boolean matchSide = facings == null || facings.length <= 0 || Arrays.stream(facings).anyMatch(enumFacing -> enumFacing.equals(face));
            return matchSide && blockAmount > blockList.size() && itemAmount > itemList.size() && this.location.test(world, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
        }
    }

    static class Listener extends BLTrigger.Listener<RightClickBlockTrigger.Instance> {
        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(ItemStack stack, IBlockState state, BlockPos pos, EnumFacing face, WorldServer world) {
            List<ICriterionTrigger.Listener<RightClickBlockTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<RightClickBlockTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(stack, state, pos, face, world)) {
                    list.add(listener);
                    break;
                }
            }

            for (ICriterionTrigger.Listener<RightClickBlockTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
