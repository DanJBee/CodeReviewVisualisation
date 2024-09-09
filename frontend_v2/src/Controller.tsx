import React from "react";
import { useSelector, useDispatch } from "react-redux";
import {
  updateDates,
  getOneDayFrame,
  getWeekFrame,
  getOneMonthFrame,
} from "./dateSlice";
import { useParams } from "react-router-dom";
import { DateTime } from "luxon";
import { RootState } from "./store";
import { Paper, ButtonGroup, Button, Typography, Slider } from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterLuxon } from "@mui/x-date-pickers/AdapterLuxon";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";

const SLIDER_SIZE = 30;

export default function Controller() {
  const dates = useSelector((state: RootState) => state.dates);
  const dispatch = useDispatch();
  const { owner, project } = useParams<{
    owner: string;
    project: string;
  }>();

  const [sliderDates, setSliderDates] = React.useState<number[]>([
    SLIDER_SIZE - 7,
    SLIDER_SIZE,
  ]);

  function getSliderDate(value: number) {
    const endDate = DateTime.fromISO(dates.endDate);
    return endDate
      .minus({ days: SLIDER_SIZE - value })
      .setLocale("en-gb")
      .toLocaleString();
  }

  React.useEffect(() => {
    const start = DateTime.fromISO(dates.startDate);
    const end = DateTime.fromISO(dates.endDate);
    const diff = end.diff(start, "days").days;
    setSliderDates([SLIDER_SIZE - diff, SLIDER_SIZE]);
  }, [dates]);

  return (
    <Paper
      sx={{
        position: "absolute",
        right: "5%",
        bottom: "5%",
        padding: "30px",
      }}
    >
      <LocalizationProvider dateAdapter={AdapterLuxon}>
        <Typography variant="h5">
          {owner}/{project}
        </Typography>
        <br />
        <DatePicker
          value={DateTime.fromISO(dates.startDate)}
          format="dd-MM-yyyy"
          onChange={(newDate) =>
            dispatch(
              updateDates({
                startDate: newDate!.toISO()!,
                endDate: dates.endDate,
              })
            )
          }
        />
        <DatePicker
          value={DateTime.fromISO(dates.endDate)}
          format="dd-MM-yyyy"
          onChange={(newDate) =>
            dispatch(
              updateDates({
                startDate: dates.startDate,
                endDate: newDate!.toISO()!,
              })
            )
          }
        />
        <br />
        <br />
        <br />
        <br />

        <Slider
          value={sliderDates}
          getAriaValueText={getSliderDate}
          valueLabelDisplay="on"
          valueLabelFormat={getSliderDate}
          max={SLIDER_SIZE}
          onChange={(e: Event, newValue: number | number[]) => {
            console.log(newValue);
            dispatch(
              updateDates({
                startDate: DateTime.fromISO(dates.endDate)
                  .minus({ days: SLIDER_SIZE - newValue[0] })
                  .toISO()!,
                endDate: DateTime.fromISO(dates.endDate)
                  .minus({ days: SLIDER_SIZE - newValue[1] })
                  .toISO()!,
              })
            );
            setSliderDates(newValue as number[]);
          }}
          disableSwap
        />
        <br />
        <br />
        <ButtonGroup
          sx={{
            display: "flex",
            justifyContent: "flex-end",
            alignContent: "flex-end",
          }}
        >
          <Button color="success" onClick={() => dispatch(getOneMonthFrame())}>
            A month
          </Button>
          <Button color="info" onClick={() => dispatch(getWeekFrame(2))}>
            Two Weeks
          </Button>
          <Button color="primary" onClick={() => dispatch(getWeekFrame(1))}>
            A week
          </Button>
          <Button color="warning" onClick={() => dispatch(getOneDayFrame())}>
            A day
          </Button>
        </ButtonGroup>
      </LocalizationProvider>
    </Paper>
  );
}
