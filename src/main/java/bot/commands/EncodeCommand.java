package bot.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import bot.MorseEncoder.MorseEncoder;

public class EncodeCommand implements Command {
    public void process(MessageReceivedEvent event, List<String> args) throws Exception {
        TextChannel channel = event.getTextChannel();
        String msg = event.getMessage().getContentRaw(); // get input message
        System.out.println("msg = " + msg);
        System.out.println("args = " + args);

        String toConvert = "";

        for (String word : args) {
            toConvert = (toConvert + word );
        }

        toConvert = toConvert.trim();
        System.out.println(toConvert);

        MorseEncoder encode = new MorseEncoder();
        ArrayList<String> output = new ArrayList<String>(encode.englishToMorse(toConvert));

        channel.sendMessage("``"+output.toString()+"``").queue();
    }
}
