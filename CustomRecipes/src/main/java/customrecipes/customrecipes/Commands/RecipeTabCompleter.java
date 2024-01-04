package customrecipes.customrecipes.Commands;

import customrecipes.customrecipes.CustomConfig.RecipeConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RecipeTabCompleter implements TabCompleter {
    List<String> Arg1 = new ArrayList<>();
    List<String> Arg2 = new ArrayList<>();
    List<String> Worlds = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (Arg1.isEmpty()) {
                Arg1.add("add");
                Arg1.add("remove");
                Arg1.add("list");
                Arg1.add("show");
                Arg1.add("toggleShowcase");
            }

            Set<String> worlds = RecipeConfig.get().getKeys(false);
            for (String world : worlds) {
                if (RecipeConfig.get().getConfigurationSection(world) != null) {
                    Arg2.addAll(RecipeConfig.get().getConfigurationSection(world).getKeys(false));
                }
            }

            for (World world : Bukkit.getWorlds()) {
                Worlds.add(world.getName());
            }

            List<String> result = new ArrayList<>();

            switch (args.length) {
                case 1:
                    if (p.isOp()) {
                        for (String s : Arg1)
                            if (s.toLowerCase().startsWith(args[0].toLowerCase()))
                                result.add(s);
                    } else
                        result.add("show");

                    return result;

                case 2:
                    if (args[0].equalsIgnoreCase("show"))
                        for (String s : Arg2) {
                            if (s.toLowerCase().startsWith(args[1].toLowerCase()))
                                result.add(s);
                        }
                    else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")){
                        for (String s : Worlds) {
                            if (s.toLowerCase().startsWith(args[1].toLowerCase()))
                                result.add(s);
                        }
                    }
                    Worlds.clear();
                    Arg2.clear();
                    return result;
                case 3:
                    if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")){
                        for (String s : Arg2) {
                            if (s.toLowerCase().startsWith(args[2].toLowerCase()))
                                result.add(s);
                        }
                    }
                    Arg2.clear();
                    Worlds.clear();
                    return result;
            }
        }
        return null;
    }
}
