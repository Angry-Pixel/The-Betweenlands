package thebetweenlands.client.model.baked.woodensupportbeam1;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WoodenSupportBeam1ModelLoader implements IGeometryLoader<UnbakedWoodenSupportBeam1Model> {
	public static final WoodenSupportBeam1ModelLoader INSTANCE = new WoodenSupportBeam1ModelLoader();

	@Override
	public UnbakedWoodenSupportBeam1Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWoodenSupportBeam1Model();
	}
}

