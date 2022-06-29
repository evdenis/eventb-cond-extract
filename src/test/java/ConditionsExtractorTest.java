import java.util.Arrays;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

import ru.ispras.eventb_cond_extract.*;

public class ConditionsExtractorTest
{
	static String INT = "\u2124"; //Z

	@Test
	public void oneCondition()
	{
		final StaticallyCheckedParameter scParameter = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard scGuard = new StaticallyCheckedGuard("grd1", "p > 0");
		final StaticallyCheckedEvent scEvent = new StaticallyCheckedEvent("event-1", Arrays.asList(scParameter), Arrays.asList(scGuard)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(new ArrayList<>(), Arrays.asList(scEvent));
		final ConditionsExtractor conditionsExtractor = new ConditionsExtractor(scMachine);
		
		assertEquals(conditionsExtractor.conditions.conditions_order.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions_order.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").size(), 1);
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(0), "grd1/1");
		
		assertEquals(conditionsExtractor.conditions.conditions.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions.get("event-1").size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd1/1"));
		final Condition c = conditionsExtractor.conditions.conditions.get("event-1").get("grd1/1");
		assertEquals(c.id, "grd1/1");
	       	assertEquals(c.predicate.toString(), "p>0");
	}

	@Test
	public void setTheoryCondition()
	{
		final StaticallyCheckedVariable scVariable = new StaticallyCheckedVariable("Processes", "\u2119(\u2124)"); // POW(INT)
		final StaticallyCheckedParameter scParameter = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard scGuard = new StaticallyCheckedGuard("grd1", "p \u2208 Processes");
		final StaticallyCheckedEvent scEvent = new StaticallyCheckedEvent("event-1", Arrays.asList(scParameter), Arrays.asList(scGuard)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(Arrays.asList(scVariable), Arrays.asList(scEvent));
		final ConditionsExtractor conditionsExtractor = new ConditionsExtractor(scMachine);
		
		assertEquals(conditionsExtractor.conditions.conditions_order.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions_order.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").size(), 1);
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(0), "grd1/1");
		
		assertEquals(conditionsExtractor.conditions.conditions.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions.get("event-1").size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd1/1"));
		final Condition c = conditionsExtractor.conditions.conditions.get("event-1").get("grd1/1");
		assertEquals(c.id, "grd1/1");
	       	assertEquals(c.predicate.toString(), "p\u2208Processes");
	}

	@Test
	public void splitCondition()
	{
		final StaticallyCheckedParameter scParameter = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard scGuard = new StaticallyCheckedGuard("grd1", "(p > 0) \u2227 (p < 1 \u2228 3 = p)");
		final StaticallyCheckedEvent scEvent = new StaticallyCheckedEvent("event-1", Arrays.asList(scParameter), Arrays.asList(scGuard)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(new ArrayList<>(), Arrays.asList(scEvent));
		final ConditionsExtractor conditionsExtractor = new ConditionsExtractor(scMachine);
		
		assertEquals(conditionsExtractor.conditions.conditions_order.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions_order.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").size(), 3);
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(0), "grd1/1");
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(1), "grd1/2");
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(2), "grd1/3");
		
		assertEquals(conditionsExtractor.conditions.conditions.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions.get("event-1").size(), 3);

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd1/1"));
		final Condition c1 = conditionsExtractor.conditions.conditions.get("event-1").get("grd1/1");
		assertEquals(c1.id, "grd1/1");
	       	assertEquals(c1.predicate.toString(), "p>0");

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd1/2"));
		final Condition c2 = conditionsExtractor.conditions.conditions.get("event-1").get("grd1/2");
		assertEquals(c2.id, "grd1/2");
	       	assertEquals(c2.predicate.toString(), "p<1");

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd1/3"));
		final Condition c3 = conditionsExtractor.conditions.conditions.get("event-1").get("grd1/3");
		assertEquals(c3.id, "grd1/3");
	       	assertEquals(c3.predicate.toString(), "3=p");
	}

	@Test
	public void severalGuards()
	{
		final StaticallyCheckedParameter scParameter = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard scGuard1 = new StaticallyCheckedGuard("grd1", "p > 0");
		final StaticallyCheckedGuard scGuard2 = new StaticallyCheckedGuard("grd2", "p < 2");
		final StaticallyCheckedEvent scEvent = new StaticallyCheckedEvent("event-1", Arrays.asList(scParameter), Arrays.asList(scGuard1, scGuard2)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(new ArrayList<>(), Arrays.asList(scEvent));
		final ConditionsExtractor conditionsExtractor = new ConditionsExtractor(scMachine);
		
		assertEquals(conditionsExtractor.conditions.conditions_order.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions_order.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").size(), 2);
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(0), "grd1/1");
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(1), "grd2/1");
		
		assertEquals(conditionsExtractor.conditions.conditions.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions.get("event-1").size(), 2);

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd1/1"));
		final Condition c1 = conditionsExtractor.conditions.conditions.get("event-1").get("grd1/1");
		assertEquals(c1.id, "grd1/1");
	       	assertEquals(c1.predicate.toString(), "p>0");

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd2/1"));
		final Condition c2 = conditionsExtractor.conditions.conditions.get("event-1").get("grd2/1");
		assertEquals(c2.id, "grd2/1");
	       	assertEquals(c2.predicate.toString(), "p<2");
	}

	@Test
	public void equalConditions()
	{
		final StaticallyCheckedParameter scParameter = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard scGuard1 = new StaticallyCheckedGuard("grd1", "p > 0");
		final StaticallyCheckedGuard scGuard2 = new StaticallyCheckedGuard("grd2", "p > 0 \u2227 p < 2");
		final StaticallyCheckedEvent scEvent = new StaticallyCheckedEvent("event-1", Arrays.asList(scParameter), Arrays.asList(scGuard1, scGuard2)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(new ArrayList<>(), Arrays.asList(scEvent));
		final ConditionsExtractor conditionsExtractor = new ConditionsExtractor(scMachine);
		
		assertEquals(conditionsExtractor.conditions.conditions_order.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions_order.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").size(), 2);
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(0), "grd1/1");
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(1), "grd2/1");
		
		assertEquals(conditionsExtractor.conditions.conditions.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions.get("event-1").size(), 2);

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd1/1"));
		final Condition c1 = conditionsExtractor.conditions.conditions.get("event-1").get("grd1/1");
		assertEquals(c1.id, "grd1/1");
	       	assertEquals(c1.predicate.toString(), "p>0");

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd2/1"));
		final Condition c2 = conditionsExtractor.conditions.conditions.get("event-1").get("grd2/1");
		assertEquals(c2.id, "grd2/1");
	       	assertEquals(c2.predicate.toString(), "p<2");
	}

	@Test
	public void identicalConditions1()
	{
		final StaticallyCheckedParameter scParameter = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard scGuard1 = new StaticallyCheckedGuard("grd1", "p > 0");
		final StaticallyCheckedGuard scGuard2 = new StaticallyCheckedGuard("grd2", "(p \u2264 0) \u21d2 p < 2");
		final StaticallyCheckedEvent scEvent = new StaticallyCheckedEvent("event-1", Arrays.asList(scParameter), Arrays.asList(scGuard1, scGuard2)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(new ArrayList<>(), Arrays.asList(scEvent));
		final ConditionsExtractor conditionsExtractor = new ConditionsExtractor(scMachine);
		
		assertEquals(conditionsExtractor.conditions.conditions_order.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions_order.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").size(), 2);
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(0), "grd1/1");
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(1), "grd2/1");
		
		assertEquals(conditionsExtractor.conditions.conditions.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions.get("event-1").size(), 2);

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd1/1"));
		final Condition c1 = conditionsExtractor.conditions.conditions.get("event-1").get("grd1/1");
		assertEquals(c1.id, "grd1/1");
	       	assertEquals(c1.predicate.toString(), "p>0");

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd2/1"));
		final Condition c2 = conditionsExtractor.conditions.conditions.get("event-1").get("grd2/1");
		assertEquals(c2.id, "grd2/1");
	       	assertEquals(c2.predicate.toString(), "p<2");
	}

	@Test
	public void identicalConditions2()
	{
		final StaticallyCheckedParameter scParameter = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard scGuard1 = new StaticallyCheckedGuard("grd1", "0 < p");
		final StaticallyCheckedGuard scGuard2 = new StaticallyCheckedGuard("grd2", "p \u2264 0 \u21d4 \u00ac p < 2");
		final StaticallyCheckedEvent scEvent = new StaticallyCheckedEvent("event-1", Arrays.asList(scParameter), Arrays.asList(scGuard1, scGuard2)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(new ArrayList<>(), Arrays.asList(scEvent));
		final ConditionsExtractor conditionsExtractor = new ConditionsExtractor(scMachine);
		
		assertEquals(conditionsExtractor.conditions.conditions_order.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions_order.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").size(), 2);
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(0), "grd1/1");
		assertEquals(conditionsExtractor.conditions.conditions_order.get("event-1").get(1), "grd2/1");
		
		assertEquals(conditionsExtractor.conditions.conditions.size(), 1);
		assertTrue(conditionsExtractor.conditions.conditions.containsKey("event-1"));
		assertEquals(conditionsExtractor.conditions.conditions.get("event-1").size(), 2);

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd1/1"));
		final Condition c1 = conditionsExtractor.conditions.conditions.get("event-1").get("grd1/1");
		assertEquals(c1.id, "grd1/1");
	       	assertEquals(c1.predicate.toString(), "0<p");

		assertTrue(conditionsExtractor.conditions.conditions.get("event-1").containsKey("grd2/1"));
		final Condition c2 = conditionsExtractor.conditions.conditions.get("event-1").get("grd2/1");
		assertEquals(c2.id, "grd2/1");
	       	assertEquals(c2.predicate.toString(), "p<2");
	}

	@Test
	public void guardsConditions()
	{
		final StaticallyCheckedParameter scParameter = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard scGuard1 = new StaticallyCheckedGuard("grd1", "0 < p");
		final StaticallyCheckedGuard scGuard2 = new StaticallyCheckedGuard("grd2", "p \u2264 0 \u21d4 \u00ac p < 2");
		final StaticallyCheckedEvent scEvent = new StaticallyCheckedEvent("event-1", Arrays.asList(scParameter), Arrays.asList(scGuard1, scGuard2)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(new ArrayList<>(), Arrays.asList(scEvent));
		final ConditionsExtractor conditionsExtractor = new ConditionsExtractor(scMachine);

		assertEquals(conditionsExtractor.guardsConditions.conditions_order.size(), 2);
		assertTrue(conditionsExtractor.guardsConditions.conditions_order.containsKey("event-1/grd1"));
		assertTrue(conditionsExtractor.guardsConditions.conditions_order.containsKey("event-1/grd2"));

		assertEquals(conditionsExtractor.guardsConditions.conditions_order.get("event-1/grd1").size(), 1);
		assertEquals(conditionsExtractor.guardsConditions.conditions_order.get("event-1/grd1").get(0), "grd1/1");

		assertEquals(conditionsExtractor.guardsConditions.conditions_order.get("event-1/grd2").size(), 2);
		assertEquals(conditionsExtractor.guardsConditions.conditions_order.get("event-1/grd2").get(0), "grd1/1");
		assertEquals(conditionsExtractor.guardsConditions.conditions_order.get("event-1/grd2").get(1), "grd2/1");
	}
}
