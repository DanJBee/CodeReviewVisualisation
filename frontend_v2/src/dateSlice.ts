import { DateTime } from "luxon";
import { createSlice } from "@reduxjs/toolkit";
import { DEFAULT_END_DATE } from "./config";
import type { PayloadAction } from "@reduxjs/toolkit";

export interface Timeframe {
  startDate: string;
  endDate: string;
}

const initialState: Timeframe = {
  startDate: DateTime.fromISO(DEFAULT_END_DATE).minus({ weeks: 1 }).toISO()!,
  endDate: DateTime.fromISO(DEFAULT_END_DATE).toISO()!,
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
    getWeekFrame: (state, action: PayloadAction<number>) => {
      state.startDate = DateTime.fromISO(state.endDate)
        .minus({ weeks: action.payload })
        .toISO()!;
    },
    getOneMonthFrame: (state) => {
      state.startDate = DateTime.fromISO(state.endDate)
        .minus({ months: 1 })
        .toISO()!;
    },
  },
});

export const { updateDates, getOneDayFrame, getWeekFrame, getOneMonthFrame } =
  dateSlice.actions;

export default dateSlice.reducer;
