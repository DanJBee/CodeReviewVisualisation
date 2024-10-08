import React from "react";
import axios from "axios";
import { DateTime } from "luxon";
import { select } from "d3";
import { useSelector } from "react-redux";
import { RootState } from "./store";
import graph from "./graph";
import { useParams } from "react-router-dom";
import { API_ENDPOINT } from "./config";

function converToISO(date: string) {
  return DateTime.fromISO(date).toJSDate().toISOString();
}

export default function Visualisation() {
  const { owner, project } = useParams<{
    owner: string;
    project: string;
  }>();

  const dates = useSelector((state: RootState) => state.dates);
  const body = select("body").attr("style", "overflow: hidden;");

  React.useEffect(() => {
    axios
      .get(
        `${API_ENDPOINT}/getGraph/${owner}/${project}?start=${converToISO(
          dates.startDate
        )}&end=${converToISO(dates.endDate)}`
      )
      .then((response) => {
        graph(body, response.data);
      });
  }, [dates]);
  return <div />;
}
