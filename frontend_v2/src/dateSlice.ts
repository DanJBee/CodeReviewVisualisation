import { DateTime } from "luxon";
import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";

export interface Timeframe {
  startDate: string;
  endDate: string;
}

const initialState: Timeframe = {
  startDate: DateTime.now().minus({ weeks: 1 }).toISO(),
  endDate: DateTime.now().toISO(),
};

export const dateSlice = createSlice({
  name: "dates",
  initialState,
  reducers: {
    updateDates: (
      state,
      action: PayloadAction<{ startDate: string; endDate: string }>
    ) => {
      state.startDate = action.payload.startDate;
      state.endDate = action.payload.endDate;
    },
    getOneDayFrame: (state) => {
      state.startDate = DateTime.fromISO(state.endDate)
        .minus({ days: 1 })
        .toISO()!;
    },
    getOneWeekFrame: (state) => {
      state.startDate = DateTime.fromISO(state.endDate)
        .minus({ weeks: 1 })
        .toISO()!;
    },
    getOneMonthFrame: (state) => {
      state.startDate = DateTime.fromISO(state.endDate)
        .minus({ months: 1 })
        .toISO()!;
    },
  },
});

export const {
  updateDates,
  getOneDayFrame,
  getOneWeekFrame,
  getOneMonthFrame,
} = dateSlice.actions;

export default dateSlice.reducer;
