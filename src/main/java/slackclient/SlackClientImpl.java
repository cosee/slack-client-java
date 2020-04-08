package slackclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import slackclient.exception.SlackClientException;
import slackclient.model.ResponseMetadata;
import slackclient.model.SlackChannel.SlackChannel;
import slackclient.model.SlackChannel.SlackChannelResponse;
import slackclient.model.SlackUser.SlackUser;
import slackclient.model.SlackUser.SlackUserResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class SlackClientImpl implements SlackClient {

    private final String slackToken;

    private final String slackUrl;

    private static final int FETCH_LIMIT = 100;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SlackClientImpl(String slackToken, String slackUrl) {
        this.slackToken = slackToken;
        this.slackUrl = slackUrl;
    }

    @Override
    public String postMessageToChannel(String channelId, String message) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("channel", channelId);
        parameters.put("text", message);

        return placeGenericRequest("chat.postMessage", parameters);
    }

    @Override
    public String createSlackChannel(String channelName, boolean isPrivate) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", channelName);

        if (isPrivate)
            parameters.put("is_private", "true");

        return placeGenericRequest("conversations.create", parameters);
    }

    @Override
    public String archiveSlackChannel(String channelId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("channel", channelId);

        return placeGenericRequest("conversations.archive", parameters);
    }

    @Override
    public boolean containsSlackChannelWithName(String channelName) {
        SlackChannelResponse slackChannelResponse = getSlackChannels();
        while (true) {
            if (channelFoundInThisPage(channelName, slackChannelResponse)) return true;
            if (noNextPage(slackChannelResponse.getResponseMetadata())) return false;
            else slackChannelResponse = getNextPageOfSlackChannelResponse(slackChannelResponse);
        }
    }

    private SlackChannelResponse getSlackChannels() {
        return getSlackChannels("");
    }

    private SlackChannelResponse getSlackChannels(String cursor) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("types", "public_channel,private_channel");
        parameters.put("limit", Integer.toString(FETCH_LIMIT));
        if (!cursor.isEmpty())
            parameters.put("cursor", cursor);
        String jsonResponse = placeGenericRequest("conversations.list", parameters);

        return extractResponse(jsonResponse);
    }

    private boolean channelFoundInThisPage(String channelName, SlackChannelResponse slackChannelResponse) {
        for (SlackChannel slackChannel : slackChannelResponse.getChannels()) {
            if (slackChannel.getName().equals(channelName))
                return true;
        }
        return false;
    }

    private boolean noNextPage(ResponseMetadata responseMetadata) {
        return responseMetadata.getNextCursor().isEmpty();
    }

    private SlackChannelResponse getNextPageOfSlackChannelResponse(SlackChannelResponse slackChannelResponse) {
        return getSlackChannels(slackChannelResponse.getResponseMetadata().getNextCursor());
    }

    private SlackChannelResponse extractResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, SlackChannelResponse.class);
        } catch (IOException e) {
            throw new SlackClientException(
                    String.format("Could not parse Json to SlackChannelResponse. Json: %s", jsonResponse));
        }
    }

    @Override
    public Optional<String> getChannelIdByName(String channelName) {
        SlackChannelResponse slackChannelResponse = getSlackChannels();
        while (true) {
            Optional<String> channel = searchForChannelIdInPage(channelName, slackChannelResponse);
            if (channel.isPresent()) return channel;
            if (noNextPage(slackChannelResponse.getResponseMetadata())) return Optional.empty();
            else slackChannelResponse = getNextPageOfSlackChannelResponse(slackChannelResponse);
        }
    }

    private Optional<String> searchForChannelIdInPage(String channelName, SlackChannelResponse slackChannelResponse) {
        for (SlackChannel channel : slackChannelResponse.getChannels()) {
            if (channel.getName().equals(channelName))
                return Optional.of(channel.getId());
        }
        return Optional.empty();
    }

    @Override
    public String setChannelTopic(String channelId, String topic) {
        return setTopic(channelId, topic);
    }

    private String setTopic(String channelId, String topic) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("channel", channelId);
        parameters.put("topic", topic);

        return placeGenericRequest("conversations.setTopic", parameters);
    }

    @Override
    public Optional<String> findUserByEmail(String email) {
        SlackUserResponse slackUserResponse = getSlackUsers();
        while (true) {
            Optional<String> slackUser = searchForUserIdInPage(email, slackUserResponse);
            if (slackUser.isPresent()) return slackUser;
            if (noNextPage(slackUserResponse.getResponseMetadata())) return Optional.empty();
            else slackUserResponse = getNextPageOfSlackUserResponse(slackUserResponse);
        }
    }

    private SlackUserResponse getSlackUsers() {
        return getSlackUsers("");
    }

    private SlackUserResponse getSlackUsers(String cursor) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("limit", Integer.toString(FETCH_LIMIT));
        if (!cursor.isEmpty())
            parameters.put("cursor", cursor);
        String jsonResponse = placeGenericRequest("users.list", parameters);

        return extractSlackUsersFromResponse(jsonResponse);
    }

    private Optional<String> searchForUserIdInPage(String email, SlackUserResponse slackUserResponse) {
        for (SlackUser slackUser : slackUserResponse.getSlackUsers()) {
            if (isSearchedUser(email, slackUser))
                return Optional.of(slackUser.getId());
        }
        return Optional.empty();
    }

    private SlackUserResponse getNextPageOfSlackUserResponse(SlackUserResponse slackUserResponse) {
        return getSlackUsers(slackUserResponse.getResponseMetadata().getNextCursor());
    }

    private SlackUserResponse extractSlackUsersFromResponse(String slackChannelResponse) {
        try {
            return objectMapper.readValue(slackChannelResponse, SlackUserResponse.class);
        } catch (IOException e) {
            throw new SlackClientException(
                    String.format("Could not parse Json to SlackUserResponse. Json: %s", slackChannelResponse));
        }
    }

    private boolean isSearchedUser(String email, SlackUser slackUser) {
        return
                slackUser.getSlackUserProfile().getEmail() != null &&
                        slackUser.getSlackUserProfile().getEmail().equals(email);
    }

    @Override
    public String postMessageToUser(String userId, String message) {
        return postMessageToChannel(userId, message);
    }

    @Override
    public String inviteUserToChannel(String userId, String channelId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("channel", channelId);
        parameters.put("users", userId);

        return placeGenericRequest("conversations.invite", parameters);
    }

    @Override
    public String placeGenericRequest(String command, Map<String, String> requestParameters) {
        String path = buildPath(command, requestParameters);
        String resp = connectToUrl(buildUrl(path));
        checkIfRequestFailed(path, resp);

        return resp;
    }

    private String buildPath(String command, Map<String, String> requestParameters) {
        return requestParameters.keySet().stream()
                .map(key -> String.format("&%s=%s", key, urlEncoder(requestParameters.get(key))))
                .collect(Collectors.joining("",
                        String.format("%s%s?token=%s", slackUrl, command, slackToken), ""));
    }

    private String urlEncoder(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new SlackClientException(
                    String.format("Error encoding url in SlackClient: String that could not be encoded: %s", value));
        }
    }

    private String connectToUrl(URL url) {
        try {
            return placeCommand(url);
        } catch (IOException e) {
            throw new SlackClientException(String.format("IO Exception in SlackClient for url: %s", url));
        }
    }

    private String placeCommand(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        String response = new String(httpURLConnection.getInputStream().readAllBytes());
        httpURLConnection.disconnect();

        return response;
    }

    private URL buildUrl(String path) {
        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            throw new SlackClientException(String.format("Malformed url in SlackClient: %s", path));
        }
    }

    private void checkIfRequestFailed(String request, String response) {
        if (hasFailed(response))
            throw new SlackClientException(
                    String.format(
                            "Slack API responded with \"ok\"=false. Request URL: %s. Slack API's response: %s",
                            request, response));
    }

    private boolean hasFailed(String response) {
        try {
            return ! objectMapper.readTree(response).path("ok").asBoolean();
        } catch (JsonProcessingException e) {
            throw new SlackClientException("Cannot check status of Slack API response (IOException).");
        }
    }
}
