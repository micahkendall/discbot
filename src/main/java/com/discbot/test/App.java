package com.discbot.test;

import java.util.Scanner;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

/**
 * Discord Test Bot
 *
 */

 // launch "java -jar bot.jar $BOT_TOKEN"

/*
Design notes

-> Discord buttons should be stateless. This can be done using button IDs!
    (i.e the required information for reacting to a button is stored in the button itself)
-> Bot should check for existing channels in the desired format, then cache for the session.
    Command "!refreshcache" // Only for manual refreshing
    Format Example
        Category M12 LAB
            #general
            #group-i for i in 1-20
            Group i VC/Screen for i in 1-20
-> Bot should be able to create channels/categories in the desired format.
    Command "!createchannels"
    Format Example
        Category M12 LAB
            #general
            #group-i for i in 1-20
            Group i VC/Screen for i in 1-20
-> Bot should be able to create buttons for joining channels.
    Command "!freejoin (on/off)"
        -> Can be run in #general of a category
        -> Creates/removes buttons for joining groups 1-20 automatically
        -> No approval required
-> Bot should be able to create buttons for requesting to join channels.
    Command "!requestjoin (on/off)"
        -> Similar to !freejoin
        -> Approval required

// Maybe request should should allow a list of users to be mentioned, so that
// those users are automatically pinged for approval requests
// !requestjoin (on/off) (...users) (request-channel)
// i.e usage might be "!requestjoin on @Peter#0000 @micah#0000 #join-requests"

Actions through buttons:
    Ids will be encoded "action:args"
    -> Join channel button
        Currently, for joining channels, post a button with id "join-channel:channelId"
        The command "!test" currently joins a channel in my test server.
*/

public class App 
{
    public static void main( String[] args )
    {
        // in production this would use args, but for testing it's easier to use scanner
        String secret_token;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter bot token: ");
        secret_token = sc.nextLine();
        sc.close();

        // init client, gateway
        final DiscordClient client = DiscordClient.create(secret_token);
        final GatewayDiscordClient gateway = client.login().block();

        // message handler -todo: move to separate class
        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            if ("!test".equals(message.getContent())) {
                Button button = Button.primary("join-channel:916888437754306570", "#group-1");
                ActionRow actionRow = ActionRow.of(button);
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("Pong!").withComponents(actionRow).block();
            }
        });

        // button handler
        OnButtonClick.subscribe(gateway.on(ButtonInteractionEvent.class));

        gateway.onDisconnect().block();
    }
}
