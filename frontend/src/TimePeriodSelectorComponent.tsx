import { DatePicker } from '@mui/x-date-pickers';
import moment, { Moment } from 'moment';
import { useContext } from 'react';
import { TimePeriodSelectorContext } from './TimePeriodSelectorState';

function TimePeriodSelectorComponent() {
  const timePeriodSelectorState = useContext(TimePeriodSelectorContext);

  const handleStartDateChange = (newStartDate: Moment | null) => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        startDate: newStartDate,
      }));
    }
  };

  const handleEndDateChange = (newEndDate: Moment | null) => {
    if (timePeriodSelectorState?.setState) {
      timePeriodSelectorState.setState((prevState) => ({
        ...prevState,
        endDate: newEndDate,
      }));
    }
  };

  return (
    <div className="container">
      <DatePicker
        className="date-picker"
        label="Start date"
        value={timePeriodSelectorState?.state.startDate}
        onChange={(newStartDate) => handleStartDateChange(newStartDate)}
        minDate={moment('2014-07-08')}
        maxDate={moment(new Date())}
      />
      <DatePicker
        className="date-picker"
        label="End date"
        value={timePeriodSelectorState?.state.endDate}
        onChange={(newEndDate) => handleEndDateChange(newEndDate)}
        minDate={moment('2014-07-08')}
        maxDate={moment(new Date())}
      />
    </div>
  );
}

export default TimePeriodSelectorComponent;
