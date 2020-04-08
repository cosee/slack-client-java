package slackclient.model.SlackChannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import slackclient.model.ResponseMetadata;

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
    @JsonProperty("response_metadata")
    private ResponseMetadata responseMetadata;
}
