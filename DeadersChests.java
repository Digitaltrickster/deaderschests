package deaderschests;
/*
 * Released under Creative Commons Attribution, Non-commercial, Share alike license.  No permission is required to include in mod packs.
 */
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.Collections;
import java.util.Arrays;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "DeadersChests", name="DeadersChests", version = "1.0")


public class DeadersChests {
	@Instance("DeadersChests")
	public static DeadersChests instance;
	@Init
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(instance);
	}
	@ForgeSubscribe
	public void onDeath(LivingDeathEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(((EntityPlayer)event.entityLiving).username) != null)
		{
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			InventoryPlayer playerinv = player.inventory;
			WorldServer world = (WorldServer)DimensionManager.getWorld(player.dimension);
			int invsize = playerinv.mainInventory.length - Collections.frequency(Arrays.asList(playerinv.mainInventory), null);
			invsize += playerinv.armorInventory.length - Collections.frequency(Arrays.asList(playerinv.armorInventory), null);
			IInventory deaderschest = null;
			if (playerinv.hasItem(Block.chest.blockID)){
				playerinv.consumeInventoryItem(Block.chest.blockID);
				world.setBlock((int)player.posX, (int)player.posY, (int)player.posZ, Block.chest.blockID);
				TileEntityChest inv1 = (TileEntityChest)world.getBlockTileEntity((int)player.posX, (int)player.posY, (int)player.posZ);
				if (invsize >= 27 && playerinv.hasItem(Block.chest.blockID)) {
					playerinv.consumeInventoryItem(Block.chest.blockID);
					world.setBlock((int)player.posX+1, (int)player.posY, (int)player.posZ, Block.chest.blockID);
					TileEntityChest inv2 = (TileEntityChest)world.getBlockTileEntity((int)player.posX+1, (int)player.posY, (int)player.posZ);
					deaderschest = new InventoryLargeChest("Large Chest", inv1, inv2);
				}
				else {
					deaderschest = inv1;
				}

				int invcounter = 0;
			
				for(int i = 0;i<playerinv.mainInventory.length;++i) {
					if (playerinv.mainInventory[i] != null) {
						deaderschest.setInventorySlotContents(invcounter, playerinv.mainInventory[i]);
						invcounter++;
					}
				}
			
				for(int i = 0;i < playerinv.armorInventory.length;++i) {
					if (playerinv.armorInventory[i] != null) {
						deaderschest.setInventorySlotContents(invcounter, playerinv.armorInventory[i]);
						invcounter++;
					}
				}
				playerinv.clearInventory(-1, -1);
			}
		}
	}
}
