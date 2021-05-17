package bot.commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import bot.MorseDecoder.MorseDecoder;

public class DecodeCommand implements Command {
    public void process(MessageReceivedEvent event, List<String> args) throws Exception {
        MessageChannel channel = event.getChannel();
        String toConvert = "";

        for (String word : args) {
            toConvert = (toConvert + word );
        }
        //long milli = System.currentTimeMillis() - event.getMessage().getCreationTime().toInstant().toEpochMilli();
        MorseDecoder decode = new MorseDecoder();
        String output = decode.MorseDecoder(toConvert);
        channel.sendMessage(output).queue();
    }
}
