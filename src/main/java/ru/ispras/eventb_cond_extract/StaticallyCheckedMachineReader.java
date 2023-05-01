package ru.ispras.eventb_cond_extract;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reader for Rodin Event-B statically checked machine files.
 *
 * Rodin stores Event-B model in a bunch of files. The reader
 * aims to *.bcm files. These files are contained statically
 * checked models.
 *
 * To read the machine instantiate this class and call
 * {@link read} method with path to *.bcm file. It returns the machine.
 */
public class StaticallyCheckedMachineReader {
	static final String MACHINE_TAG = "org.eventb.core.scMachineFile";
	static final String VARIABLE_TAG = "org.eventb.core.scVariable";
	static final String INVARIANT_TAG = "org.eventb.core.scInvariant";
	static final String EVENT_TAG = "org.eventb.core.scEvent";
	static final String PARAMETER_TAG = "org.eventb.core.scParameter";
	static final String GUARD_TAG = "org.eventb.core.scGuard";

	static final String LABEL_ATTRIBUTE = "org.eventb.core.label";
	static final String NAME_ATTRIBUTE = "name";
	static final String TYPE_ATTRIBUTE = "org.eventb.core.type";
	static final String PREDICATE_ATTRIBUTE = "org.eventb.core.predicate";

	/**
	 * Reads the machine from the *.bcm file.
	 *
	 * @param path		path to file with staticaly checked machine (*.bcm file)
	 * @return				statically checked machine
	 * @throws ParserConfigurationException	if the *.bcm file is incorrect
	 * @throws SAXException			if the *.bcm file syntax is incorrect
	 * @throws IOException			if the *.bcm file can't be opened or read
	 * @throws IllegalArgumentException	if the *.bcm file content is incorrect
	 */
	public StaticallyCheckedMachine read(final String path)
		throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document document = builder.parse(new File(path));

		return readMachine(document.getDocumentElement());
	}

	private StaticallyCheckedMachine readMachine(final Element element) {
		return new StaticallyCheckedMachine (
			readVariables(element.getElementsByTagName(VARIABLE_TAG)),
			readInvariants(element.getElementsByTagName(INVARIANT_TAG)),
			readEvents(element.getElementsByTagName(EVENT_TAG)));
	}

	private List<StaticallyCheckedVariable> readVariables(final NodeList nodes) {
		return
			IntStream.range(0, nodes.getLength())
			.mapToObj(i -> nodes.item(i))
			.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
			.map(n -> (Element) n)
			.map(e -> new StaticallyCheckedVariable(
						e.getAttribute(NAME_ATTRIBUTE),
						e.getAttribute(TYPE_ATTRIBUTE)))
			.collect(Collectors.toList());
	}

	private List<StaticallyCheckedEvent> readEvents(final NodeList nodes) {
		return
			IntStream.range(0, nodes.getLength())
			.mapToObj(i -> nodes.item(i))
			.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
			.map(n -> (Element) n)
			.map(e -> new StaticallyCheckedEvent(
						e.getAttribute(LABEL_ATTRIBUTE),
						readParameters(e.getElementsByTagName(PARAMETER_TAG)),
						readGuards(e.getElementsByTagName(GUARD_TAG))))
			.collect(Collectors.toList());
	}

	private List<StaticallyCheckedInvariant> readInvariants(final NodeList nodes) {
		return
			IntStream.range(0, nodes.getLength())
			.mapToObj(i -> nodes.item(i))
			.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
			.map(n -> (Element) n)
			.map(e -> new StaticallyCheckedInvariant(
						e.getAttribute(LABEL_ATTRIBUTE),
						e.getAttribute(PREDICATE_ATTRIBUTE)))
			.collect(Collectors.toList());
	}

	private List<StaticallyCheckedParameter> readParameters(final NodeList nodes) {
		return
			IntStream.range(0, nodes.getLength())
			.mapToObj(i -> nodes.item(i))
			.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
			.map(n -> (Element) n)
			.map(e -> new StaticallyCheckedParameter(
						e.getAttribute(NAME_ATTRIBUTE),
						e.getAttribute(TYPE_ATTRIBUTE)))
			.collect(Collectors.toList());
	}

	private List<StaticallyCheckedGuard> readGuards(final NodeList nodes) {
		return
			IntStream.range(0, nodes.getLength())
			.mapToObj(i -> nodes.item(i))
			.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
			.map(n -> (Element) n)
			.map(e -> new StaticallyCheckedGuard(
						e.getAttribute(LABEL_ATTRIBUTE),
						e.getAttribute(PREDICATE_ATTRIBUTE)))
			.collect(Collectors.toList());
	}
}
