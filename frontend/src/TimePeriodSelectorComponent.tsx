import { DatePicker } from '@mui/x-date-pickers';
import dayjs, { Dayjs } from 'dayjs';
import { useState } from 'react';

function getCurrentDate() {
  const year = new Date().getFullYear().toString();
  let month = (new Date().getMonth() + 1).toString();
  let day = new Date().getDate().toString();

  if (month.length < 2) {
    month = `0${month}`;
  }
  if (day.length < 2) {
    day = `0${day}`;
  }

  return `${year}-${month.valueOf()}-${day.valueOf()}`;
}

function TimePeriodSelectorComponent() {
  const [startDate, setStartDate] = useState<Dayjs | null>(dayjs('2014-07-08'));
  const [endDate, setEndDate] = useState<Dayjs | null>(dayjs(new Date()));

  return (
    <div className="container">
      <DatePicker
        className="date-picker"
        label="Start date"
        value={startDate}
        onChange={(newStartDate) => setStartDate(newStartDate)}
        minDate={dayjs('2014-07-08')}
        maxDate={dayjs(getCurrentDate())}
      />
      <DatePicker
        className="date-picker"
        label="End date"
        value={endDate}
        onChange={(newEndDate) => setEndDate(newEndDate)}
        minDate={dayjs('2014-07-08')}
        maxDate={dayjs(getCurrentDate())}
      />
    </div>
  );
}

export default TimePeriodSelectorComponent;
