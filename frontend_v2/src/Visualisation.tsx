import React from "react";
import axios from "axios";
import { DateTime } from "luxon";
import { select } from "d3";
import { useSelector } from "react-redux";
import { RootState } from "./store";
import graph from "./graph";
import { useParams } from "react-router-dom";

function formatDate(date: string) {
  return `${DateTime.fromISO(date).toISO({ includeOffset: false })}Z`;
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
        `http://localhost:8080/getGraph/${owner}/${project}?start=${formatDate(
          dates.startDate
        )}&end=${formatDate(dates.endDate)}`
      )
      .then((response) => {
        graph(body, response.data);
      });
  }, [dates]);
  return <div />;
}
