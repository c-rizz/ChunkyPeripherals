package crizz.chunkyperipherals.tools;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import crizz.chunkyperipherals.ChunkyPeripherals;
import crizz.chunkyperipherals.utils.TicketManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ChunkyDetectorItem extends Item
{
	public static final String unlocalizedName = ChunkyPeripherals.MODID+"_ChunkyDetector";
	
	
	public ChunkyDetectorItem()
	{
		super();
		
		
		setMaxStackSize(1);
		setCreativeTab(ChunkyPeripherals.getCreativeTab());
		setUnlocalizedName(unlocalizedName);
	//	LanguageRegistry.addName(this, "Turtle Chunky Module");
		setTextureName("chunkyperipherals:ChunkyDetector");
		GameRegistry.registerItem(this, unlocalizedName);
	}
	
	public void loadRecipe()
	{
		
		GameRegistry.addRecipe(new ItemStack(ChunkyPeripherals.chunkyModuleItem), new Object[]
	            {
	            	" G ",
	            	"SSS",
	            	"SRS",
	            	'G', new ItemStack(Items.gold_ingot),
	            	'R', new ItemStack(Items.redstone),
	            	'S', Blocks.stone
	            });
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
	{
		par2List.add("Tool to check if a block is kept loaded by ChunkyPeripherals");
	}
	

	
	@Override
    public boolean onItemUse(ItemStack tool, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
	{
		if(!world.isRemote)
		{
			if(TicketManager.checkBlockIsKeptLoaded(x, z))
			{
				player.addChatComponentMessage(new ChatComponentText("ChunkyDetector: loaded"));
			}
			else
			{
				player.addChatComponentMessage(new ChatComponentText("ChunkyDetector: not loaded"));
			}
		}
		return false;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass)
	{
		return itemIcon;		
	}
}
