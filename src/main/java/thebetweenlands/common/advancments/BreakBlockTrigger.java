package thebetweenlands.common.advancments;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import thebetweenlands.common.lib.ModInfo;

import java.util.*;

public class BreakBlockTrigger implements ICriterionTrigger<BreakBlockTrigger.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "break_block");

    private final Map<PlayerAdvancements, BreakBlockTrigger.Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        BreakBlockTrigger.Listeners listeners = this.listeners.computeIfAbsent(playerAdvancements, Listeners::new);

        listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        BreakBlockTrigger.Listeners listeners = this.listeners.get(playerAdvancements);

        if (listeners != null) {
            listeners.remove(listener);

            if (listeners.isEmpty()) {
                this.listeners.remove(playerAdvancements);
            }
        }
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        BlockPredicate[] blockPredicates = BlockPredicate.deserializeArray(json.getAsJsonArray("blocks"));
        LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("location"));
        return new BreakBlockTrigger.Instance(blockPredicates, locationpredicate);
    }

    public void trigger(EntityPlayerMP player, BlockPos pos, IBlockState state) {
        BreakBlockTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());

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

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<BreakBlockTrigger.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(Listener<BreakBlockTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(Listener<BreakBlockTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(IBlockState state, BlockPos pos, WorldServer world) {
            List<Listener<BreakBlockTrigger.Instance>> list = new ArrayList<>();

            for (Listener<BreakBlockTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(state, pos, world)) {
                    list.add(listener);
                    break;
                }
            }

            for (Listener<BreakBlockTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
