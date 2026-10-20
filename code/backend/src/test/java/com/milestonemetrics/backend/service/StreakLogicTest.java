package com.milestonemetrics.backend.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Pure unit tests for the weekly streak calculation — no Spring context or
 * database required, so these run anywhere.
 */
class StreakLogicTest {

    private static List<LocalDate> weeks(int... weekOffsets) {
        LocalDate base = LocalDate.of(2026, 1, 5); // a Monday
        return java.util.Arrays.stream(weekOffsets)
                .mapToObj(w -> base.plusWeeks(w))
                .sorted()
                .toList();
    }

    @Test
    void noQualifyingPeriods() {
        assertArrayEquals(new int[]{0, 0, 0},
                GamificationService.streaksFromStarts(List.of(), 7));
    }

    @Test
    void singlePeriod() {
        assertArrayEquals(new int[]{1, 1, 1},
                GamificationService.streaksFromStarts(weeks(0), 7));
    }

    @Test
    void fourConsecutiveWeeks() {
        // current=4, longest=4, count=4
        assertArrayEquals(new int[]{4, 4, 4},
                GamificationService.streaksFromStarts(weeks(0, 1, 2, 3), 7));
    }

    @Test
    void gapBreaksStreak() {
        // weeks 0,1,2 then gap then 4,5  -> longest=3, current=2 (ends at 5), count=5
        assertArrayEquals(new int[]{2, 3, 5},
                GamificationService.streaksFromStarts(weeks(0, 1, 2, 4, 5), 7));
    }

    @Test
    void latestRunShorterThanLongest() {
        // weeks 0,1,2,3,4 (5-run) then gap then 7 (single) -> longest=5, current=1, count=6
        assertArrayEquals(new int[]{1, 5, 6},
                GamificationService.streaksFromStarts(weeks(0, 1, 2, 3, 4, 7), 7));
    }
}
