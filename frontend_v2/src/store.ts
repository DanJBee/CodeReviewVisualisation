import { configureStore } from "@reduxjs/toolkit";
import dateReducer from "./dateSlice.ts";

export const store = configureStore({
  reducer: {
    dates: dateReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;

export type AppDispatch = typeof store.dispatch;
