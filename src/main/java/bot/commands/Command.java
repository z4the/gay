package bot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface Command {

    public void process(MessageReceivedEvent event, List<String> args) throws Exception;

}

