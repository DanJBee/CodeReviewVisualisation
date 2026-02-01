package uk.ac.rhul.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Ignore extra properties if encounter any
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeveloperReportDTO(
    @JsonProperty("username")
    String username,

    @JsonProperty("avatarUrl")
    String avatarUrl,

    @JsonProperty("valueScore")
    Integer valueScore,

    @JsonProperty("workVolume")
    String workVolume,

    @JsonProperty("advice")
    String advice,

    @JsonProperty("isSlacker")
    Boolean isSlacker
) {}
