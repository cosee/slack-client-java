package slackclient.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import slackclient.FileLoader;
import slackclient.model.SlackChannel.SlackChannel;
import slackclient.model.SlackChannel.SlackChannelResponse;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SlackChannelResponseTest {

    @Test
    public void jsonSerializing() throws IOException {
        List<SlackChannel> slackChannels = getSlackChannels();
        SlackChannel exampleChannel = makeExampleChannel();

        assertThat(slackChannels).contains(exampleChannel);
    }

    private List<SlackChannel> getSlackChannels() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = FileLoader.loadFile("slackChannelResponse.json");

        return objectMapper.readValue(jsonString, SlackChannelResponse.class).getChannels();
    }

    private SlackChannel makeExampleChannel() {
        SlackChannel general = new SlackChannel();
        general.setId("C012AB3CD");
        general.setName("general");

        return general;
    }

}
