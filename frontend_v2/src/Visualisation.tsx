import React from "react";
import axios from "axios";
import { DateTime } from "luxon";
import { select } from "d3";
import { useSelector } from "react-redux";
import { RootState } from "./store";
import graph from "./graph";
import { useParams } from "react-router-dom";
import { API_ENDPOINT } from "./config";

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
        `${API_ENDPOINT}/getGraph/${owner}/${project}?start=${
          DateTime.fromISO(dates.startDate).toISO({
            includeOffset: false,
          }) + "Z"
        }&end=${
          DateTime.fromISO(dates.endDate).toISO({
            includeOffset: false,
          }) + "Z"
        }`,
      )
      .then((response) => {
        graph(body, response.data);
      });
  }, [dates]);
  return <div />;
}
