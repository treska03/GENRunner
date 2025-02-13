package pl.edu.agh.dto;

import java.util.List;

public record GroupedExperimentsRequest(
    String groupName,
    List<String> experimentsIdToAdd
) {


}
