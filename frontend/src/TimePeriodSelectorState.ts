import { Moment } from 'moment';
import { createContext, Dispatch, SetStateAction } from 'react';

export type TimePeriodSelectorState = {
  startDate: Moment | null;
  endDate: Moment | null;
};

export type TimePeriodSelectorContextType = {
  state: TimePeriodSelectorState;
  setState: Dispatch<SetStateAction<TimePeriodSelectorState>>;
};

export const TimePeriodSelectorContext = createContext<
TimePeriodSelectorContextType | undefined
>(undefined);
