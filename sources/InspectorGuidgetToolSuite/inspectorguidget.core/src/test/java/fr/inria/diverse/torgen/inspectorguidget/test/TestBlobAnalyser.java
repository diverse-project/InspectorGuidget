package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.analyser.BlobListenerAnalyser;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestBlobAnalyser {
	private BlobListenerAnalyser analyser;

	@Before
	public void setUp() {
		analyser = new BlobListenerAnalyser();
	}

	@Test
	public void testStdBlob() {
		analyser.getCmdAnalyser().addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfReturn.java");
		analyser.run();

		assertEquals(1, analyser.getBlobs().entrySet().size());
		assertEquals(3, analyser.getBlobs().entrySet().stream().collect(Collectors.toList()).get(0).getValue().size());
	}

	@Test
	public void testNotABlob() {
		analyser.getCmdAnalyser().addInputResource("src/test/resources/java/analysers/ActionListenerCondFragmentedCommand.java");
		analyser.run();

		assertTrue(analyser.getBlobs().entrySet().isEmpty());
	}

	@Test
	public void testStdBlobSetNBCMD2() {
		BlobListenerAnalyser.setNbCmdBlobs(2);
		analyser.getCmdAnalyser().addInputResource("src/test/resources/java/analysers/ActionListenerCondFragmentedCommand.java");
		analyser.run();

		assertEquals(1, analyser.getBlobs().entrySet().size());
		assertEquals(2, analyser.getBlobs().entrySet().stream().collect(Collectors.toList()).get(0).getValue().size());
	}

	@Test
	public void testNotABlobSetNBCMD2() {
		BlobListenerAnalyser.setNbCmdBlobs(2);
		analyser.getCmdAnalyser().addInputResource("src/test/resources/java/analysers/ActionListenerCondOneInstanceOf.java");
		analyser.run();

		assertTrue(analyser.getBlobs().entrySet().isEmpty());
	}

	@Test
	public void testNbCmdCannotBeZero() {
		BlobListenerAnalyser.setNbCmdBlobs(0);
		analyser.getCmdAnalyser().addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfReturn.java");
		analyser.run();

		assertEquals(1, analyser.getBlobs().entrySet().size());
		assertEquals(3, analyser.getBlobs().entrySet().stream().collect(Collectors.toList()).get(0).getValue().size());
	}
}
