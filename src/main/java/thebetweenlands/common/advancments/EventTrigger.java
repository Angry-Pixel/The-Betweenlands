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

public class EventTrigger implements ICriterionTrigger<EventTrigger.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "event");

    private final Map<PlayerAdvancements, EventTrigger.Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        EventTrigger.Listeners listeners = this.listeners.computeIfAbsent(playerAdvancements, Listeners::new);

        listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        EventTrigger.Listeners listeners = this.listeners.get(playerAdvancements);

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
        return new EventTrigger.Instance(new ResourceLocation(JsonUtils.getString(json, "name", ModInfo.ASSETS_PREFIX + "all")));
    }

    public void trigger(EntityPlayerMP player, ResourceLocation location) {
        EventTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(location);
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        private final ResourceLocation location;

        public Instance(ResourceLocation location) {
            super(EventTrigger.ID);
            this.location = location;
        }

        public boolean test(ResourceLocation location) {
            return "all".equals(this.location.getResourcePath()) || this.location.equals(location);
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<EventTrigger.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(Listener<EventTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(Listener<EventTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(ResourceLocation location) {
            List<Listener<EventTrigger.Instance>> list = new ArrayList<>();

            for (Listener<EventTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(location)) {
                    list.add(listener);
                }
            }

            for (Listener<EventTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
