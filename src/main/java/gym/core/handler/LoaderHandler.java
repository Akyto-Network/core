package gym.core.handler;

import gym.core.Core;
import gym.core.handler.loader.Inventories;
import gym.core.handler.loader.Message;
import gym.core.handler.loader.Permission;
import gym.core.handler.loader.Settings;
import lombok.Getter;

@Getter
public class LoaderHandler {
	
	private Message message;
	private Permission permission;
	private Settings settings;
	private Inventories inventory;
	
	public LoaderHandler(final Core main) {
		this.permission = new Permission(main);
		this.message = new Message(main);
		this.settings = new Settings(main);
		this.inventory = new Inventories(main);
	}

}
