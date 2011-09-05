package com.FriedTaco.taco.MorePhysics;


	import java.io.File;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.util.logging.Level;
	import java.util.logging.Logger;
	import java.util.ArrayList;
import java.util.Arrays;
	import java.util.HashMap;
import java.util.List;
	import java.util.Properties;
	import org.bukkit.entity.Boat;
	import org.bukkit.entity.Player;
	import org.bukkit.event.Event.Priority;
	import org.bukkit.event.Event;
	import org.bukkit.event.player.PlayerLoginEvent;
	import org.bukkit.inventory.ItemStack;
	import org.bukkit.plugin.PluginDescriptionFile;
	import org.bukkit.plugin.java.JavaPlugin;
	import org.bukkit.plugin.PluginManager;
	import com.nijiko.permissions.PermissionHandler;
	import com.nijikokun.bukkit.Permissions.Permissions;
	import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;
	import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;




	public class MorePhysics extends JavaPlugin {
		public static final Logger log = Logger.getLogger("Minecraft");
		private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();  
	    private final MorePhysicsPlayerListener playerListener  = new MorePhysicsPlayerListener(this);
	    private final MorePhysicsVehicleListener VehicleListener = new MorePhysicsVehicleListener(this);
	    private final MorePhysicsBlockListener BlockListener = new MorePhysicsBlockListener(this);
	    private final MorePhysicsEntityListener entityListener = new MorePhysicsEntityListener(this);
		public static ArrayList<Boat> sinking = new ArrayList<Boat>();
		public List<String> bouncyBlocks = new ArrayList<String>();
		@SuppressWarnings("unused")
		private static Yaml yaml = new Yaml(new SafeConstructor());
		public static PermissionHandler Permissions;
		public boolean movement=true,swimming=true,boats=true,pistons=true,exemptions=true,pistonsB=true;		
		public double lhat,lshirt,lpants,lboots,ihat,ishirt,ipants,iboots,ghat,gshirt,gpants,gboots,dhat,dshirt,dpants,dboots,chat,cshirt,cpants,cboots;
		static String mainDirectory = "plugins/MorePhysics";
		static File config = new File(mainDirectory + File.separator + "config.dat");
		static Properties properties = new Properties(); 
		protected static Configuration Config;


		   
		 private void setupPermissions() {
		      Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		      if (MorePhysics.Permissions == null) 
		      {
		          if (test != null) {
		              MorePhysics.Permissions = ((Permissions)test).getHandler();
		              System.out.println("[MorePhysics] Permissions detected. Now using permissions.");
		          } else {
		             System.out.println("[MorePhysics] Permissions NOT detected. Giving permission to ops.");
		          }
		      }
		  }
		 public void loadConfig(){
	            Config = getConfiguration();
            	Config.setProperty("Boats_Sink", true);
            	Config.setProperty("Movement_Affected", true);
            	Config.setProperty("Swimming_Affected", true);
            	Config.setProperty("Pistons_Launch_Entities", true);
            	Config.setProperty("Pistons_Launch_Blocks", true);
            	Config.setProperty("Allow_Physics_Exemptions", true);
            	Config.setProperty("Bounce_Causing_Blocks", "1 2 12 35");
        	    Config.setProperty("Leather_Helm",2);
                Config.setProperty("Leather_Chest",10);
                Config.setProperty("Leather_Pants",8);
                Config.setProperty("Leather_Boots",2);
                Config.setProperty("Iron_Helm",20);
                Config.setProperty("Iron_Chest",60);
                Config.setProperty("Iron_Pants",40);
                Config.setProperty("Iron_Boots",20);
                Config.setProperty("Gold_Helm",40);
                Config.setProperty("Gold_Chest",80);
                Config.setProperty("Gold_Pants",70);
                Config.setProperty("Gold_Boots",40);
                Config.setProperty("Diamond_Helm",5);
                Config.setProperty("Diamond_Chest",30);
                Config.setProperty("Diamond_Pants",20);
                Config.setProperty("Diamond_Boots",5);
                Config.setProperty("Chain_Helm",10);
                Config.setProperty("Chain_Chest",50);
                Config.setProperty("Chain_Pants",30);
                Config.setProperty("Chain_Boots",10);
	            Config.setHeader("#MorePhysics configuration");
	            boats = Config.getBoolean("Boats_Sink", true);
	            swimming = Config.getBoolean("Movement_Affected", true);
	            movement = Config.getBoolean("Swimming_Affected", true);
	            lhat = Config.getDouble("Leather_Helm",2)/1000;
	            lshirt = Config.getDouble("Leather_Chest",10)/1000;
	            lpants = Config.getDouble("Leather_Pants",8)/1000;
	            lboots = Config.getDouble("Leather_Boots",2)/1000;
	            ihat = Config.getDouble("Iron_Helm",20)/1000;
	            ishirt = Config.getDouble("Iron_Chest",60)/1000;
	            ipants = Config.getDouble("Iron_Pants",40)/1000;
	            iboots = Config.getDouble("Iron_Boots",20)/1000;
	          	ghat = Config.getDouble("Gold_Helm",40)/1000;
	          	gshirt = Config.getDouble("Gold_Chest",80)/1000;
	          	gpants = Config.getDouble("Gold_Pants",70)/1000;
	          	gboots = Config.getDouble("Gold_Boots",40)/1000;
	          	dhat = Config.getDouble("Diamond_Helm",5)/1000;
	          	dshirt = Config.getDouble("Diamond_Chest",30)/1000;
	          	dpants = Config.getDouble("Diamond_Pants",20)/1000;
	          	dboots = Config.getDouble("Diamond_Boots",5)/1000;
	          	chat = Config.getDouble("Chain_Helm",10)/1000;
	          	cshirt = Config.getDouble("Chain_Chest",50)/1000;
	          	cpants = Config.getDouble("Chain_Pants",30)/1000;
	          	cboots = Config.getDouble("Chain_Boots",10)/1000;
	          	pistons = Config.getBoolean("Pistons_Launch_Entities", true);
	          	pistonsB = Config.getBoolean("Pistons_Launch_Blocks", true);
	          	exemptions = Config.getBoolean("Allow_Physics_Exemptions", true);
	          	bouncyBlocks = Arrays.asList(Config.getString("Bounce_Causing_Blocks", "").split(" "));
	            Config.save();
	        }

		 public void loadSettings() throws Exception
		 {
			 if (!this.getDataFolder().exists())
			 {
				 	this.getDataFolder().mkdirs();
			 }
			 final String dir = "plugins/MorePhysics";
		        if (!new File(dir + File.separator + "MorePhysics.properties").exists()) {
		            FileWriter writer = null;
		            try {
		                writer = new FileWriter(dir + File.separator + "MorePhysics.properties");
		                writer.write("MorePhysics v 1.4 configuration\r\n");
		                writer.write("#Allow boats to sink.\r\n");
		                writer.write("BoatsSink=true \r\n");
		                writer.write("#Allow armour to affect movement on land.\r\n");
		                writer.write("MovementAffected=true\r\n");
		                writer.write("#Allow armour to affect movement in water.\r\n");
		                writer.write("SwimmingAffected=true\r\n");
		                writer.write("#Allow pistons to launch players and other entities. (Mobs, dropped items, arrows, etc.)\r\n");
		                writer.write("PistonLaunch=true\r\n");
		                writer.write("#Allow pistons to launch blocks.\r\n");
		                writer.write("PistonLaunchBlocks=true\r\n");
		                writer.write("#Allow people to be exempt from physics (Requires permissions node)\r\n");
		                writer.write("AllowExemptions=true\r\n");
		                writer.write("#Blocks that are bouncy, must be separated by spaces. Leave this empty to exempt this feature.\r\n");
		                writer.write("BouncyBlocks=1 2 12 35 \r\n");
		                writer.write("#The following are the weights of armour.\r\n");
		                writer.write("#These are values out of 100 and are predefined by default.\r\n");
		                writer.write("#Tampering with these values may result in players becoming conscious of their weight.\r\n");
		                writer.write("Leather_Helm=2\r\n");
		                writer.write("Leather_Chest=10\r\n");
		                writer.write("Leather_Pants=8\r\n");
		                writer.write("Leather_Boots=2\r\n");
		                writer.write("Iron_Helm=20\r\n");
		                writer.write("Iron_Chest=60\r\n");
		                writer.write("Iron_Pants=40\r\n");
		                writer.write("Iron_Boots=20\r\n");
		                writer.write("Gold_Helm=40\r\n");
		                writer.write("Gold_Chest=80\r\n");
		                writer.write("Gold_Pants=70\r\n");
		                writer.write("Gold_Boots=40\r\n");
		                writer.write("Diamond_Helm=5\r\n");
		                writer.write("Diamond_Chest=30\r\n");
		                writer.write("Diamond_Pants=20\r\n");
		                writer.write("Diamond_Boots=5\r\n");
		                writer.write("Chain_Helm=10\r\n");
		                writer.write("Chain_Chest=50\r\n");
		                writer.write("Chain_Pants=30\r\n");
		                writer.write("Chain_Boots=10\r\n");
		                
		                } catch (Exception e) {
		                log.log(Level.SEVERE,
		                        "Exception while creating MorePhysics.properties", e);
		                try {
		                    if (writer != null)
		                        writer.close();
		                } catch (IOException ex) {
		                    log
		                            .log(
		                                    Level.SEVERE,
		                                    "Exception while closing writer for MorePhysics.properties",
		                                    ex);
		                }
		            } finally {
		                try {
		                    if (writer != null)
		                        writer.close();
		                } catch (IOException e) {
		                    log
		                            .log(
		                                    Level.SEVERE,
		                                    "Exception while closing writer for MorePhysics.properties",
		                                    e);
		                }
		            }
		        }
		        PropertiesFile properties = new PropertiesFile(dir + File.separator + "MorePhysics.properties");
		        try {
		          boats = properties.getBoolean("BoatsSink", true);
		          swimming = properties.getBoolean("MovementAffected", true);
		          movement = properties.getBoolean("SwimmingAffected", true);
		          lhat = properties.getDouble("Leather_Helm",2)/1000;
		          lshirt = properties.getDouble("Leather_Chest",10)/1000;
		          lpants = properties.getDouble("Leather_Pants",8)/1000;
		          lboots = properties.getDouble("Leather_Boots",2)/1000;
		          ihat = properties.getDouble("Iron_Helm",20)/1000;
		          ishirt = properties.getDouble("Iron_Chest",60)/1000;
		          ipants = properties.getDouble("Iron_Pants",40)/1000;
		          iboots = properties.getDouble("Iron_Boots",20)/1000;
		          ghat = properties.getDouble("Gold_Helm",40)/1000;
		          gshirt = properties.getDouble("Gold_Chest",80)/1000;
		          gpants = properties.getDouble("Gold_Pants",70)/1000;
		          gboots = properties.getDouble("Gold_Boots",40)/1000;
		          dhat = properties.getDouble("Diamond_Helm",5)/1000;
		          dshirt = properties.getDouble("Diamond_Chest",30)/1000;
		          dpants = properties.getDouble("Diamond_Pants",20)/1000;
		          dboots = properties.getDouble("Diamond_Boots",5)/1000;
		          chat = properties.getDouble("Chain_Helm",10)/1000;
		          cshirt = properties.getDouble("Chain_Chest",50)/1000;
		          cpants = properties.getDouble("Chain_Pants",30)/1000;
		          cboots = properties.getDouble("Chain_Boots",10)/1000;
		          pistons = properties.getBoolean("PistonLaunch", true);
		          pistonsB = properties.getBoolean("PistonLaunchBlocks", true);
		          exemptions = properties.getBoolean("AllowExemptions", true);
		          bouncyBlocks = Arrays.asList(properties.getString("BouncyBlocks","").split(" "));
		        } catch (Exception e) {
		            log.log(Level.SEVERE,
		                    "Exception while reading from MorePhysics.properties", e);
		        }

		 }
	    public void onDisable() {
	    }
	    @Override
	    public void onEnable() {
	    	try {
				loadSettings();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loadConfig();
	        PluginManager pm = getServer().getPluginManager();
	        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
	        pm.registerEvent(Event.Type.VEHICLE_DAMAGE, VehicleListener, Priority.Normal, this);
	        pm.registerEvent(Event.Type.VEHICLE_DESTROY, VehicleListener, Priority.Normal, this);
	        pm.registerEvent(Event.Type.VEHICLE_MOVE, VehicleListener, Priority.Normal, this);
	        pm.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, BlockListener, Priority.Normal, this);
	        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
	        PluginDescriptionFile pdfFile = this.getDescription();
	        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	        setupPermissions();
	    }

	    public boolean isDebugging(final Player player) {
	        if (debugees.containsKey(player)) {
	            return debugees.get(player);
	        } else {
	            return false;
	        }
	    }

	    public void setDebugging(final Player player, final boolean value) {
	        debugees.put(player, value);
	    }

		public void recordEvent(PlayerLoginEvent event) {
			// TODO Auto-generated method stub
			
		}
		double weight(int id)
	    {
	    	switch(id)
	    	{
	    		case 298:
	    			return this.lhat;	
	    		case 299:
	    			return this.lshirt;	
	    		case 300:
	    			return this.lpants;	
	    		case 301:
	    			return this.lboots;	
	    		case 302:
	    			return this.chat;
	    		case 303:
	    			return this.cshirt;
	    		case 304:
	    			return this.cpants;
	    		case 305:
	    			return this.cboots;
	    		case 306:
	    			return this.ihat;
	    		case 307:
	    			return this.ishirt;
	    		case 308:
	    			return this.ipants;
	    		case 309:
	    			return this.iboots;
	    		case 310:
	    			return this.dhat;
	    		case 311:
	    			return this.dshirt;
	    		case 312:
	    			return this.dpants;
	    		case 313:
	    			return this.dboots;
	    		case 314:
	    			return this.ghat;
	    		case 315:
	    			return this.gshirt;
	    		case 316:
	    			return this.gpants;
	    		case 317:
	    			return this.gboots;
	    		default:
	    			return 0;
	    	}
	    }
		
		double getTotalWeight(Player player)
		{
			double modifier = 0;
			for(ItemStack i : player.getInventory().getArmorContents())
			{
				if(i != null)
					modifier += weight(i.getTypeId());
			}
			return modifier;
		}
	}




