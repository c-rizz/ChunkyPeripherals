package crizz.chunkyperipherals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import crizz.chunkyperipherals.blocks.ChunkLoaderPeripheralBlock;
import crizz.chunkyperipherals.tools.ChunkyDetectorItem;
import crizz.chunkyperipherals.upgrades.StubUpgrade;
import crizz.chunkyperipherals.upgrades.ChunkyModule.ChunkyModuleItem;
import crizz.chunkyperipherals.upgrades.ChunkyModule.ChunkyUpgrade;
import crizz.chunkyperipherals.upgrades.MinyChunkyModule.MinyChunkyItem;
import crizz.chunkyperipherals.upgrades.MinyChunkyModule.MinyChunkyUpgrade;
import crizz.chunkyperipherals.utils.TicketManager;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.ITurtleAccess;

@Mod(modid=ChunkyPeripherals.MODID, name="Chunky Peripherals", version="1.1.1.0",dependencies="required-after:ComputerCraft")
public class ChunkyPeripherals
{
		public static final String MODID = "chunkyperipherals";
		public static Logger logger;
		public static Configuration config;

		
		
		public static ChunkyModuleItem 				 	chunkyModuleItem;
		public static MinyChunkyItem 			 	minyChunkyModuleItem;
		public static ChunkLoaderPeripheralBlock	chunkLoaderPeripheralBlock;
		public static ChunkyDetectorItem			chunkyDetectorItem;
		
		
		//public static CreativeTabs CCCreativeTab;
		private static CreativeTabs creativeTab = CreativeTabs.tabMisc;
		
		private static boolean 	useAlsoAlternativeRecipes;		
        public  static boolean 	activateInfoLogging;
        
        private static boolean useChunkyModule;
        private static boolean useMinyChunkyModule;
        private static boolean useChunkLoaderPeripheralBlock;
        private static boolean useChunkyDetector;
        
        public static int chunkyModuleUpgradeID;
        public static int minyChunkyModuleUpgradeID;
        
        public static int maxChunkLoadingRadius;
        
        
        
        public static boolean	isServerStopping;
        
        public static int tickCount;
           
        
        // The instance of your mod that Forge uses.
        @Instance(value = "chunkyperipherals")
        public static ChunkyPeripherals instance;
        
