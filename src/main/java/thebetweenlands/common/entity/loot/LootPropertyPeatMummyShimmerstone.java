package thebetweenlands.common.entity.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.lib.ModInfo;

public class LootPropertyPeatMummyShimmerstone implements EntityProperty {
	private final boolean hasShimmerstone;

	public LootPropertyPeatMummyShimmerstone(boolean hasShimmerstone) {
		this.hasShimmerstone = hasShimmerstone;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		return entity instanceof EntityPeatMummy && ((EntityPeatMummy)entity).doesCarryShimmerstone() == this.hasShimmerstone;
	}

	public static class Serializer extends EntityProperty.Serializer<LootPropertyPeatMummyShimmerstone> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "has_shimmerstone"), LootPropertyPeatMummyShimmerstone.class);
		}

		@Override
		public JsonElement serialize(LootPropertyPeatMummyShimmerstone property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.hasShimmerstone);
		}

		@Override
		public LootPropertyPeatMummyShimmerstone deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new LootPropertyPeatMummyShimmerstone(JsonUtils.getBoolean(element, this.getName().getResourcePath()));
		}
	}
}