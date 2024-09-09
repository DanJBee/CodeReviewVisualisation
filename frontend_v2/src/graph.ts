import {
  drag,
  forceCenter,
  forceCollide,
  forceLink,
  forceManyBody,
  forceSimulation,
  max,
  min,
  scaleLinear,
  scaleSqrt,
  zoom,
} from "d3";
import { Data, Link, Node } from "./types";

const graph = () => {
  // Defines height & width values
  const height = window.innerHeight;
  const width = window.innerWidth;

  return async (selection: any, data: Data) => {
    // Removes the old graph
    selection.selectAll(".graph").remove();

    // Defines the links & nodes in the graph
    const links = data.links.map((d: Link) => ({ ...d }));
    const nodes = data.nodes.map((d: Node) => ({ ...d }));

    // Creates a map of nodes by the author ID
    const nodeMap = new Map(nodes.map((node: Node) => [node.authorId, node]));

    // Updates the source & target properties of the links between the nodes in the graph
    links.forEach((link: Link) => {
      const sourceNode: Node | undefined = nodeMap.get(link.source.authorId);
      const targetNode: Node | undefined = nodeMap.get(link.target.authorId);
      const newLink: Link = link;

      if (sourceNode && targetNode) {
        newLink.source = sourceNode;
        newLink.target = targetNode;
      }
    });

    // Defines the svg element that the graph will be within
    const svg = selection
      .append("svg")
      .attr("class", "graph")
      .attr("height", height)
      .attr("width", width)
      .attr("viewBox", [0, 0, width / 2, height / 2])
      .attr("style", "max-width: 100%; height: auto; overflow: visible;")
      .call(
        zoom().on("zoom", (event) => {
          svg.attr("transform", event.transform);
        }),
      )
      .append("g");

    // Defines the minimum & maximum thickness of the links between the nodes in the graph
    const minThickness: number | undefined = min(
      links,
      (d: Link) => d.thickness,
    );
    const maxThickness: number | undefined = max(
      links,
      (d: Link) => d.thickness,
    );

    // Defines a scale for the thickness of the links between the nodes in the graph
    const scaleThickness = scaleSqrt()
      .domain([minThickness ?? 1, maxThickness ?? 10])
      .range([0.5, 5]);

    // Defines the link between each node in the graph
    const link = svg
      .append("g")
      .attr("stroke", "#999")
      .attr("stroke-opacity", 1.5)
      .selectAll()
      .data(links)
      .join("line")
      .attr("stroke-width", (d: Link) => scaleThickness(d.thickness)); // this number controls the thickness of each link

    // Defines the minimum & maximum sizes for the nodes in the graph
    const minSize: number | undefined = min(
      nodes.filter((d: Node) => d.size !== undefined),
      (d: Node) => d.size,
    );
    const maxSize: number | undefined = max(
      nodes.filter((d: Node) => d.size !== undefined),
      (d: Node) => d.size,
    );

    // Defines a scale for the sizes of the nodes in the graph
    const scaleSize = scaleLinear()
      .domain([minSize ?? 1, maxSize ?? 4000])
      .range([7, 40]);

    // Defines each individual node in the graph
    const node = svg
      .append("g")
      .attr("stroke", "#fff")
      .attr("stroke-width", 1.5)
      .selectAll()
      .data(nodes)
      .join("circle")
      .attr("r", (d: Node) => scaleSize(d.size)) // this number controls the size of each node
      // sets an individual ID for each node to ensure the images' height/width are the correct
      // dimensions
      .attr("fill", (d: Node) => `url(#image-${d.authorId})`);

    // Sets the position attribute of the links & nodes in the graph each time the nodes 'ticks'
    function ticked() {
      link
        .attr("x1", (d: Link) => d.source.x)
        .attr("y1", (d: Link) => d.source.y)
        .attr("x2", (d: Link) => d.target.x)
        .attr("y2", (d: Link) => d.target.y);

      node.attr("cx", (d: Node) => d.x).attr("cy", (d: any) => d.y);
    }

    // Defines the simulation of the physics on the nodes in the graph
    const simulation = forceSimulation(nodes)
      .force(
        "link",
        forceLink(links).id((d: any) => d.authorId),
      )
      .force("charge", forceManyBody())
      .force("centre", forceCenter(width / 4, height / 4))
      .force(
        "collide",
        forceCollide().radius((d: any) => scaleSize(d.size) * 2),
      )
      .on("tick", ticked);

    // Reheats the simulation when the drag starts & fix the subject's i.e. the node's position
    function startDrag(event: any) {
      const newEvent = event;
      if (!event.active) simulation.alphaTarget(0.3).restart();
      newEvent.subject.fx = event.x;
      newEvent.subject.fy = event.y;
    }

    // Updates the subject's i.e. the dragged node's position during the drag
    function dragging(event: any) {
      const newEvent = event;
      newEvent.subject.fx = event.x;
      newEvent.subject.fy = event.y;
    }

    // Restores the target alpha so the simulation cools after dragging ends
    // Unfixes the subject's i.e. the previously dragged node's position now that it is no longer
    // being dragged
    function endDrag(event: any) {
      const newEvent = event;
      if (!event.active) simulation.alphaTarget(0);
      newEvent.subject.fx = null;
      newEvent.subject.fy = null;
    }

    // Defines the element for the avatar URL pattern on the nodes in the graph
    const defs = svg.append("defs");

    // Defines the avatar URL pattern themselves on the nodes in the graph
    const pattern = defs
      .selectAll("pattern")
      .data(nodes)
      .enter()
      .append("pattern")
      .attr("id", (d: Node) => `image-${d.authorId}`)
      .attr("x", "0")
      .attr("y", "0")
      .attr("height", "1")
      .attr("width", "1");

    // Adds the avatar URL pattern to the nodes in the graph
    pattern
      .append("image")
      .attr("x", "0")
      .attr("y", "0")
      .attr("height", (d: Node) => scaleSize(d.size) * 2) // double the radius value
      .attr("width", (d: Node) => scaleSize(d.size) * 2) // double the radius value
      .attr("xlink:href", (d: Node) => d.avatarUrl) // this value controls the image on the nodes in the graph
      .attr("style", "filter: blur(2px);");

    // Appends a title to each node
    node.append("title").text((d: Node) => d.authorId);

    // Applies the drag physics to each node in the graph
    node.call(
      drag().on("start", startDrag).on("drag", dragging).on("end", endDrag),
    );
  };
};

export default graph();
