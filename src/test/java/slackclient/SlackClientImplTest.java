package slackclient;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.*;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class SlackClientImplTest {

    private SlackClientImpl slackClient;

    private String URL;

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void initTests() {
        URL = String.format("http://localhost:%s/api/", wireMockRule.port());
        initSlackClient();
    }

    private void initSlackClient() {
        slackClient = new SlackClientImpl("token", URL);
    }

    @Test
    public void postMessageToChannel() {
        mockWireMockStubForPostMessage();
        slackClient.postMessageToChannel("1", "text");
        getVerifyForPostMessage();
    }

    private void mockWireMockStubForPostMessage() {
        stubFor(post(urlEqualTo("/api/chat.postMessage?token=token&channel=1&text=text"))
                .willReturn(aResponse()
                        .withBody("{\"ok\":true}")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                )
        );
    }

    private void getVerifyForPostMessage() {
        verify(postRequestedFor(urlEqualTo("/api/chat.postMessage?token=token&channel=1&text=text")));
    }

    @Test
    public void createSlackChannel() {
        stubFor(post(urlEqualTo("/api/conversations.create?token=token&is_private=true&name=channelId"))
                .willReturn(aResponse()
                .withBody("{\"ok\":true}")));
        slackClient.createSlackChannel("channelId", true);
        verify(postRequestedFor(urlEqualTo("/api/conversations.create?token=token&is_private=true&name=channelId")));
    }

    @Test
    public void archiveSlackChannel() {
        stubFor(post(urlEqualTo("/api/conversations.archive?token=token&channel=channelId")).willReturn(aResponse()
                .withBody("{\"ok\":true}")));
        slackClient.archiveSlackChannel("channelId");
        verify(postRequestedFor(urlEqualTo("/api/conversations.archive?token=token&channel=channelId")));
    }

    @Test
    public void containsChannelWithName() {
        mockWireMockStubForGetChannelResponse();
        assertTrue(slackClient.containsSlackChannelWithName("general"));
    }

    @Test
    public void containsChannelWithNamePaginationTest() {
        mockWireMockStubForGetChannelResponse();
        assertTrue(slackClient.containsSlackChannelWithName("pagetestchannel"));
    }

    @Test
    public void handleEmptyCursorCorrectlyIfChannelDoesNotExists() {
        mockWireMockStubForGetChannelResponse();
        assertFalse(slackClient.containsSlackChannelWithName("thischanneldoesnotexist"));
    }

    private void mockWireMockStubForGetChannelResponse() {
        mockWireMockStubForGetChannelWithNoCursor();
        mockWireMockStubForGetChannelWithCursor();
    }

    private void mockWireMockStubForGetChannelWithNoCursor() {
        stubFor(post(urlEqualTo("/api/conversations.list?token=token&types=public_channel%2Cprivate_channel&limit="
                .concat(String.valueOf(100))))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(FileLoader.loadFile("slackChannelResponse.json"))
                )
        );
    }

    private void mockWireMockStubForGetChannelWithCursor() {
        stubFor(post(urlEqualTo(("/api/conversations.list?token=token&cursor=dGVhbTpDMDYxRkE1UEI%3D&" +
                "types=public_channel%2Cprivate_channel&limit=")
                .concat(String.valueOf(100))))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(FileLoader.loadFile("slackChannelResponseNextPage.json"))
                )
        );
    }

    @Test
    public void getChannelIdByName() {
        mockWireMockStubForGetChannelResponse();
        String response = slackClient.getChannelIdByName("general").orElse("failed");
        assertThat(response).isEqualTo("C012AB3CD");
    }

    @Test
    public void getChannelIdByNameWithPagination() {
        mockWireMockStubForGetChannelResponse();
        String response = slackClient.getChannelIdByName("pagetestchannel").orElse("failed");
        assertThat(response).isEqualTo("C012AB3CF");
    }

    @Test
    public void getChannelIdForNotExistingChannelFails() {
        mockWireMockStubForGetChannelResponse();
        assertFalse(slackClient.getChannelIdByName("thischanneldoesnotexist").isPresent());
    }

    @Test
    public void setChannelTopic() {
        stubFor(post(urlEqualTo("/api/conversations.setTopic?token=token&channel=channelId&topic=topic"))
                .willReturn(aResponse()
                .withBody("{\"ok\":true}")));
        slackClient.setChannelTopic("channelId", "topic");
        verify(postRequestedFor(urlEqualTo("/api/conversations.setTopic?token=token&channel=channelId&topic=topic")));
    }

    @Test
    public void postMessageToUser() {
        mockWireMockStubForPostMessage();
        slackClient.postMessageToUser("1", "text");
        getVerifyForPostMessage();
    }

    @Test
    public void inviteUserToChannel() {
        mockStubForInviteUserToChannel();
        slackClient.inviteUserToChannel("W012A3CDE", "C012AB3CD");
        verify(postRequestedFor(urlEqualTo("/api/conversations.invite?token=token&channel=C012AB3CD&users=W012A3CDE")));
    }

    private void mockStubForInviteUserToChannel() {
        stubFor(post(urlEqualTo("/api/conversations.invite?token=token&channel=C012AB3CD&users=W012A3CDE"))
                .willReturn(aResponse()
                        .withBody("{\"ok\":true}")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                )
        );
    }

    @Test
    public void findSlackUserByEmail() {
        mockStubForFindUserByEmail();
        String result = slackClient.findUserByEmail("spengler@ghostbusters.example.com").orElse("failed");
        assertThat(result).isEqualTo("W012A3CDE");
    }

    private void mockStubForFindUserByEmail() {
        stubFor(post(urlEqualTo("/api/users.list?token=token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(FileLoader.loadFile("slackUserResponse.json"))
                )
        );
    }

    @Test
    public void placeGenericRequest() {
        mockWireMockStubForGenericRequest();
        Map<String, String> parameters = prepareParametersForGenericRequest();
        slackClient.placeGenericRequest("chat.postMessage", parameters);
        verifyPlaceGenericRequest();
    }

    private void mockWireMockStubForGenericRequest() {
        stubFor(post(urlEqualTo("/api/chat.postMessage?token=token&channel=channelId&text=42"))
                .willReturn(aResponse()
                        .withBody("{\"ok\":true}")
                        .withStatus(200)));
    }

    private Map<String, String> prepareParametersForGenericRequest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("channel", "channelId");
        parameters.put("text", "42");
        return parameters;
    }

    private void verifyPlaceGenericRequest() {
        verify(postRequestedFor(
                urlEqualTo("/api/chat.postMessage?token=token&channel=channelId&text=42")));
    }
}
