package com.FriedTaco.taco.MorePhysics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;


public class MorePhysicsVehicleListener implements Listener
{
	private final MorePhysics plugin;
	
    public MorePhysicsVehicleListener(MorePhysics instance) {
        plugin = instance;
    }
    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent	event)
    {
    	if(plugin.boats && event.getVehicle() instanceof Boat)
    	{
    		Boat b = (Boat)event.getVehicle();
	    	if(!MorePhysics.sinking.contains(b) && !b.isDead() && (event.getDamage() >= 2))
	    	{
	    		MorePhysics.sinking.add(b);
	    		b.setVelocity(b.getVelocity().subtract(new Vector(0,.05,0)));
	    	}
    	}
    }
    @EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent event) 	
	{
		if(event.getVehicle() instanceof Boat && MorePhysics.sinking.contains((Boat) event.getVehicle()))
    		MorePhysics.sinking.remove((Boat) event.getVehicle());		
	}
    @EventHandler
	public void onVehicleMove(VehicleMoveEvent event)
	{
    	Vehicle v = event.getVehicle();
		if(plugin.boats)
		{
			if(v instanceof Boat && MorePhysics.sinking.contains((Boat) v))
			{
				Block on = v.getWorld().getBlockAt(event.getTo());
				Block under = on.getRelative(0, -1, 0);
				Material underType = under.getType();
				if(underType == Material.WATER || underType == Material.STATIONARY_WATER)
				{
					Vector vec  = event.getVehicle().getVelocity();
					vec.subtract(new Vector(0,.05,0));
					event.getVehicle().setVelocity(vec);
				}
			}
		}
		if(plugin.minecarts && v instanceof Minecart)
		{
			if(v.getVelocity().length() > .1)
			{
				for(Entity ent : v.getNearbyEntities(.4, .75, .4))
				{
					if(ent instanceof LivingEntity)
					{
						int dmg = (int) (event.getVehicle().getVelocity().length() * (10*plugin.cartDamage));
						LivingEntity le = (LivingEntity) ent;
			    		EntityDamageEvent damage = new EntityDamageEvent(le,DamageCause.ENTITY_ATTACK, dmg);
			    		Bukkit.getPluginManager().callEvent(damage);
			    		if(!damage.isCancelled())
			    		{
							Location to = event.getTo();
							Location from = event.getFrom();
							Location loc = ent.getLocation();
							if(loc.distance(to)<loc.distance(from))
							{
								if(v.getPassenger() != le)
								{
									if(le instanceof Player)
						    		{
						    			Player p = (Player) le;
						    			if(dmg >= p.getHealth())
						    				plugin.hitPlayers.add(p.getName());
						    		}
					    			if(le instanceof Animals && plugin.animalcart)
					    			{
					    				le.damage(dmg);
					    			} else if(le instanceof Monster && plugin.monstercart) {
					    				le.damage(dmg);
					    			} else if(le instanceof Player && plugin.playercart) {
					    				le.damage(dmg);
					    			}
								}
							}
						}
					}
				}
			}
		}
	}
    /*
    @EventHandler
    public void onVehilceEntityCollide(VehicleEntityCollisionEvent event)
    {
    	if(plugin.minecarts && event.getVehicle() instanceof Minecart)
    	{
	    	Entity e = event.getEntity();
	    	if(e instanceof LivingEntity)
	    	{
	    		Vehicle v = event.getVehicle();
	    		LivingEntity le = (LivingEntity) e;
	    		boolean front = isInFrontOf(le,v);
	    		if(front)
	    		{
		    		int dmg = (int) (event.getVehicle().getVelocity().length() * 20);
		    		EntityDamageEvent damage = new EntityDamageEvent(le,DamageCause.ENTITY_ATTACK, dmg);
		    		Bukkit.getPluginManager().callEvent(damage);
		    		if(!damage.isCancelled())
		    		{
			    		if(e instanceof Player)
			    		{
			    			Player p = (Player) e;
			    			if(dmg >= p.getHealth())
			    				plugin.hitPlayers.add(p.getName());
			    		}
			    		if(dmg>2)
			    		{
			    			if(e instanceof Animals && plugin.animalcart)
			    			{
			    				le.damage(dmg);
			    			} else if(e instanceof Monster && plugin.monstercart) {
			    				le.damage(dmg);
			    			} else if(e instanceof Player && plugin.playercart) {
			    				le.damage(dmg);
			    			}
			  
			    		}
		    		}
	    		}
	    	}
    	}
    }
    boolean isInFrontOf(LivingEntity l, Vehicle v) {
    	double dot = v.getVelocity().normalize().dot(l.getLocation().toVector().subtract(v.getLocation().subtract(0,.5,0).toVector()).normalize());
    	return Math.abs(dot) >= Math.cos(Math.PI/4);
    	
    }
    */
}