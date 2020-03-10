package lightningtweaks.server;

import lightningtweaks.common.IProxy;

public class ServerProxy implements IProxy {
    @Override
    public void postInit() {
	// Nothing to be done on a dedicated server.
    }
}