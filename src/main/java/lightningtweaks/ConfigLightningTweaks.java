package lightningtweaks;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = LightningTweaks.MODID)
public class ConfigLightningTweaks {
    @Mod.EventBusSubscriber
    public static class EventHandler {
	@SubscribeEvent
	public static void onConfigChangedEvent(OnConfigChangedEvent event) {
	    if (event.getModID().equals(LightningTweaks.MODID))
		ConfigManager.sync(LightningTweaks.MODID, Type.INSTANCE);
	}
    }
}