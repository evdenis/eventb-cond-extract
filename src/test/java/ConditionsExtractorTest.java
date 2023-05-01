import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import ru.ispras.eventb_cond_extract.*;

public class ConditionsExtractorTest
{
	static String INT = "\u2124"; //Z

	@Test
	public void oneCondition()
	{
		final StaticallyCheckedParameter p = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard grd1 = new StaticallyCheckedGuard("grd1", "p > 0");
		final StaticallyCheckedEvent event1 = new StaticallyCheckedEvent("event1", Arrays.asList(p), Arrays.asList(grd1)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(
				new ArrayList<>(), new ArrayList<>(), Arrays.asList(event1));
		final MachineConditions machineConditions = new ConditionsExtractor(scMachine).machineConditions;
		
		assertEquals(machineConditions.eventsConditions.size(), 1);
		assertTrue(machineConditions.eventsConditions.containsKey("event1"));

		final List<Condition> conditions = machineConditions.eventsConditions.get("event1").values;
		assertEquals(1, conditions.size());
		assertEquals("event1/grd1/1", conditions.get(0).id);
		assertEquals("p>0", conditions.get(0).predicate.toString());
		assertEquals("0<p", conditions.get(0).normalizedPredicate);
		assertFalse(conditions.get(0).isConst);
	}

	@Test
	public void setTheoryCondition()
	{
		final StaticallyCheckedVariable scVariable = new StaticallyCheckedVariable("Processes", "\u2119(\u2124)"); // POW(INT)
		final StaticallyCheckedParameter p = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard grd = new StaticallyCheckedGuard("grd1", "p \u2208 Processes");
		final StaticallyCheckedEvent event1 = new StaticallyCheckedEvent("event1", Arrays.asList(p), Arrays.asList(grd)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(
				Arrays.asList(scVariable), new ArrayList<>(), Arrays.asList(event1));
		final MachineConditions machineConditions = new ConditionsExtractor(scMachine).machineConditions;

		assertEquals(1, machineConditions.eventsConditions.size());
		assertTrue(machineConditions.eventsConditions.containsKey("event1"));
		final List<Condition> conditions = machineConditions.eventsConditions.get("event1").values;
		assertEquals(1, conditions.size());
		assertEquals("p\u2208Processes", conditions.get(0).predicate.toString());
		assertFalse(conditions.get(0).isConst);
	}

	@Test
	public void splitCondition()
	{
		final StaticallyCheckedParameter p = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard grd1 = new StaticallyCheckedGuard("grd1", "(p > 0) \u2227 (p < 1 \u2228 3 = p)");
		final StaticallyCheckedEvent event1 = new StaticallyCheckedEvent("event1", Arrays.asList(p), Arrays.asList(grd1)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(
				new ArrayList<>(), new ArrayList<>(), Arrays.asList(event1));
		final MachineConditions machineConditions = new ConditionsExtractor(scMachine).machineConditions;
		final List<Condition> conditions = machineConditions.eventsConditions.get("event1").values;
		assertEquals(3, conditions.size());

		assertEquals("event1/grd1/1", conditions.get(0).id);
		assertEquals("p>0", conditions.get(0).predicate.toString());

		assertEquals("event1/grd1/2", conditions.get(1).id);
		assertEquals("p<1", conditions.get(1).predicate.toString());

		assertEquals("event1/grd1/3", conditions.get(2).id);
		assertEquals("3=p", conditions.get(2).predicate.toString());
	}

	@Test
	public void severalGuards()
	{
		final StaticallyCheckedParameter p = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard grd1 = new StaticallyCheckedGuard("grd1", "p > 0");
		final StaticallyCheckedGuard grd2 = new StaticallyCheckedGuard("grd2", "p < 2");
		final StaticallyCheckedEvent event1 = new StaticallyCheckedEvent("event1", Arrays.asList(p), Arrays.asList(grd1, grd2)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(
				new ArrayList<>(), new ArrayList<>(), Arrays.asList(event1));
		final MachineConditions machineConditions = new ConditionsExtractor(scMachine).machineConditions;
		final List<Condition> conditions = machineConditions.eventsConditions.get("event1").values;
		
		assertEquals(2, conditions.size());
		assertEquals("event1/grd1/1", conditions.get(0).id);
		assertEquals("p>0", conditions.get(0).predicate.toString());
		assertFalse(conditions.get(0).isConst);
		assertEquals("event1/grd2/1", conditions.get(1).id);
		assertEquals("p<2", conditions.get(1).predicate.toString());
		assertFalse(conditions.get(1).isConst);
	}

	@Test
	public void equalConditions()
	{
		final StaticallyCheckedParameter p = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard grd1 = new StaticallyCheckedGuard("grd1", "p > 0");
		final StaticallyCheckedGuard grd2 = new StaticallyCheckedGuard("grd2", "0 < p \u2227 p < 2");
		final StaticallyCheckedEvent event1 = new StaticallyCheckedEvent("event1", Arrays.asList(p), Arrays.asList(grd1, grd2)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(
				new ArrayList<>(), new ArrayList<>(), Arrays.asList(event1));
		final MachineConditions machineConditions = new ConditionsExtractor(scMachine).machineConditions;
		final List<Condition> conditions = machineConditions.eventsConditions.get("event1").values;

		assertEquals(3, conditions.size());

		assertEquals("event1/grd1/1", conditions.get(0).id);
		assertEquals("p>0", conditions.get(0).predicate.toString());
		assertEquals("0<p", conditions.get(0).normalizedPredicate);
		assertFalse(conditions.get(0).isConst);

		assertEquals("event1/grd2/1", conditions.get(1).id);
		assertEquals("0<p", conditions.get(1).predicate.toString());
		assertEquals(conditions.get(1).normalizedPredicate, conditions.get(0).normalizedPredicate);
		assertFalse(conditions.get(1).isConst);

		assertEquals("event1/grd2/2", conditions.get(2).id);
		assertEquals("p<2", conditions.get(2).predicate.toString());
		assertEquals("2\u2264p", conditions.get(2).normalizedPredicate);
		assertFalse(conditions.get(2).isConst);
	}

	@Test
	public void identicalConditions1()
	{
		final StaticallyCheckedParameter p = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard grd1 = new StaticallyCheckedGuard("grd1", "p > 0");
		final StaticallyCheckedGuard grd2 = new StaticallyCheckedGuard("grd2", "(p \u2264 0) \u21d2 p < 2");
		final StaticallyCheckedEvent event1 = new StaticallyCheckedEvent("event-1", Arrays.asList(p), Arrays.asList(grd1, grd2)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(
				new ArrayList<>(), new ArrayList<>(), Arrays.asList(event1));
		final MachineConditions machineConditions = new ConditionsExtractor(scMachine).machineConditions;
		final List<Condition> conditions = machineConditions.eventsConditions.get("event-1").values;
		
		assertEquals(3, conditions.size(), 3);
		assertEquals("event-1/grd1/1", conditions.get(0).id);
		assertEquals("p>0", conditions.get(0).predicate.toString());
		assertFalse(conditions.get(0).isConst);

		assertEquals("event-1/grd2/1", conditions.get(1).id);
		assertEquals("p\u22640", conditions.get(1).predicate.toString());
		assertEquals(conditions.get(1).normalizedPredicate, conditions.get(0).normalizedPredicate);
		assertFalse(conditions.get(1).isConst);

		assertEquals("event-1/grd2/2", conditions.get(2).id);
		assertEquals("p<2", conditions.get(2).predicate.toString());
		assertEquals("2\u2264p", conditions.get(2).normalizedPredicate);
		assertFalse(conditions.get(2).isConst);
	}

	@Test
	public void identicalConditions2()
	{
		final StaticallyCheckedParameter p = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard grd1 = new StaticallyCheckedGuard("grd1", "0 < p");
		final StaticallyCheckedGuard grd2 = new StaticallyCheckedGuard("grd2", "p \u2264 0 \u21d4 \u00ac p < 2");
		final StaticallyCheckedEvent event1 = new StaticallyCheckedEvent("event-1", Arrays.asList(p), Arrays.asList(grd1, grd2)); 
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(
				new ArrayList<>(), new ArrayList<>(), Arrays.asList(event1));
		final MachineConditions machineConditions = new ConditionsExtractor(scMachine).machineConditions;
		final List<Condition> conditions = machineConditions.eventsConditions.get("event-1").values;

		assertEquals(3, conditions.size());
		assertEquals("event-1/grd1/1", conditions.get(0).id);
		assertEquals("0<p", conditions.get(0).predicate.toString());
		assertEquals("0<p", conditions.get(0).normalizedPredicate);
		assertFalse(conditions.get(0).isConst);

		assertEquals("event-1/grd2/1", conditions.get(1).id);
		assertEquals("p\u22640", conditions.get(1).predicate.toString());
		assertEquals(conditions.get(1).normalizedPredicate, conditions.get(0).normalizedPredicate);
		assertFalse(conditions.get(1).isConst);

		assertEquals("event-1/grd2/2", conditions.get(2).id);
		assertEquals("p<2", conditions.get(2).predicate.toString());
		assertEquals("2\u2264p", conditions.get(2).normalizedPredicate);
		assertFalse(conditions.get(2).isConst);
	}

	@Test
	public void invariantsConditions()
	{
		final StaticallyCheckedVariable p = new StaticallyCheckedVariable("p", INT);
		final StaticallyCheckedInvariant inv1 = new StaticallyCheckedInvariant("inv1", "0 < p");
		final StaticallyCheckedInvariant inv2 = new StaticallyCheckedInvariant("inv2", "p \u2264 0 \u21d4 \u00ac p < 2");
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(
				Arrays.asList(p), Arrays.asList(inv1, inv2), new ArrayList<>());
		final MachineConditions machineConditions = new ConditionsExtractor(scMachine).machineConditions;
		assertEquals(2, machineConditions.invariantsConditions.size());
		assertTrue(machineConditions.invariantsConditions.containsKey("inv1"));
		assertTrue(machineConditions.invariantsConditions.containsKey("inv2"));

		final List<Condition> conditions1 = machineConditions.invariantsConditions.get("inv1").values;
		assertEquals(1, conditions1.size());
		assertEquals("inv1/1", conditions1.get(0).id);
		assertEquals("0<p", conditions1.get(0).predicate.toString());
		assertEquals("0<p", conditions1.get(0).normalizedPredicate);
		assertFalse(conditions1.get(0).isConst);

		final List<Condition> conditions2 = machineConditions.invariantsConditions.get("inv2").values;
		assertEquals("inv2/1", conditions2.get(0).id);
		assertEquals("p\u22640", conditions2.get(0).predicate.toString());
		assertEquals(conditions2.get(0).normalizedPredicate, conditions1.get(0).normalizedPredicate);
		assertFalse(conditions2.get(0).isConst);

		assertEquals("inv2/2", conditions2.get(1).id);
		assertEquals("p<2", conditions2.get(1).predicate.toString());
		assertEquals("2\u2264p", conditions2.get(1).normalizedPredicate);
		assertFalse(conditions2.get(1).isConst);
	}

	@Test
	public void constEventConditions()
	{
		final StaticallyCheckedVariable v = new StaticallyCheckedVariable("v", INT);
		final StaticallyCheckedParameter p = new StaticallyCheckedParameter("p", INT);
		final StaticallyCheckedGuard grd = new StaticallyCheckedGuard("grd", "1 < A \u21d2 A < p \u2227 v < 2");
		final StaticallyCheckedEvent event = new StaticallyCheckedEvent("event", Arrays.asList(p), Arrays.asList(grd));
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(
				Arrays.asList(v), new ArrayList<>(), Arrays.asList(event));

		final MachineConditions machineConditions = new ConditionsExtractor(scMachine).machineConditions;
		final List<Condition> conditions = machineConditions.eventsConditions.get("event").values;
		assertEquals(3, conditions.size());

		final Condition grd_1 = conditions.get(0);
		assertEquals("event/grd/1", grd_1.id);
		assertEquals("1<A", grd_1.predicate.toString());
		assertEquals("1<A", grd_1.normalizedPredicate);
		assertTrue(grd_1.isConst);

		final Condition grd_2 = conditions.get(1);
		assertEquals("event/grd/2", grd_2.id);
		assertEquals("A<p", grd_2.predicate.toString());
		assertEquals("A<p", grd_2.normalizedPredicate);
		assertTrue(!grd_2.isConst);

		final Condition grd_3 = conditions.get(2);
		assertEquals("event/grd/3", grd_3.id);
		assertEquals("v<2", grd_3.predicate.toString());
		assertEquals("2\u2264v", grd_3.normalizedPredicate);
		assertTrue(!grd_3.isConst);
	}

	@Test
	public void constInvariantConditions()
	{
		final StaticallyCheckedVariable p = new StaticallyCheckedVariable("p", INT);
		final StaticallyCheckedInvariant inv = new StaticallyCheckedInvariant("inv", "1 < A \u21d2 A < p");
		final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachine(
				Arrays.asList(p), Arrays.asList(inv), new ArrayList<>());

		final MachineConditions machineConditions = new ConditionsExtractor(scMachine).machineConditions;
		assertEquals(1, machineConditions.invariantsConditions.size());
		assertTrue(machineConditions.invariantsConditions.containsKey("inv"));

		final List<Condition> conditions = machineConditions.invariantsConditions.get("inv").values;
		assertEquals(2, conditions.size());

		final Condition inv_1 = conditions.get(0);
		assertEquals("inv/1", inv_1.id);
		assertEquals("1<A", inv_1.predicate.toString());
		assertEquals("1<A", inv_1.normalizedPredicate);
		assertTrue(inv_1.isConst);

		final Condition inv_2 = conditions.get(1);
		assertEquals("inv/2", inv_2.id);
		assertEquals("A<p", inv_2.predicate.toString());
		assertEquals("A<p", inv_2.normalizedPredicate);
		assertTrue(!inv_2.isConst);
	}
}
