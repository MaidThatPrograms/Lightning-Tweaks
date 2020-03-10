package lightningtweaks;

import lightningtweaks.common.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = LightningTweaks.MODID, version = "1.12.2-2.0.0.0", useMetadata = true)
public class LightningTweaks {
    public static final String MODID = "lightningtweaks";
    @SidedProxy(clientSide = MODID + ".client.ClientProxy", serverSide = MODID + ".server.ServerProxy")
    private static IProxy proxy;

    @EventHandler
    public static void postInit(@SuppressWarnings("unused") FMLPostInitializationEvent event) {
	proxy.postInit();
    }
}