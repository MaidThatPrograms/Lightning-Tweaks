package lightningtweaks.common.config;

import lightningtweaks.LightningTweaks;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ConfigChangeHandler {
    @SubscribeEvent
    public static void onConfigChangedEvent(OnConfigChangedEvent event) {
	if (event.getModID().equals(LightningTweaks.MODID))
	    ConfigManager.sync(LightningTweaks.MODID, Type.INSTANCE);
    }
}