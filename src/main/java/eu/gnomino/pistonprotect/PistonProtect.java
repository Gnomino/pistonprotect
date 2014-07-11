package eu.gnomino.pistonprotect;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import eu.gnomino.pistonprotect.Listeners.PistonListener;

public class PistonProtect extends JavaPlugin {
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new PistonListener(this), this);
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pistonprotect-reload")) {
			reloadConfig();
			Command.broadcastCommandMessage(sender, "Reloaded PistonProtect's config");
		}
		return false;
	}
}
