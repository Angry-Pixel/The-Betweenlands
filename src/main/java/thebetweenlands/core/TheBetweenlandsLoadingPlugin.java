package thebetweenlands.core;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions({ "thebetweenlands.core." })
@MCVersion("1.12.2")
public class TheBetweenlandsLoadingPlugin implements IFMLLoadingPlugin {
	@Override
	public String[] getASMTransformerClass() {
		boolean useGLDebug = "true".equals(System.getProperty("bl.glDebug"));
		if(useGLDebug) {
			return new String[] { "thebetweenlands.core.TheBetweenlandsClassTransformer", "thebetweenlands.core.OpenGLDebug" };
		}
		return new String[] { "thebetweenlands.core.TheBetweenlandsClassTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
