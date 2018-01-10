package thebetweenlands.common.advancments;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PeatFireTrigger implements ICriterionTrigger<PeatFireTrigger.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "peat_fire");

    private final Map<PlayerAdvancements, PeatFireTrigger.Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        PeatFireTrigger.Listeners listeners = this.listeners.computeIfAbsent(playerAdvancements, Listeners::new);

        listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        PeatFireTrigger.Listeners listeners = this.listeners.get(playerAdvancements);

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
        return new PeatFireTrigger.Instance();
    }

    public void trigger(EntityPlayerMP player) {
        PeatFireTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger();
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        public Instance() {
            super(PeatFireTrigger.ID);
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<PeatFireTrigger.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty()
        {
            return this.listeners.isEmpty();
        }

        public void add(Listener<PeatFireTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(Listener<PeatFireTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger() {
            this.listeners.stream().findFirst().ifPresent(listener -> listener.grantCriterion(this.playerAdvancements));
        }
    }
}
