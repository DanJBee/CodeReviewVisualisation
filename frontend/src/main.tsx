import ReactDOM from 'react-dom/client';
import { LocalizationProvider } from '@mui/x-date-pickers';
import 'moment/locale/en-gb';
import { AdapterMoment } from '@mui/x-date-pickers/AdapterMoment';
import GraphComponent from './GraphComponent';
import TimePeriodSelectorComponent from './TimePeriodSelectorComponent';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <>
    <GraphComponent />
    <LocalizationProvider dateAdapter={AdapterMoment} adapterLocale="en-gb">
      <TimePeriodSelectorComponent />
    </LocalizationProvider>
  </>,
);
