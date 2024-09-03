import "./App.css";
import { Button, ButtonGroup } from "@mui/material";
import { useContext } from "react";
import moment from "moment";
import { TimePeriodSelectorContext } from "./TimePeriodSelectorState";

function TimeButtonsComponent() {
  const timePeriodSelectorState = useContext(TimePeriodSelectorContext);

  const handleOneYearDecrease = () => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: moment(prevState.startDate).subtract(1, "year"),
      }));
    }
  };

  const handleOneYearIncrease = () => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: moment(prevState.startDate).add(1, "year"),
      }));
    }
  };

  const handleOneMonthDecrease = () => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: moment(prevState.startDate).subtract(1, "month"),
      }));
    }
  };

  const handleOneMonthIncrease = () => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: moment(prevState.startDate).add(1, "month"),
      }));
    }
  };

  const handleOneDayDecrease = () => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: moment(prevState.startDate).subtract(1, "day"),
      }));
    }
  };

  return (
    <ButtonGroup id="time-button" variant="contained">
      <Button color="primary" onClick={() => handleOneYearDecrease()}>
        -1 Month
      </Button>
      <Button color="primary" onClick={() => handleOneYearIncrease()}>
        +1 Month
      </Button>
      <Button color="success" onClick={() => handleOneMonthDecrease()}>
        -1 Week
      </Button>
      <Button color="success" onClick={() => handleOneMonthIncrease()}>
        +1 Week
      </Button>
      <Button color="warning" onClick={() => handleOneDayDecrease()}>
        -1 Day
      </Button>
      <Button color="warning" onClick={() => handleOneMonthDecrease()}>
        +1 Day
      </Button>
    </ButtonGroup>
  );
}

export default TimeButtonsComponent;
