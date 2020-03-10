package slackclient;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("UnusedReturnValue")
public interface SlackClient {

    String postMessageToChannel(String channelId, String message);

    String createSlackChannel(String channelName, boolean isPrivate);

    String archiveSlackChannel(String channelId);

    boolean containsSlackChannelWithName(String channelName);

    Optional<String> getChannelIdByName(String channelId);

    String setChannelTopic(String channelId, String topic);

    String postMessageToUser(String userId, String message);

    Optional<String> findUserByEmail(String email);

    String inviteUserToChannel(String userId, String channelId);

    String placeGenericRequest(String command, Map<String, String> requestParameters);

}
