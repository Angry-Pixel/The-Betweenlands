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
import thebetweenlands.common.entity.mobs.EntityRockSnot;
import thebetweenlands.common.lib.ModInfo;

public class EntityPropertyRockSnotType implements EntityProperty {
	private final boolean placed;

	public EntityPropertyRockSnotType(boolean placed) {
		this.placed = placed;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		return entity instanceof EntityRockSnot && ((EntityRockSnot)entity).getPlacedByPlayer() == this.placed;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPropertyRockSnotType> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "rock_snot_placed"), EntityPropertyRockSnotType.class);
		}

		@Override
		public JsonElement serialize(EntityPropertyRockSnotType property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.placed);
		}

		@Override
		public EntityPropertyRockSnotType deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new EntityPropertyRockSnotType(JsonUtils.getBoolean(element, this.getName().getPath()));
		}
	}
}