        // Says where the client and server 'proxy' code is loaded.
        @SidedProxy(clientSide="crizz.chunkyperipherals.client.ClientProxy", serverSide="crizz.chunkyperipherals.CommonProxy")
        public static CommonProxy proxy;
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event)
        {
        	logger = LogManager.getLogger("ChunkyPeripherals");
        //	logger.setParent(FMLLog.getLogger());

    		logger.info("Loading CRMod...");
    		
        	config = new Configuration(event.getSuggestedConfigurationFile());
        	config.load();
        	
        	
        	useAlsoAlternativeRecipes 		= config.get(Configuration.CATEGORY_GENERAL, "useAlsoAlternativeRecipes", 		false).getBoolean(false);
        	activateInfoLogging 			= config.get(Configuration.CATEGORY_GENERAL, "activateInfoLogging",				false).getBoolean(false);
        	useChunkyModule					= config.get(Configuration.CATEGORY_GENERAL, "useChunkyModule",					true).getBoolean(true);
        	useMinyChunkyModule				= config.get(Configuration.CATEGORY_GENERAL, "useMinyChunkyModule",				true).getBoolean(true);
        	useChunkLoaderPeripheralBlock	= config.get(Configuration.CATEGORY_GENERAL, "useChunkLoaderPeripheralBlock",	true).getBoolean(true);
        	useChunkyDetector				= config.get(Configuration.CATEGORY_GENERAL, "useChunkyDetector",				true).getBoolean(true);
        	
        	
        	
        	if(activateInfoLogging)
        		logger.info("activated info logging");
        	
        	chunkyModuleUpgradeID 			= config.get(Configuration.CATEGORY_GENERAL, "chunkyModuleUpgradeID", 260).getInt();//default Id 260
        	minyChunkyModuleUpgradeID 		= config.get(Configuration.CATEGORY_GENERAL, "minyChunkyModuleUpgradeID", 261).getInt();//default Id 261
        	maxChunkLoadingRadius			= config.get(Configuration.CATEGORY_GENERAL, "maxChunkLoadingRadiusInBlocks", 80).getInt();
        	
        	Side side = FMLCommonHandler.instance().getEffectiveSide();
        	if(side==Side.CLIENT)
        	{
        		//registers the icons for the upgrades
        		MinecraftForge.EVENT_BUS.register(new ChunkyUpgrade());
        		MinecraftForge.EVENT_BUS.register(new MinyChunkyUpgrade());
        	}
        	
        	isServerStopping=false;
       // 	CCCreativeTab = findCCTab();
        	
        	TicketManager.initialize();
        	int roudedMaxChunkLoadingRadius = maxChunkLoadingRadius % 16 == 0? maxChunkLoadingRadius : (maxChunkLoadingRadius + 16  - (maxChunkLoadingRadius%16));
        	ForgeChunkManager.addConfigProperty(instance, "maximumChunksPerTicket", Integer.toString(roudedMaxChunkLoadingRadius*roudedMaxChunkLoadingRadius*4/256), Property.Type.INTEGER);
        }
        
        
        @EventHandler
        public void serverLoad(FMLServerStartingEvent event)
        {
          event.registerServerCommand(new CRModListForgePersistentChunks());
          event.registerServerCommand(new CRModListLoadedChunks());
          event.registerServerCommand(new CRModToggleInfoLogging());
        }
        
        
        @EventHandler 
        public void load(FMLInitializationEvent event)
        {
                proxy.registerRenderers();
                
                               
                if(useChunkyModule)
                {
	                chunkyModuleItem = new ChunkyModuleItem();
	                chunkyModuleItem.loadRecipe();
	                if(useAlsoAlternativeRecipes)
	                	chunkyModuleItem.loadAlternativeRecipe();
	                infoLog("registering chunkyModuleUpgrade");                
	                ComputerCraftAPI.registerTurtleUpgrade(new ChunkyUpgrade());
                }
                
                if(useMinyChunkyModule)
                {
	                minyChunkyModuleItem = new MinyChunkyItem();
	                minyChunkyModuleItem.loadRecipe();
	                infoLog("registering minyChunkyModuleUpgrade"); 
	                ComputerCraftAPI.registerTurtleUpgrade(new MinyChunkyUpgrade());
                }
                
                if(useChunkLoaderPeripheralBlock)
                {
                	chunkLoaderPeripheralBlock = new ChunkLoaderPeripheralBlock();
	                GameRegistry.registerBlock(chunkLoaderPeripheralBlock, ChunkLoaderPeripheralBlock.unlocalizedName);
                	chunkLoaderPeripheralBlock.loadRecipe();
	                if(useAlsoAlternativeRecipes)
	                	chunkLoaderPeripheralBlock.loadAlternativeRecipe();
	                infoLog("registering chunkLoaderPeripheralBlock"); 
                }
                
                if(useChunkyDetector)
                {
                	chunkyDetectorItem = new ChunkyDetectorItem();
                	chunkyDetectorItem.loadRecipe();
	                infoLog("registering chunkyDetectorItem"); 
                }
                
                
                ChunkLoadingCallback.starterTicketsList 				= 	new ConcurrentHashMap<Object, Ticket>(2);//create the map (by defualt capacity is 2 (overworld and nether))
                logger.info("CRMod.instance="+ChunkyPeripherals.instance);
                ForgeChunkManager.setForcedChunkLoadingCallback(ChunkyPeripherals.instance, new ChunkLoadingCallback());
                
                
                config.save();
                config=null;
        }
       
        @EventHandler
        public void postInit(FMLPostInitializationEvent event)
        {
        	logger.info("CRMod loaded.");
        }
        
        
        @EventHandler
        public void atServerStopping(FMLServerStoppingEvent e)
        {
        	isServerStopping=true;
        }
        
        @EventHandler
        public void atServerStopped(FMLServerStoppedEvent e)
        {
        
        }
        
        static public void infoLog(String out)
        {
        	if(activateInfoLogging)
        	{
        		logger.info(out);
        	}
        		
        	
        }
        
        static private  CreativeTabs findCCTab()
        {
			if(FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER)
				return CreativeTabs.tabMisc;
			for (CreativeTabs tab : CreativeTabs.creativeTabArray)
			{
				if (tab.getTabLabel().equals("ComputerCraft"))
					return tab;
			}
			return null;
        }
        
        public static CreativeTabs getCreativeTab()
        {
        	return creativeTab;
        }
}