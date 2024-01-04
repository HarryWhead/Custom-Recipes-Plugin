package customrecipes.customrecipes.Commands;

import customrecipes.customrecipes.CustomConfig.RecipeConfig;
import customrecipes.customrecipes.CustomRecipes;
import customrecipes.customrecipes.GUI.RecipeGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static customrecipes.customrecipes.CustomRecipes.*;

public class RecipeCommands implements CommandExecutor
{

    Plugin plugin = CustomRecipes.getPlugin(CustomRecipes.class);
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        RecipeConfig.reload();
        plugin.reloadConfig();
        if(sender instanceof Player)
        {
            Player p = (Player) sender;

            if (args.length == 0)
            {
                p.sendMessage(ChatColor.RED + "Incorrect usage, use /CustomRecipe <add:remove:list:show> (world)");
                return true;
            }

                switch (args[0]) {
                    case "add":
                        if(p.isOp()) {
                            if (args.length < 3) {
                                p.sendMessage(ChatColor.RED + "Incorrect usage, use /CustomRecipe add (world) (name)");
                                return true;
                            }

                            if (Bukkit.getWorld(args[1]) == null) {
                                p.sendMessage(ChatColor.RED + "This world does not exist.");
                                return true;
                            }

                            String[] nameArgs = Arrays.copyOfRange(args, 2, args.length);
                            itemName = args[1] + "." + String.join(" ", nameArgs);

                            if (RecipeConfig.get().contains(itemName)) {
                                p.sendMessage(ChatColor.RED + "This item name is already in use!");
                                return true;
                            }

                            p.openInventory(rGUI);
                            return true;
                        }
                    case "remove":
                        if(p.isOp()) {
                            if (args.length >= 3) {

                                if (Bukkit.getWorld(args[1]) == null) {
                                    p.sendMessage(ChatColor.RED + "This world does not exist.");
                                    return true;
                                }

                                String[] nameArgs = Arrays.copyOfRange(args, 2, args.length);
                                String name = String.join(" ", nameArgs);

                                RecipeConfig.get().set(args[1] + "." + name, null);
                                RecipeConfig.save();

                                p.sendMessage(ChatColor.GRAY + name + ChatColor.WHITE + " custom recipe has been removed");
                            } else {
                                p.sendMessage(ChatColor.RED + "Incorrect usage, use /CustomRecipe remove (world) (name)");
                                return true;
                            }
                        }
                        break;

                    case "list":
                        if(p.isOp()) {
                            Set<String> worlds = RecipeConfig.get().getKeys(false);
                            p.sendMessage(ChatColor.GRAY + "Current custom recipes");

                            for (String world : worlds) {
                                if (RecipeConfig.get().getConfigurationSection(world) == null) {
                                    return true;
                                }

                                Set<String> list = RecipeConfig.get().getConfigurationSection(world).getKeys(false);
                                p.sendMessage(ChatColor.GRAY + "" + ChatColor.UNDERLINE + world);
                                for (String key : list)
                                    p.sendMessage(ChatColor.GRAY + "- " + key);
                            }
                        }
                        break;

                    case "show":
                        if (showEnabled || p.isOp()) {
                            String worldName = p.getWorld().getName();
                            String[] nameArgs = Arrays.copyOfRange(args, 1, args.length);
                            String name = String.join(" ", nameArgs);

                            if (RecipeConfig.get().contains(worldName + "." + name))
                                new RecipeGUI().createShowcaseGui(worldName + "." + name, p);
                            else {
                                p.sendMessage(ChatColor.RED + "This is not a valid recipe, use /CustomRecipe list to see valid recipes!");
                                return true;
                            }
                        }
                        break;
                    case "toggleShowcase":
                    {
                        if (p.isOp()) {
                            if (showEnabled) {
                                p.sendMessage(ChatColor.RED + "Public showcase is now disabled");
                                showEnabled = false;
                            } else {
                                p.sendMessage(ChatColor.GREEN + "Public showcase is now enabled");
                                showEnabled = true;
                            }
                        }
                    }
                }

            }
        RecipeConfig.save();
        plugin.saveConfig();
        return true;
    }
}
