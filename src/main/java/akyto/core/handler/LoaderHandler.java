package akyto.core.handler;

import akyto.core.Core;
import akyto.core.handler.loader.Inventories;
import akyto.core.handler.loader.Message;
import akyto.core.handler.loader.Permission;
import akyto.core.handler.loader.Settings;
import lombok.Getter;

@Getter
public class LoaderHandler {
	
	private final Message message;
	private final Permission permission;
	private final Settings settings;
	private final Inventories inventory;
	
	public LoaderHandler(final Core main) {
		this.permission = new Permission(main);
		this.message = new Message(main);
		this.settings = new Settings(main);
		this.inventory = new Inventories(main);
	}

}
