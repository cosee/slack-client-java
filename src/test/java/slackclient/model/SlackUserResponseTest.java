package slackclient.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import slackclient.FileLoader;
import slackclient.model.SlackUser.*;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SlackUserResponseTest {

    @Test
    public void jsonSerializing() throws IOException {
        List<SlackUser> slackUsers = getSlackUsers();
        SlackUser exampleUser = makeExampleUser();

        assertThat(slackUsers).contains(exampleUser);
    }

    private List<SlackUser> getSlackUsers() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = FileLoader.loadFile("slackUserResponse.json");

        return objectMapper.readValue(jsonString, SlackUserResponse.class).getSlackUsers();
    }

    private SlackUser makeExampleUser() {
        SlackUser spengler = new SlackUser();
        spengler.setId("W012A3CDE");
        spengler.setName("spengler");
        spengler.setSlackUserProfile(new SlackUserProfile());
        spengler.getSlackUserProfile().setEmail("spengler@ghostbusters.example.com");

        return spengler;
    }

}
