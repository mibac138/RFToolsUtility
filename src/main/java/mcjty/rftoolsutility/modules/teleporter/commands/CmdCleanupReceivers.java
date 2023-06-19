package mcjty.rftoolsutility.modules.teleporter.commands;

import mcjty.rftoolsbase.commands.AbstractRfToolsCommand;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.world.entity.player.Player;

public class CmdCleanupReceivers extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getCommand() {
        return "cleanup";
    }

    @Override
    public int getPermissionLevel() {
        return 1;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public void execute(Player sender, String[] args) {
        TeleportDestinations destinations = TeleportDestinations.get(sender.level());
        destinations.cleanupInvalid();
    }
}
