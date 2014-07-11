package eu.gnomino.pistonprotect.Listeners;

import java.util.ArrayList;

import com.sk89q.worldguard.protection.ApplicableRegionSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import eu.gnomino.pistonprotect.PistonProtect;

// Bombero, bombero, yo quiero ser bombero ! Bombero, bombero, Porque es mi voluntad !
// Bombero, bombero, bombero, yo quiero ser bombero ! Que nadie se melta con mi identidad !

public class PistonListener implements Listener {
	private final PistonProtect pl;
	public PistonListener(PistonProtect pl) {
		this.pl = pl;
	}
	private WorldGuardPlugin getWG() {
		return (WorldGuardPlugin) pl.getServer().getPluginManager().getPlugin("WorldGuard");
	}
	@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent event) {
		WorldGuardPlugin wg = getWG();
		if (wg == null) {
			return; 
		}
		RegionManager regionManager = wg.getRegionManager(event.getBlock().getWorld());
		ApplicableRegionSet pistonRegions = regionManager.getApplicableRegions(event.getBlock().getLocation());
		ArrayList<String> pistonRegionsIDs = new ArrayList<String>();
		for (ProtectedRegion r : pistonRegions) {
			pistonRegionsIDs.add(r.getId());
		}
		for (Block b : (event.getBlocks())) {
			ApplicableRegionSet regions = regionManager.getApplicableRegions(b.getLocation());
			if (regions.size() > 0) {
				for (ProtectedRegion region : regions) {
				    if (!pistonRegionsIDs.contains(region.getId())) {
				    	event.setCancelled(true);
				    	if (pl.getConfig().getBoolean("log_fails")) {
				    		pl.getLogger().info("Blocked piston extending at " + event.getBlock().getX() + ";" + event.getBlock().getY() + ";" + event.getBlock().getZ() + " in " + event.getBlock().getWorld().getName() + " ( region " + region.getId() + " )");
				    	}
				    	if(pl.getConfig().getBoolean("break_pistons_extend")) {
				    		event.getBlock().breakNaturally();
				    	}
				    	return;
				    }
				}
			}
		}
	}
	@EventHandler
	public void onPistonRetract(BlockPistonRetractEvent event) {
		if (event.isSticky()) {
			Block pulled = event.getRetractLocation().getBlock();
			if(pulled.getType() == Material.AIR) {
				return;
			}
			Block piston = event.getBlock();
			if (pulled.getPistonMoveReaction() == PistonMoveReaction.MOVE || pulled.getPistonMoveReaction() == PistonMoveReaction.BREAK) {
				WorldGuardPlugin wg = getWG();
				if (wg == null) {
					return; 
				}
				RegionManager regionManager = wg.getRegionManager(piston.getWorld());
				ApplicableRegionSet pistonRegions = regionManager.getApplicableRegions(piston.getLocation());
				ArrayList<String> pistonRegionsIDs = new ArrayList<String>();
				for (ProtectedRegion r : pistonRegions) {
					pistonRegionsIDs.add(r.getId());
				}
				for(ProtectedRegion r : regionManager.getApplicableRegions(pulled.getLocation())) {
					if(!pistonRegionsIDs.contains(r.getId())) {
						event.setCancelled(true);
				    	if (pl.getConfig().getBoolean("log_fails")) {
				    		pl.getLogger().info("Blocked sticky piston retracting at " + event.getBlock().getX() + ";" + event.getBlock().getY() + ";" + event.getBlock().getZ() + " in " + event.getBlock().getWorld().getName() + " ( region " + r.getId() + " )");
				    	}
				    	if(pl.getConfig().getBoolean("break_sticky_retract")) {
				    		piston.breakNaturally();
				    	}
				    	return;
					}
				}
			}
		}
	}
}
