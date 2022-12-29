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

public class LocationTrigger extends BLTrigger<LocationTrigger.Instance, LocationTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "location");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public LocationTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new LocationTrigger.Listener(playerAdvancements);
    }

    @Override
    public LocationTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new LocationTrigger.Instance(JsonUtils.getString(json, "name", ""));
    }

    public void trigger(EntityPlayerMP player, String location) {
        LocationTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(location);
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        private final String location;

        public Instance(String location) {
            super(LocationTrigger.ID);
            this.location = location;
        }

        public boolean test(String location) {
            return this.location.isEmpty() || this.location.equals(location);
        }
    }

    static class Listener extends BLTrigger.Listener<LocationTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(String location) {
            List<ICriterionTrigger.Listener<LocationTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<LocationTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(location)) {
                    list.add(listener);
                }
            }

            for (ICriterionTrigger.Listener<LocationTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
