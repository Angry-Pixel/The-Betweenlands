package thebetweenlands.common.advancments;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class EventTrigger extends BLTrigger<EventTrigger.Instance, EventTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "event");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public EventTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new Listener(playerAdvancements);
    }

    @Override
    public EventTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new EventTrigger.Instance(new ResourceLocation(JsonUtils.getString(json, "name", ModInfo.ASSETS_PREFIX + "all")));
    }

    public void trigger(EntityPlayerMP player, ResourceLocation location) {
        EventTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

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
            return "all".equals(this.location.getPath()) || this.location.equals(location);
        }
    }

    static class Listener extends BLTrigger.Listener<EventTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(ResourceLocation location) {
            List<ICriterionTrigger.Listener<EventTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<EventTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(location)) {
                    list.add(listener);
                }
            }

            for (ICriterionTrigger.Listener<EventTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
