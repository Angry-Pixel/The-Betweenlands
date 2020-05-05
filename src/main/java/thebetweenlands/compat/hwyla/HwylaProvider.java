package thebetweenlands.compat.hwyla;

import com.google.common.base.Strings;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.entity.Entity;
import thebetweenlands.common.entity.draeton.EntityDraetonInteractionPart;
import thebetweenlands.common.lib.ModInfo;

import javax.annotation.Nonnull;
import java.util.List;

public class HwylaProvider implements IWailaEntityProvider {

    @SuppressWarnings("unused")
    public static void callbackRegister(IWailaRegistrar registrar) {
        HwylaProvider provider = new HwylaProvider();
        registrar.registerTailProvider(provider, EntityDraetonInteractionPart.class);
    }

    @Nonnull
    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (!Strings.isNullOrEmpty(FormattingConfig.modNameFormat)) {
            String unknown = String.format(FormattingConfig.modNameFormat, "Unknown");
            currenttip.removeIf(unknown::equals);
            currenttip.add(String.format(FormattingConfig.modNameFormat, ModIdentification.findModContainer(ModInfo.ID).getName()));
        }

        return currenttip;
    }
}
