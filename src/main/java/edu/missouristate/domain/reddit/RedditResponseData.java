package edu.missouristate.domain.reddit;

import com.fasterxml.jackson.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "after",
        "dist",
        "modhash",
        "geo_filter",
        "children",
        "before"
})
public class RedditResponseData {

    @JsonProperty("after")
    private Object after;
    @JsonProperty("dist")
    private Integer dist;
    @JsonProperty("modhash")
    private String modhash;
    @JsonProperty("geo_filter")
    private String geoFilter;
    @JsonProperty("children")
    private List<RedditPostWrapper> children;
    @JsonProperty("before")
    private Object before;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("after")
    public Object getAfter() {
        return after;
    }

    @JsonProperty("after")
    public void setAfter(Object after) {
        this.after = after;
    }

    @JsonProperty("dist")
    public Integer getDist() {
        return dist;
    }

    @JsonProperty("dist")
    public void setDist(Integer dist) {
        this.dist = dist;
    }

    @JsonProperty("modhash")
    public String getModhash() {
        return modhash;
    }

    @JsonProperty("modhash")
    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    @JsonProperty("geo_filter")
    public String getGeoFilter() {
        return geoFilter;
    }

    @JsonProperty("geo_filter")
    public void setGeoFilter(String geoFilter) {
        this.geoFilter = geoFilter;
    }

    @JsonProperty("children")
    public List<RedditPostWrapper> getChildren() {
        return children;
    }

    @JsonProperty("children")
    public void setChildren(List<RedditPostWrapper> children) {
        this.children = children;
    }

    @JsonProperty("before")
    public Object getBefore() {
        return before;
    }

    @JsonProperty("before")
    public void setBefore(Object before) {
        this.before = before;
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
        sb.append("RedditResponseData{");
        sb.append("after=").append(after);
        sb.append(", dist=").append(dist);
        sb.append(", modhash='").append(modhash).append('\'');
        sb.append(", geoFilter='").append(geoFilter).append('\'');
        sb.append(", children=").append(children); // This assumes RedditPostWrapper has a meaningful toString() implementation.
        sb.append(", before=").append(before);
        sb.append(", additionalProperties=").append(additionalProperties);
        sb.append('}');
        return sb.toString();
    }

}

