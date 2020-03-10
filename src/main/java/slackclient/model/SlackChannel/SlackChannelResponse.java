package slackclient.model.SlackChannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class SlackChannelResponse {
    private Boolean ok;
    private List<SlackChannel> channels;
}
