import React from 'react';
import ReactDOM from 'react-dom/client';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/en-gb';
import GraphComponent from './GraphComponent';
import TimePeriodSelectorComponent from './TimePeriodSelectorComponent';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <GraphComponent />
    <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en-gb">
      <TimePeriodSelectorComponent />
    </LocalizationProvider>
  </React.StrictMode>,
);
