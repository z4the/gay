package bot;

import bot.commands.Command;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.api.entities.Game;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static bot.Config.*;
//import static bot.SecurityUtils.*;


public class Main {
    public static JDA jda;
    private static ThreadPoolExecutor threadPool = null;

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public static void setupThreadPool(int threadCount) {
        // If we already have a thread pool, we'll need to shut it down.
        if (threadPool != null) {
            // If we have a thread pool with this many threads already, we don't have to create a new one
            if (threadPool.getActiveCount() == threadCount) {
                return;
            }

            threadPool.shutdown();
        }

        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
    }

    public static void main(String...args) throws FileNotFoundException {
        Config.loadConfig();
        final boolean debug = Config.isDebugMode();
        new CommandUtils();
        new CommandManager(debug);

        try {
            //jda = new JDABuilder(AccountType.BOT)
             //       .setToken(Config.getBotToken())
              //      .addEventListener(new MessageListener())
               //     .buildBlocking();
            //jda = JDABuilder.createDefault(Config.getBotToken()).build();
            //jda.getPresence().setGame(Game.streaming("on " + System.getProperty("os.name"), "https://twitch.tv/loonatricks"));
            JDA jda = JDABuilder.createDefault(Config.getBotToken()).addEventListeners(new MessageListener()).build();
            //jda.addEventListener(new MessageListener());

            System.out.println("load");
        } catch (LoginException e) {
            System.out.println("fail");
            e.printStackTrace();
        }
        System.out.println("Connected");
        //jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
    }
}

class MessageListener extends ListenerAdapter {

    /* These keep track of how many bot.commands a user has running currently */
    private Map<String, Integer> userToCommandCount = new ConcurrentHashMap<String, Integer>();

    public int getCommandCount(User user) {
        return userToCommandCount.getOrDefault(user.getId(), 0);
    }

    public void setCommandCount(User user, int count) {
        userToCommandCount.put(user.getId(), count);
    }

    public void addCommandCount(User user, int count) {
        setCommandCount(user,getCommandCount(user) + count);
    }

    public void removeCommandCount(User user, int count) {
        setCommandCount(user, Math.max(0, getCommandCount(user) - count));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        Guild guild = event.getGuild();
        TextChannel  channel = event.getTextChannel();
        List<Role> role = guild.getRoles();
        //System.out.println("roles : " + Arrays.asList(role));
        //PrivateChannel privateChannel = event.getPrivateChannel();



        if (author.isBot() || !event.isFromType(ChannelType.TEXT)) {
            return;
        }

        String msg = event.getMessage().getContentRaw();
        System.out.println(msg);


        if (!msg.startsWith(Config.getPrefix())) {
            System.out.println("not prefix");
            return;
        }

        msg = msg.substring(Config.getPrefix().length());
        List<String> args = new ArrayList<String>(Arrays.asList(msg.split(" ")));
        Command command = CommandManager.getCommand(args.get(0));

        if (command == null) {
            return;
        }

        args.remove(0);

        // Let's see if this user can start a new command right now...
        if (getCommandCount(author) >= Config.getMaxConcurrentCommands()) {
            String plural = Config.getMaxConcurrentCommands() == 1 ? "" : "s";
            event.getChannel().sendMessage("You are already running a maximum of " + Config.getMaxConcurrentCommands() + " command" + plural + "! Please slow down a bit :(").queue();
            return;
        }

        // Add a new command currently running to the user
        addCommandCount(author, 1);

        Main.getThreadPool().execute(() -> {
            try {
                command.process(event, args);
            } catch (Exception e) {
                e.printStackTrace();
                event.getChannel().sendMessage(e.getMessage());
            }

            // This command has finished, we can stop keeping track of it
            removeCommandCount(author, 1);
        });
/*
            else if (msg.contains("test")) {
            File f = new File("path/to/file.jpg");
            try {
                jda.getSelfUser().getManager().setAvatar(Icon.from(f));
            } catch (IOException e) {
                e.printStackTrace();
            }

            */
    }
}
