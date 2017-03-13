package com.gmail.s.birchyboy.AngryChickens;

import net.minecraft.server.v1_7_R1.EntityInsentient;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AngryChickens extends JavaPlugin implements Listener{
//////////////////////////////////////
	LivingEntity chicken_roster[] = new LivingEntity[10];
	Entity e_attack;
	Entity e_hurt;
	String pName;
	
	AngryChickens plugin = this;
	
	World world;
	int prob;
	double health;
	
	WELL512 random = new WELL512();
//////////////////////////////////////
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
		world = getServer().getWorld(AngryChickens.this.getConfig().getString("world").trim());
		prob = AngryChickens.this.getConfig().getInt("probability");
		health = AngryChickens.this.getConfig().getInt("health");
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run(){
				if(check()) {
					for(int a=0; a<10; a++) {
						if(check()){
							for(Entity nearby : chicken_roster[a].getNearbyEntities(1.5D, 1.5D, 1.5D)){
								if(nearby.getType().equals(EntityType.PLAYER) || nearby instanceof Player){
									((LivingEntity) nearby).damage(2D);
								}
							}
						}
					}
				}
		    }}, 0, 30L);
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		
			@Override
			public void run(){
				if(e_attack != null && check()){
					Location l = e_attack.getLocation();
					
					for(int z=0; z<10; z++){
						if(chicken_roster[z].getLocation().distance(l) > 10 && check()){
							chicken_roster[z].teleport(l);
						}	
						if(check()){
							((EntityInsentient) ((CraftLivingEntity) chicken_roster[z]).getHandle()).getNavigation().a(l.getX(), l.getY(), l.getZ(), 1.25F);
						}
					}
				}
			}}, 0, 7L);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void ChickenHurt(EntityDamageByEntityEvent event){
		e_hurt = event.getEntity();
		Entity e_attackT = event.getDamager();
		
		for(int a=0; a<10; a++){
			if(e_hurt.equals(chicken_roster[a])){
				e_attack = e_attackT;
				return;
			}else if(!(e_hurt instanceof Chicken)){
				//e_attack = null;
				return;
			}
		}

		if(e_hurt instanceof Chicken && e_attackT instanceof Player){
			e_attack = e_attackT;
			int randomInt = random.nextInt(prob) + 1;
			for(int a=0; a<10; a++){
				if(!check()){
					continue;
				}else{
					chicken_roster[a].remove();
				}
			}
			
			if(randomInt == 1 && world.equals(e_hurt.getWorld())){
				chicken_roster[9]= (LivingEntity) e_hurt;
				LivingEntity temp;
				for(int i = 0; i<9; i++){
					temp = (LivingEntity) e_hurt.getLocation().getWorld().spawnEntity(e_hurt.getLocation().add(0, 2, 0), EntityType.CHICKEN);
					temp.setMaxHealth(health);
					temp.setHealth(health);
					temp.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, 2));
					
					chicken_roster[i] = temp;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void PlayerDeath(EntityDeathEvent event){
		Entity player = event.getEntity();
		if(player.getType().equals(EntityType.PLAYER)){
			if(player.equals(e_attack)){
				for(int a=0; a<10; a++){
					chicken_roster[a].remove();
					chicken_roster[a] = null;
					e_attack = null;
					e_hurt = null;
			    }
			}
		}else if(player.getType().equals(EntityType.CHICKEN)){
			for(int a=0; a<10; a++){
				if(player.equals(chicken_roster[a])){
					event.getDrops().clear();
					chicken_roster[a] = null;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void PlayerQuit(PlayerQuitEvent event){
		Player p = (Player) e_attack;
		if(p.equals(event.getPlayer())){
			pName = event.getPlayer().getDisplayName();
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void PlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if(p.getName().equals(pName))
			e_attack = p;
	}
	
	public boolean check(){
		boolean temp = false;
		for(int i = 0; i<10; i++){
			if(chicken_roster[i] == null)
				temp = false;
			else
				temp = true;
		}
		return temp;
	}
}