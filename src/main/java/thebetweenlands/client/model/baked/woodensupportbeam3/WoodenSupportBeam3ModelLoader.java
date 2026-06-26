package thebetweenlands.client.model.baked.woodensupportbeam3;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WoodenSupportBeam3ModelLoader implements IGeometryLoader<UnbakedWoodenSupportBeam3Model> {
	public static final WoodenSupportBeam3ModelLoader INSTANCE = new WoodenSupportBeam3ModelLoader();

	@Override
	public UnbakedWoodenSupportBeam3Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWoodenSupportBeam3Model();
	}
}

