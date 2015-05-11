package me.Fupery.Artiste.Command;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.Command.Utils.Conditions;
import me.Fupery.Artiste.Command.Utils.Error;
import me.Fupery.Artiste.IO.Artist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbstractCommand {

	protected boolean playerRequired, canvasRequired, adminRequired,
			artistRequired;
	protected boolean successMsg = true;

	protected String usage, error, success;

	protected Artist artist;

	protected int minArgs = 1, maxArgs = 1;

	protected String[] args;
	protected CommandSender sender;
	
	protected Conditions conditions;

	protected AbstractCommand(CommandListener listener) {

		this.sender = listener.getSender();
		this.args = listener.getArgs();
		listener.setCmd(this);

		if (sender instanceof Player)
			artist = StartClass.artistList.get(((Player) sender).getUniqueId());
	}

	public void check() {

		error = evaluate();
		
		conditions.clear();

		if (error == null)

			if (run()) {
				if (successMsg = true)
					success();
				return;
			}

		error();
	}

	protected String evaluate() {

		if (sender instanceof Player) {

			if (artist == null) {

				artist = new Artist(((Player) sender).getUniqueId());

				StartClass.artistList.put(((Player) sender).getUniqueId(),
						artist);

			} else if (artistRequired && artist.isBanned())
				return error = "You have been banned from creating artworks.";
		}

		if (args.length < minArgs || args.length > maxArgs)

			return ChatColor.RED + "/artmap " + usage;

		if (playerRequired && !(sender instanceof Player))

			return Error.noConsole;

		if (adminRequired && !(sender.hasPermission("Artiste.admin")))

			return Error.noPermission;

		if (canvasRequired && StartClass.canvas == null)

			return Error.noDef;

		return null;
	}

	protected boolean run() {

		if (error != null)
			return false;

		return true;
	}

	protected void error() {

		String msg = (error == null) ? usage() : error;

		if (sender instanceof Player)

			sender.sendMessage(ChatColor.RED + msg);

		else

			Bukkit.getLogger().info(msg);
	}

	protected void success() {

		if (success != null) {

			if (sender instanceof Player)

				sender.sendMessage(success);

			else

				Bukkit.getLogger().info(success);
		}
	}

	protected String usage() {

		String msg = (usage == null) ? "help for a full list of commands"
				: usage;

		return "/artmap " + msg;
	}
}
