package thebetweenlands.client.model.baked.blackhatmushroom3;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class BlackHatMushroom3ModelLoader implements IGeometryLoader<UnbakedBlackHatMushroom3Model> {
	public static final BlackHatMushroom3ModelLoader INSTANCE = new BlackHatMushroom3ModelLoader();

	@Override
	public UnbakedBlackHatMushroom3Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedBlackHatMushroom3Model();
	}
}

