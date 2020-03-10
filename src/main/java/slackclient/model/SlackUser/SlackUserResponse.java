package slackclient.model.SlackUser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SlackUserResponse {

    private Boolean ok;

    @JsonProperty("members")
    List<SlackUser> slackUsers;

}
