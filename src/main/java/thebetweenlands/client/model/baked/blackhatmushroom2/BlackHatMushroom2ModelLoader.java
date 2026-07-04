package thebetweenlands.client.model.baked.blackhatmushroom2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class BlackHatMushroom2ModelLoader implements IGeometryLoader<UnbakedBlackHatMushroom2Model> {
	public static final BlackHatMushroom2ModelLoader INSTANCE = new BlackHatMushroom2ModelLoader();

	@Override
	public UnbakedBlackHatMushroom2Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedBlackHatMushroom2Model();
	}
}

