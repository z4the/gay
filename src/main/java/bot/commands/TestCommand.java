package bot.commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class TestCommand implements Command {
    public void process(MessageReceivedEvent event, List<String> args) throws Exception {
        MessageChannel channel = event.getChannel();
        //long milli = System.currentTimeMillis() - event.getMessage().getCreationTime().toInstant().toEpochMilli();

        channel.sendMessage("test").queue();
    }
}
