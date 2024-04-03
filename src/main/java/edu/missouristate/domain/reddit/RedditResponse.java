package edu.missouristate.domain.reddit;

import com.fasterxml.jackson.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "kind",
        "data"
})
public class RedditResponse {

    @JsonProperty("kind")
    private String kind;
    @JsonProperty("data")
    private RedditResponseData data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("kind")
    public String getKind() {
        return kind;
    }

    @JsonProperty("kind")
    public void setKind(String kind) {
        this.kind = kind;
    }

    @JsonProperty("data")
    public RedditResponseData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(RedditResponseData data) {
        this.data = data;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RedditResponse{");
        sb.append("kind='").append(kind).append('\'');
        sb.append(", data=").append(data); // Assuming RedditResponseData has a meaningful toString implementation
        sb.append(", additionalProperties=").append(additionalProperties);
        sb.append('}');
        return sb.toString();
    }


}

