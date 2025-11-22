package domain.backend.dashboard.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/sql/dashboard/cleanup.sql", "/sql/dashboard/dashboard-data.sql"})
public class WasteStatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Summary endpoint aggregates totals for the requested organization")
    void summaryReturnsAggregates() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                        .param("start", "2024-01-01")
                        .param("end", "2024-12-31")
                        .param("orgId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activityCount").value(2))
                .andExpect(jsonPath("$.data.totalKg").value(closeTo(40.0, 0.001)))
                .andExpect(jsonPath("$.data.totalL").value(closeTo(12.5, 0.001)))
                .andExpect(jsonPath("$.data.avgKgPerActivity").value(closeTo(20.0, 0.001)));
    }

    @Test
    @DisplayName("Timeseries endpoint exposes month buckets for activities hosted by the organization")
    void timeSeriesReturnsMonthlyBuckets() throws Exception {
        mockMvc.perform(get("/api/dashboard/timeseries")
                        .param("start", "2024-01-01")
                        .param("end", "2024-12-31")
                        .param("orgId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].month").value("2024-03"))
                .andExpect(jsonPath("$.data[0].totalKg").value(closeTo(15.0, 0.001)))
                .andExpect(jsonPath("$.data[0].activityCount").value(1))
                .andExpect(jsonPath("$.data[1].month").value("2024-04"))
                .andExpect(jsonPath("$.data[1].totalKg").value(closeTo(25.0, 0.001)))
                .andExpect(jsonPath("$.data[1].activityCount").value(1));
    }

    @Test
    @DisplayName("Material breakdown returns kg totals plus ratio for each waste type")
    void materialBreakdownReturnsRatios() throws Exception {
        mockMvc.perform(get("/api/dashboard/material-breakdown")
                        .param("start", "2024-01-01")
                        .param("end", "2024-12-31")
                        .param("orgId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].material").value("Plastic"))
                .andExpect(jsonPath("$.data[0].kg").value(closeTo(25.0, 0.001)))
                .andExpect(jsonPath("$.data[0].ratio").value(closeTo(0.625, 0.0001)))
                .andExpect(jsonPath("$.data[1].material").value("Glass"))
                .andExpect(jsonPath("$.data[1].kg").value(closeTo(10.0, 0.001)))
                .andExpect(jsonPath("$.data[1].ratio").value(closeTo(0.25, 0.0001)))
                .andExpect(jsonPath("$.data[2].material").value("Metal"))
                .andExpect(jsonPath("$.data[2].kg").value(closeTo(5.0, 0.001)))
                .andExpect(jsonPath("$.data[2].ratio").value(closeTo(0.125, 0.0001)));
    }

    @Test
    @DisplayName("Org leaderboard ranks organizations by collected kg")
    void orgLeaderboardReturnsDescendingTotals() throws Exception {
        mockMvc.perform(get("/api/org-leaderboard")
                        .param("start", "2024-01-01")
                        .param("end", "2024-12-31")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].orgId").value(1))
                .andExpect(jsonPath("$.data[0].orgName").value("Ocean Savers"))
                .andExpect(jsonPath("$.data[0].totalKg").value(closeTo(40.0, 0.001)))
                .andExpect(jsonPath("$.data[1].orgId").value(2))
                .andExpect(jsonPath("$.data[1].orgName").value("River Keepers"))
                .andExpect(jsonPath("$.data[1].totalKg").value(closeTo(17.0, 0.001)));
    }

    @Test
    @DisplayName("Region dashboard uses spatial bounds to aggregate activities")
    void regionDashboardAggregatesActivitiesInsideBounds() throws Exception {
        String boundsJson = """
                {
                  "swLat": 37.0,
                  "swLng": 126.5,
                  "neLat": 38.0,
                  "neLng": 127.2
                }
                """;

        mockMvc.perform(post("/api/dashboard/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(boundsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalWeightKg").value(closeTo(40.0, 0.001)))
                .andExpect(jsonPath("$.data.wasteCount").value(4))
                .andExpect(jsonPath("$.data.materials[*].material",
                        containsInAnyOrder("Plastic", "Metal", "Glass")))
                .andExpect(jsonPath("$.data.trend", hasSize(2)))
                .andExpect(jsonPath("$.data.trend[0].month").value("2024-03"))
                .andExpect(jsonPath("$.data.trend[1].month").value("2024-04"));
    }
}
