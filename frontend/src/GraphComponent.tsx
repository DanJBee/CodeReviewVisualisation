// Graph Component file

import './App.css';
import { select } from 'd3';
import { graph } from './Graph.ts';

function GraphComponent() {
  // Removes the overflow from the body of the page
  const body = select('body').attr('style', 'overflow: hidden;');

  // Defines the main function that generates the force-directed graph onto the screen
  const main = async () => {
    const force_directed_graph = graph();

    body.call(force_directed_graph);
  };

  main().then(() => {});

  return <></>;
}

export default GraphComponent;
