package slackclient.model.SlackChannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ResponseMetadata {
    @JsonProperty("next_cursor")
    private String nextCursor;
}
