import { useMemo, useState } from "react";
import moment from "moment";
import "moment/locale/en-gb";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterMoment } from "@mui/x-date-pickers/AdapterMoment";
import {
  TimePeriodSelectorContext,
  TimePeriodSelectorState,
} from "./TimePeriodSelectorState";
import GraphComponent from "./GraphComponent";
import TimePeriodSelectorComponent from "./TimePeriodSelectorComponent";
import TimeButtonsComponent from "./TimeButtonsComponent";

const initialState: TimePeriodSelectorState = {
  startDate: moment(new Date().setMonth(new Date().getMonth() - 1)),
  endDate: moment(new Date()),
};

function App() {
  const [state, setState] = useState(initialState);

  // Only updates the state context value when one or more of the start/end dates have been changed
  const contextValue = useMemo(() => ({ state, setState }), [state, setState]);

  return (
    <TimePeriodSelectorContext.Provider value={contextValue}>
      <GraphComponent />
      <div style={{ margin: "50px" }}>
        <LocalizationProvider dateAdapter={AdapterMoment} adapterLocale="en-gb">
          <TimePeriodSelectorComponent />
        </LocalizationProvider>
        <TimeButtonsComponent />
      </div>
    </TimePeriodSelectorContext.Provider>
  );
}

export default App;
