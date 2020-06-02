package lightningtweaks.client;

import java.util.List;

import lightningtweaks.LightningTweaks;
import lightningtweaks.common.LTConfig;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * TODO
 */
@WailaPlugin
public class MetallicityPlugin implements IComponentProvider, IWailaPlugin {
	private static final ResourceLocation config_metallic = new ResourceLocation(LightningTweaks.MODID, "metallic");

	/**
	 * TODO
	 */
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if (config.get(config_metallic))
			tooltip.add(new TranslationTextComponent(
					"config.waila.plugin_" + config_metallic.getNamespace() + '.' + config_metallic.getPath())
							.appendText(": " + LTConfig.isMetallic(accessor.getBlock().asItem())));
	}

	/**
	 * TODO
	 */
	public void register(IRegistrar registrar) {
		registrar.addConfig(config_metallic, false);
		registrar.registerComponentProvider(this, TooltipPosition.BODY, Block.class);
	}
}
