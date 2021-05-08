package bot.commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

import static bot.CommandUtils.wGet;

public class WGetCommand implements Command {
    public void process(MessageReceivedEvent event, List<String> args) {
        MessageChannel channel = event.getChannel();
        wGet(args);
        channel.sendMessage("Downloaded: " + args).queue();

    }
}