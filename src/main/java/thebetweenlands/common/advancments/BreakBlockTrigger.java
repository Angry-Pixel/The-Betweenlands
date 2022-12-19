package thebetweenlands.common.advancments;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import thebetweenlands.common.lib.ModInfo;

public class BreakBlockTrigger extends BLTrigger<BreakBlockTrigger.Instance, BreakBlockTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "break_block");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public BreakBlockTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new BreakBlockTrigger.Listener(playerAdvancements);
    }

    @Override
    public BreakBlockTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        BlockPredicate[] blockPredicates = BlockPredicate.deserializeArray(json.getAsJsonArray("blocks"));
        LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("location"));
        return new BreakBlockTrigger.Instance(blockPredicates, locationpredicate);
    }

    public void trigger(EntityPlayerMP player, BlockPos pos, IBlockState state) {
        BreakBlockTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(state, pos, player.getServerWorld());
        }
    }

    public static class Instance extends AbstractCriterionInstance {
        private final BlockPredicate[] blocks;
        private final LocationPredicate location;

        public Instance(BlockPredicate[] blocks, LocationPredicate location) {
            super(BreakBlockTrigger.ID);
            this.blocks = blocks;
            this.location = location;
        }

        public boolean test(IBlockState state, BlockPos pos, WorldServer world) {
            List<BlockPredicate> list = Lists.newArrayList(this.blocks);
            int amount = list.size();
            list.removeIf(predicate -> predicate.test(state));
            return amount > list.size() && this.location.test(world, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
        }
    }

    static class Listener extends BLTrigger.Listener<BreakBlockTrigger.Instance> {
        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(IBlockState state, BlockPos pos, WorldServer world) {
            List<ICriterionTrigger.Listener<BreakBlockTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<BreakBlockTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(state, pos, world)) {
                    list.add(listener);
                    break;
                }
            }

            for (ICriterionTrigger.Listener<BreakBlockTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
