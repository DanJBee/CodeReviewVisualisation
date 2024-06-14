import { DatePicker } from '@mui/x-date-pickers';
import { useState } from 'react';
import moment, { Moment } from 'moment';

function TimePeriodSelectorComponent() {
  const [startDate, setStartDate] = useState<Moment | null>(
    moment('2014-07-08'),
  );
  const [endDate, setEndDate] = useState<Moment | null>(moment(new Date()));

  return (
    <div className="container">
      <DatePicker
        className="date-picker"
        label="Start date"
        value={startDate}
        onChange={(newStartDate) => setStartDate(newStartDate)}
        minDate={moment('2014-07-08')}
        maxDate={moment(new Date())}
      />
      <DatePicker
        className="date-picker"
        label="End date"
        value={endDate}
        onChange={(newEndDate) => setEndDate(newEndDate)}
        minDate={moment('2014-07-08')}
        maxDate={moment(new Date())}
      />
    </div>
  );
}

export default TimePeriodSelectorComponent;
