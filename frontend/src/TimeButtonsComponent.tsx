import './App.css';
import { Button } from '@mui/material';
import { useContext } from 'react';
import moment from 'moment';
import { TimePeriodSelectorContext } from './TimePeriodSelectorState';

function TimeButtonsComponent() {
  const timePeriodSelectorState = useContext(TimePeriodSelectorContext);

  const handleOneYearDecrease = () => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: moment(prevState.startDate).subtract(1, 'year'),
      }));
    }
  };

  const handleOneYearIncrease = () => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: moment(prevState.startDate).add(1, 'year'),
      }));
    }
  };

  const handleOneMonthDecrease = () => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: moment(prevState.startDate).subtract(1, 'month'),
      }));
    }
  };

  const handleOneMonthIncrease = () => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: moment(prevState.startDate).add(1, 'month'),
      }));
    }
  };

  const handleOneDayDecrease = () => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: moment(prevState.startDate).subtract(1, 'day'),
      }));
    }
  };

  return (
    <>
      <Button
        variant="contained"
        id="time-button"
        onClick={() => handleOneYearDecrease()}
        style={{ right: 455 }}
      >
        -1 Year
      </Button>
      <Button
        variant="contained"
        id="time-button"
        onClick={() => handleOneYearIncrease()}
        style={{ right: 365 }}
      >
        +1 Year
      </Button>
      <Button
        variant="contained"
        id="time-button"
        onClick={() => handleOneMonthDecrease()}
        style={{ right: 265 }}
      >
        -1 Month
      </Button>
      <Button
        variant="contained"
        id="time-button"
        onClick={() => handleOneMonthIncrease()}
        style={{ right: 160 }}
      >
        +1 Month
      </Button>
      <Button
        variant="contained"
        id="time-button"
        onClick={() => handleOneDayDecrease()}
        style={{ right: 80 }}
      >
        -1 Day
      </Button>
      <Button
        variant="contained"
        id="time-button"
        onClick={() => handleOneMonthDecrease()}
      >
        +1 Day
      </Button>
    </>
  );
}

export default TimeButtonsComponent;
