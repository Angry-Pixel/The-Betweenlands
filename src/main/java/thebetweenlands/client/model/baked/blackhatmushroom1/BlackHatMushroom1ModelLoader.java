package thebetweenlands.client.model.baked.blackhatmushroom1;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class BlackHatMushroom1ModelLoader implements IGeometryLoader<UnbakedBlackHatMushroom1Model> {
	public static final BlackHatMushroom1ModelLoader INSTANCE = new BlackHatMushroom1ModelLoader();

	@Override
	public UnbakedBlackHatMushroom1Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedBlackHatMushroom1Model();
	}
}

