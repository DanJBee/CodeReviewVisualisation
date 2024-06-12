// Graph Component file

import './App.css';
import { select } from 'd3';
import graph from './Graph';

function GraphComponent() {
  // Removes the overflow from the body of the page
  const body = select('body').attr('style', 'overflow: hidden;');

  // Generates the force-directed graph so it appears on the screen
  graph(body).then(() => {});

  return <div />;
}

export default GraphComponent;
