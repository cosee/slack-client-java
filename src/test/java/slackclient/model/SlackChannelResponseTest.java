package slackclient.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import slackclient.FileLoader;
import slackclient.model.SlackChannel.*;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SlackChannelResponseTest {

    SlackChannelResponse slackChannelResponse;

    @Before
    public void initSlackChannelResponse() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = FileLoader.loadFile("slackChannelResponse.json");
        slackChannelResponse = objectMapper.readValue(jsonString, SlackChannelResponse.class);
    }

    @Test
    public void jsonSerializing() {
        SlackChannel exampleChannel = makeExampleChannel();
        assertThat(slackChannelResponse.getChannels()).contains(exampleChannel);
    }

    private SlackChannel makeExampleChannel() {
        SlackChannel general = new SlackChannel();
        general.setId("C012AB3CD");
        general.setName("general");

        return general;
    }

    @Test
    public void nextCursorIsParsed() {
        ResponseMetadata responseMetadata = slackChannelResponse.getResponseMetadata();

        assertThat(responseMetadata.getNextCursor()).isEqualTo("dGVhbTpDMDYxRkE1UEI=");
    }
}
