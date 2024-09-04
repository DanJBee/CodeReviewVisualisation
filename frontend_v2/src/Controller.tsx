import { useSelector, useDispatch } from "react-redux";
import {
  updateDates,
  getOneDayFrame,
  getOneWeekFrame,
  getOneMonthFrame,
} from "./dateSlice";
import { useParams } from "react-router-dom";
import { DateTime } from "luxon";
import { RootState } from "./store";
import { Paper, ButtonGroup, Button, Typography } from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterLuxon } from "@mui/x-date-pickers/AdapterLuxon";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";

export default function Controller() {
  const dates = useSelector((state: RootState) => state.dates);
  const dispatch = useDispatch();
  const { owner, project } = useParams<{
    owner: string;
    project: string;
  }>();

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
          <Button color="primary" onClick={() => dispatch(getOneWeekFrame())}>
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
