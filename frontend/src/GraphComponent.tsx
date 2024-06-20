// Graph Component file

import './App.css';
import { select } from 'd3';
import { useContext, useEffect } from 'react';
import graph from './Graph';
import { TimePeriodSelectorContext } from './TimePeriodSelectorState';

function GraphComponent() {
  // Removes the overflow from the body of the page
  const body = select('body').attr('style', 'overflow: hidden;');

  const timePeriodSelectorContext = useContext(TimePeriodSelectorContext);

  useEffect(() => {
    graph(body, timePeriodSelectorContext).then(() => {});
  }, [body, timePeriodSelectorContext, timePeriodSelectorContext?.state]);

  return <div />;
}

export default GraphComponent;
