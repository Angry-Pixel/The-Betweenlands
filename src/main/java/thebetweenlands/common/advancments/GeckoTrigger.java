package thebetweenlands.common.advancments;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementProgress;
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

public class GeckoTrigger implements ICriterionTrigger<GeckoTrigger.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "gecko");

    private final Map<PlayerAdvancements, GeckoTrigger.Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        GeckoTrigger.Listeners listeners = this.listeners.computeIfAbsent(playerAdvancements, Listeners::new);

        listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        GeckoTrigger.Listeners listeners = this.listeners.get(playerAdvancements);

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
        boolean test = JsonUtils.getBoolean(json, "test", false);
        boolean release = JsonUtils.getBoolean(json, "release", false);
        return new GeckoTrigger.Instance(test, release);
    }

    public void trigger(EntityPlayerMP player, boolean test, boolean release) {
        GeckoTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(test, release);
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        private final boolean test;
        private final boolean release;

        public Instance(boolean test, boolean release) {
            super(GeckoTrigger.ID);
            this.test = test;
            this.release = release;
        }

        public boolean test(boolean test, boolean release) {
            return (this.test && test) || (this.release && release);
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<GeckoTrigger.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty()
        {
            return this.listeners.isEmpty();
        }

        public void add(Listener<GeckoTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(Listener<GeckoTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(boolean test, boolean release) {
            List<Listener<GeckoTrigger.Instance>> list = new ArrayList<>();

            for (Listener<GeckoTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(test, release)) {
                    list.add(listener);
                }
            }

            for (Listener<GeckoTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
