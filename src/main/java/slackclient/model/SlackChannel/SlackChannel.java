package slackclient.model.SlackChannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class SlackChannel {
    private String id;
    private String name;
    @JsonProperty("is_archived")
    private boolean isArchived;
}