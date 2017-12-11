package thebetweenlands.common.loot;

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

public class EntityPropertyPeatMummyShimmerstone implements EntityProperty {
	private final boolean hasShimmerstone;

	public EntityPropertyPeatMummyShimmerstone(boolean hasShimmerstone) {
		this.hasShimmerstone = hasShimmerstone;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		return entity instanceof EntityPeatMummy && ((EntityPeatMummy)entity).doesCarryShimmerstone() == this.hasShimmerstone;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPropertyPeatMummyShimmerstone> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "has_shimmerstone"), EntityPropertyPeatMummyShimmerstone.class);
		}

		@Override
		public JsonElement serialize(EntityPropertyPeatMummyShimmerstone property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.hasShimmerstone);
		}

		@Override
		public EntityPropertyPeatMummyShimmerstone deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new EntityPropertyPeatMummyShimmerstone(JsonUtils.getBoolean(element, this.getName().getResourcePath()));
		}
	}
}