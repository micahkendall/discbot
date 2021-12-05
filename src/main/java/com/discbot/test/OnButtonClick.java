package com.discbot.test;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Flux;

public class OnButtonClick {

    static void onJoinClick(ButtonInteractionEvent event, String channelId){
        GuildChannel channel = event.getInteraction().getGuild().block().getChannelById(Snowflake.of(channelId)).block();
        Snowflake memberId = event.getInteraction().getMember().get().getId();
        PermissionOverwrite allowReadWrite = PermissionOverwrite.forMember(memberId, PermissionSet.of(Permission.SEND_MESSAGES, Permission.VIEW_CHANNEL), PermissionSet.none());
        channel.addMemberOverwrite(memberId, allowReadWrite).block();
        event.reply("Added "+memberId.asString()+" to "+channel.getName()).block();
    }

    public static void subscribe(Flux<ButtonInteractionEvent> flux){
        flux.subscribe(event -> {
            // if event.getCustomId() begins with "join-channel-:" then join the channel
            if (event.getCustomId().startsWith("join-channel:")) {
                String channelId = event.getCustomId().substring("join-channel:".length());
                onJoinClick(event, channelId);
            }else {
                // some other button event
            }
        });
    }
    
}
