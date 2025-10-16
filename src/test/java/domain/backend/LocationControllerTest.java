package domain.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.backend.location.controller.LocationController;
import domain.backend.location.dto.LocationAggregationDto;
import domain.backend.location.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(locationController).build();
    }

    @Test
    void getAggregatedNational() throws Exception {
        // given
        LocationAggregationDto dto1 = new LocationAggregationDto("서울", 10, 37.5665, 126.9780);
        LocationAggregationDto dto2 = new LocationAggregationDto("부산", 5, 35.1796, 129.0756);
        List<LocationAggregationDto> dtoList = Arrays.asList(dto1, dto2);

        given(locationService.getAggregatedNationalData()).willReturn(dtoList);

        // when & then
        mockMvc.perform(get("/location/activity/national"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }
}