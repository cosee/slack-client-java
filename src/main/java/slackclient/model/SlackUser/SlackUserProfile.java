package slackclient.model.SlackUser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SlackUserProfile {

    private String email;

}
