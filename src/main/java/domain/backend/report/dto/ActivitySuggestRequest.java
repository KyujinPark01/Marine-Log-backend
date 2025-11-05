package domain.backend.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivitySuggestRequest {
    private Long latitude;
    private Long longitude;
}
