package slackclient.model.SlackUser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SlackUser {

    private String id;
    private String name;

    @JsonProperty("profile")
    private SlackUserProfile slackUserProfile;

}
